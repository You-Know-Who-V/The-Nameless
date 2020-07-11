package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    EditText currentPasswordEditText,newPasswordEditText,confirmPasswordEditText;
    FirebaseUser user;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        user= FirebaseAuth.getInstance().getCurrentUser();

        currentPasswordEditText=findViewById(R.id.currentTextPassword);
        newPasswordEditText=findViewById(R.id.newTextPassword);
        confirmPasswordEditText=findViewById(R.id.confirmTextPassword);

        progressBar=findViewById(R.id.progressBar);
    }

    public void changePassword(View view)
    {
        final String currentPass=currentPasswordEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(currentPass))
        {
            user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(),currentPass))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                updatePassword();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePassword.this,"Please Enter Correct Password",Toast.LENGTH_LONG).show();
                            currentPasswordEditText.setText("");
                            newPasswordEditText.setText("");
                            confirmPasswordEditText.setText("");
                        }
                    });
        }
        else
        {
            Toast.makeText(ChangePassword.this, "Kindly Enter Current Password!!", Toast.LENGTH_SHORT).show();
            newPasswordEditText.setText("");
            confirmPasswordEditText.setText("");
        }
    }

    public void updatePassword()
    {
        if(!TextUtils.isEmpty(newPasswordEditText.getText().toString().trim())
            &&!TextUtils.isEmpty(confirmPasswordEditText.getText().toString().trim()))
        {
            if(!newPasswordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())){
                Toast.makeText(this, "Passwords are not matching!", Toast.LENGTH_SHORT).show();
                newPasswordEditText.setText("");
                confirmPasswordEditText.setText("");
            }
            else if(currentPasswordEditText.getText().toString().trim().equals(newPasswordEditText.getText().toString().trim()))
            {
                Toast.makeText(ChangePassword.this,"New Password and Current Password cannot be Same",Toast.LENGTH_LONG).show();
                newPasswordEditText.setText("");
                confirmPasswordEditText.setText("");
            }
            else{
                progressBar.setVisibility(View.VISIBLE);
                user.updatePassword(newPasswordEditText.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(ChangePassword.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                    newPasswordEditText.setText("");
                                    confirmPasswordEditText.setText("");
                                    currentPasswordEditText.setText("");
                                }
                                else
                                {
                                    Toast.makeText(ChangePassword.this, "Password Updation Failed!!!:(", Toast.LENGTH_LONG).show();
                                    newPasswordEditText.setText("");
                                    confirmPasswordEditText.setText("");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePassword.this, "Password Updation Failed!!!:(", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
        else
        {
            Toast.makeText(ChangePassword.this, "New Password Field cannot not be Empty", Toast.LENGTH_SHORT).show();
        }
    }
}