package com.example.thenameless;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private TextView signUpButton;
    private ProgressBar progressBar;
    static FirebaseAuth mAuth;
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

        signUpButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
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
        }

    }

    private void accountLogIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.INVISIBLE);

                            startActivity(new Intent(LoginActivity.this, HomePage.class));
                        } else {

                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(LoginActivity.this, "Login Failed!!! Try Again", Toast.LENGTH_SHORT).show();

                            passwordEditText.setText("");
                        }
                    }
                });
    }
}