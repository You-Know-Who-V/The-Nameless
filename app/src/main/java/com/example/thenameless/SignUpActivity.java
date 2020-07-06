package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUp Activity";
    private EditText nameEditText,emailEditText,passwordEditText,confirmPassword;
    private Button signUpButton;
    private TextView alreadyMember;
    private ProgressBar progressBar;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        firebaseAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.signUp_name_editText);
        emailEditText = findViewById(R.id.signUp_email_editText);
        passwordEditText = findViewById(R.id.signUp_password_editText);
        confirmPassword = findViewById(R.id.signUp_confirmPassword_editText);
        signUpButton = findViewById(R.id.signUp_signUp_button);
        alreadyMember = findViewById(R.id.signUp_alreadyMember_text);
        progressBar = findViewById(R.id.signUp_progressBar);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){

                }
                else{

                }
            }
        };

        signUpButton.setOnClickListener(this);
        alreadyMember.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signUp_signUp_button:
                //make new account

                if(!TextUtils.isEmpty(nameEditText.getText().toString().trim())
                        && !TextUtils.isEmpty(emailEditText.getText().toString().trim())
                        && !TextUtils.isEmpty(passwordEditText.getText().toString().trim())
                        && !TextUtils.isEmpty(confirmPassword.getText().toString().trim())){

                    if(!passwordEditText.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){
                        Toast.makeText(this, "Passwords are not matching!", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        progressBar.setVisibility(View.VISIBLE);

                        createNewAccount(nameEditText.getText().toString().trim(),
                                emailEditText.getText().toString().trim(),
                                passwordEditText.getText().toString().trim());
                    }
                }
                else{
                    Toast.makeText(this, "Empty Fields Not Allowed!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.signUp_alreadyMember_text:
                //goto login activity
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                //finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser != null && firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void createNewAccount(final String name, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            
                            currentUser = firebaseAuth.getCurrentUser();
                            
                            assert currentUser != null;
                            currentUserId = currentUser.getUid();
                            currentUserName = name;

                            Map<String, String> userObj = new HashMap<>();

                            userObj.put("userName", currentUserName);
                            userObj.put("userId", currentUser.getUid());

                            db.collection("Users").add(userObj)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                        progressBar.setVisibility(View.INVISIBLE);

                                                        Namelesser namelesser = Namelesser.getInstance();

                                                        namelesser.setUserName(currentUserName);
                                                        namelesser.setUserId(currentUserId);

                                                        startActivity(new Intent(SignUpActivity.this, HomePage.class));



                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                                }
                            });

                            startActivity(new Intent(SignUpActivity.this, HomePage.class));
                            //finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.INVISIBLE);

                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
}