package com.example.thenameless;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private TextView signUpButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                break;
            case R.id.login_signUp_Text:
                //goto signUp activity

                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                //finish();
                break;
        }

    }
}