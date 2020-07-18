package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.Manifest;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.bumptech.glide.Glide;
import com.example.thenameless.model.Namelesser;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AccountDetails extends AppCompatActivity {

    private static final String TAG = "Account Details";
    private EditText nameEditText,emailEditText,clgEmailEditText,semEditText;
    private ImageView profileImage;
    private String imageUrl = "";
    private Button verifyButton;
    private static final int GET_IMAGE_CODE = 1111;

    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar profileImageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        mAuth = FirebaseAuth.getInstance();

        profileImageProgressBar = findViewById(R.id.detail_imageProgressBar);
        nameEditText=findViewById(R.id.name_editText);
        emailEditText=findViewById(R.id.email_editText);
        clgEmailEditText=findViewById(R.id.clg_emailEditText);
        semEditText=findViewById(R.id.sem_editText);
        verifyButton = findViewById(R.id.details_verify_button);
        profileImage=findViewById(R.id.profile_image);

        if(Namelesser.getInstance().getUserNumber() != null) {
            verifyButton.setText("Change Verified Phone Number");
        }

        currentUser=mAuth.getCurrentUser();
        int type=Integer.parseInt(getIntent().getStringExtra("type"));
        if(type==1)
        {
            //new user
            nameEditText.setText(getIntent().getStringExtra("name"));
            emailEditText.setText(getIntent().getStringExtra("email"));
        }
        else
        {
            //already existing user
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
            return true;
        }
        else if(item.getItemId()== R.id.logout)
        {
            MainActivity.mAuth.signOut();
            finish();
            startActivity(new Intent(AccountDetails.this, LoginActivity.class));
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
                                clgEmailEditText.setText((String) document.get("ClgEmail"));
                                semEditText.setText((String) document.get("Sem"));
                                Picasso.get()
                                        .load((String) document.get("ProfileImg"))
                                        .placeholder(R.drawable.cool_backgrounds)
                                        .into(profileImage);
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
                        Toast.makeText(AccountDetails.this,"Unable to retrieve Information  :(",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateInfo(View view)
    {
        if(!TextUtils.isEmpty(nameEditText.getText().toString().trim())
                && !TextUtils.isEmpty(emailEditText.getText().toString().trim())
                && !TextUtils.isEmpty(clgEmailEditText.getText().toString().trim())
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
        userDetails.put("ClgEmail",clgEmailEditText.getText().toString());
        userDetails.put("PhoneNo",Namelesser.getInstance().getUserNumber());
        userDetails.put("Sem",semEditText.getText().toString());
        userDetails.put("ProfileImg",imageUrl);
        Toast.makeText(this, imageUrl, Toast.LENGTH_SHORT).show();

        db.collection("AccountDetails").document(currentUser.getUid())
                .set(userDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AccountDetails.this, "Details Updated Successfully!!!!!", Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.INVISIBLE);

                        startActivity(new Intent(AccountDetails.this,HomePage.class));
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
                                imageUrl+=uri.toString();
                                Log.d("Img url",imageUrl);
                                Toast.makeText(AccountDetails.this, "Profile Pic Updated Successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AccountDetails.this,"Unable to change the Profile Picture",Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void verifyPhoneNumber(View view) {

        startActivity(new Intent(AccountDetails.this, EnterPhoneNumber.class));



    }

}