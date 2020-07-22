package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AccountDetails extends AppCompatActivity {

    private static final String TAG = "Account Details";
    private EditText nameEditText,emailEditText,branchEditText,semEditText;
    private ImageView profileImage;
    private String imageUrl;
    private Button verifyButton, updateButton;
    private static final int GET_IMAGE_CODE = 1111;

    int type;

    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar profileImageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        setTitle("Account Details");
        mAuth = FirebaseAuth.getInstance();

        profileImageProgressBar = findViewById(R.id.detail_imageProgressBar);
        nameEditText=findViewById(R.id.name_editText);
        emailEditText=findViewById(R.id.email_editText);
        branchEditText=findViewById(R.id.branchEditText);
        semEditText=findViewById(R.id.sem_editText);
        updateButton = findViewById(R.id.detail_update_button);
        verifyButton = findViewById(R.id.details_verify_button);
        profileImage=findViewById(R.id.profile_image);

        if(Namelesser.getInstance().getUserNumber() != null) {
            verifyButton.setText("Change Verified Phone Number");
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });

        currentUser=mAuth.getCurrentUser();
        type=Integer.parseInt(getIntent().getStringExtra("type"));
        if(type==1)
        {
            //new user
            nameEditText.setText(getIntent().getStringExtra("name"));
            emailEditText.setText(getIntent().getStringExtra("email"));
        }
        else if (type == 3)
        {
            getDetails();
            //Toast.makeText(this, "Don't forget to Update details!", Toast.LENGTH_SHORT).show();
            updateInfo();
        }
        else {
            getDetails();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_details_menu,menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.change_pass)
        {
            startActivity(new Intent(AccountDetails.this,ChangePassword.class));
            finish();
            return true;
        }
        else if(item.getItemId()== R.id.logout)
        {

            Namelesser.getInstance().setUserMail(null);
            Namelesser.getInstance().setUserName(null);
            Namelesser.getInstance().setUserId(null);
            Namelesser.getInstance().setUserNumber(null);
            MainActivity.mAuth.signOut();
            finish();
            startActivity(new Intent(AccountDetails.this, LoginActivity.class));
            finish();
            return true;
        }
        return false;
    }

    public void getDetails()
    {
        db.collection("AccountDetails").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                nameEditText.setText((String) document.get("Name"));
                                emailEditText.setText((String) document.get("EMail"));
                                branchEditText.setText((String)document.get("Branch"));
                                semEditText.setText((String) document.get("Sem"));
                                imageUrl=(String) document.get("ProfileImg");
                                if((String) document.get("ProfileImg") != "")
                                Picasso.get()
                                        .load((String) document.get("ProfileImg"))
                                        .placeholder(R.drawable.cool_backgrounds)
                                        .into(profileImage);
                                if(type == 3) {
                                    updateInfo();
                                }
                            } else {
                                Log.d("Info", "No such document");
                            }
                        } else {
                            Log.d("Info", "get failed with ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountDetails.this,"Unable to retrieve Information :(",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateInfo()
    {
        if(!TextUtils.isEmpty(nameEditText.getText().toString().trim())
                && !TextUtils.isEmpty(emailEditText.getText().toString().trim())
                && !TextUtils.isEmpty(branchEditText.getText().toString().trim())
                && !TextUtils.isEmpty(semEditText.getText().toString().trim()))
        {

                if(Integer.parseInt(semEditText.getText().toString()) >=1 && Integer.parseInt(semEditText.getText().toString())<=8)
                {
                    //progressBar.setVisibility(View.VISIBLE);
                    updateCollection();
                }
                else
                {
                    Toast.makeText(AccountDetails.this,"Sem Should be Between 1 and 8 (Both Inclusive)",Toast.LENGTH_LONG).show();
                }

        }
        else
        {
            Toast.makeText(AccountDetails.this, "Empty Fields are not Allowed!!!!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateCollection()
    {
        Map<String,String > userDetails=new HashMap<>();
        userDetails.put("Name",nameEditText.getText().toString());
        userDetails.put("EMail",emailEditText.getText().toString());
        userDetails.put("Branch",branchEditText.getText().toString());
        userDetails.put("PhoneNo",Namelesser.getInstance().getUserNumber());
        userDetails.put("Sem",semEditText.getText().toString());
        userDetails.put("ProfileImg",imageUrl);
        //Toast.makeText(this, imageUrl, Toast.LENGTH_SHORT).show();

        db.collection("AccountDetails").document(currentUser.getUid())
                .set(userDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AccountDetails.this, "Details Updated Successfully!!!!!", Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(AccountDetails.this, HomePage.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountDetails.this,"There was some error!!! Please Try again later.",Toast.LENGTH_LONG).show();
//                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public void getPhoto()
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GET_IMAGE_CODE);
        //finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void changeImage(View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            profileImageProgressBar.setVisibility(View.VISIBLE);
            getPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GET_IMAGE_CODE &&  resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                Uri imageUri = data.getData();

                //update Storage
                addImagetoStorage(imageUri);
            }
        }
    }

    private void addImagetoStorage(Uri imageUri)
    {
        final StorageReference storageRef=FirebaseStorage.getInstance().getReference().child("ProfilePic")
                .child(""+currentUser.getUid()+ Timestamp.now().getSeconds());

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl=uri.toString();
                                Log.d("Img url",imageUrl);
                                Toast.makeText(AccountDetails.this, "Profile Pic Updated Successfully!", Toast.LENGTH_SHORT).show();
//
                                Picasso.get()
                                        .load(imageUrl)
                                        .placeholder(R.drawable.cool_backgrounds)
                                        .into(profileImage);
                                profileImageProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileImageProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AccountDetails.this,"Unable to change the Profile Picture!",Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void verifyPhoneNumber(View view) {

        startActivity(new Intent(AccountDetails.this, EnterPhoneNumber.class));

        finish();



    }

}