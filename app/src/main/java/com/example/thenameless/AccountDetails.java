package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountDetails extends AppCompatActivity {

    EditText nameEditText,phone_noEditText,emailEditText,clgEmailEditText,semEditText;
    ProgressBar progressBar;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        nameEditText=findViewById(R.id.name_editText);
        phone_noEditText=findViewById(R.id.phone_no_editText);
        emailEditText=findViewById(R.id.email_editText);
        clgEmailEditText=findViewById(R.id.clg_emailEditText);
        phone_noEditText=findViewById(R.id.phone_no_editText);
        semEditText=findViewById(R.id.sem_editText);

        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        currentUser=MainActivity.mAuth.getCurrentUser();
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
                                phone_noEditText.setText((String) document.get("PhoneNo"));
                                semEditText.setText((String) document.get("Sem"));
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
                && !TextUtils.isEmpty(phone_noEditText.getText().toString().trim())
                && !TextUtils.isEmpty(semEditText.getText().toString().trim()))
        {
            if(phone_noEditText.getText().toString().length()==10 )
            {
                if(Integer.parseInt(semEditText.getText().toString()) >=1 && Integer.parseInt(semEditText.getText().toString())<=8)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    updateCollection();
                }
                else
                {
                    Toast.makeText(AccountDetails.this,"Sem Should be Between 1 and 8 (Both Inclusive)",Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(AccountDetails.this,"Phone Number should contain 10 Digits!!!",Toast.LENGTH_LONG).show();
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
        userDetails.put("PhoneNo",phone_noEditText.getText().toString());
        userDetails.put("Sem",semEditText.getText().toString());

        db.collection("AccountDetails").document(currentUser.getUid())
                .set(userDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AccountDetails.this, "Details Updated Successfully!!!!!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                        startActivity(new Intent(AccountDetails.this,HomePage.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountDetails.this,"There was some error!!! Please Try again later.",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}