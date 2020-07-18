package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 11;
    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private Button signInButton, signInWithPhone;
    private TextView signUpButton;
    private ProgressBar progressBar;
    private SignInButton signInButtonGoogle;

    static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.login_email_editText);
        passwordEditText = findViewById(R.id.login_password_editText);
        signInButton = findViewById(R.id.login_signIn_button);
        signUpButton = findViewById(R.id.login_signUp_Text);
        progressBar = findViewById(R.id.login_progressBar);
        signInButtonGoogle = findViewById(R.id.login_googleSignIn_button);
        signInWithPhone = findViewById(R.id.login_signInWithPhone_button);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){

                    //startActivity(new Intent(LoginActivity.this, HomePage.class));

                }
                else{

                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signUpButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        signInWithPhone.setOnClickListener(this);
        signInButtonGoogle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.login_signIn_button:
                //login into acc.
                if(!TextUtils.isEmpty(emailEditText.getText().toString().trim())
                        && !TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {

                    progressBar.setVisibility(View.VISIBLE);

                    accountLogIn(emailEditText.getText().toString().trim(),
                            passwordEditText.getText().toString().trim());
                }
                else{
                    Toast.makeText(this, "Empty Fields Not Allowed!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.login_signUp_Text:
                //goto signUp activity
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                //finish();
                break;
            case R.id.login_googleSignIn_button:
                progressBar.setVisibility(View.VISIBLE);
                signIn();
                break;
            case R.id.login_signInWithPhone_button:
                startActivity(new Intent(LoginActivity.this, EnterPhoneNumber.class));
                break;
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void accountLogIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.INVISIBLE);

                            currentUser = mAuth.getCurrentUser();

                            collectionReference.whereEqualTo("userId",currentUser.getUid())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                            Namelesser namelesser = Namelesser.getInstance();

                                            for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){

                                                namelesser.setUserId(currentUser.getUid());
                                                namelesser.setUserName(snapshot.getString("userName"));

                                            }
                                            //Toast.makeText(LoginActivity.this, "Username: "+ namelesser.getUserName(), Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(LoginActivity.this, HomePage.class));
                                        }
                                    });
                        } else {

                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(LoginActivity.this, "Login Failed!!! Try Again", Toast.LENGTH_SHORT).show();

                            passwordEditText.setText("");
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignINResult(task);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Result Not Ok!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignINResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);

        } catch (ApiException e) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            currentUser = mAuth.getCurrentUser();
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                            if(account != null) {
                                Namelesser namelesser = Namelesser.getInstance();
                                namelesser.setUserId(currentUser.getUid());
                                namelesser.setUserName(account.getDisplayName());
                                Toast.makeText(LoginActivity.this, account.getDisplayName(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomePage.class));
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Sign IN Unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
