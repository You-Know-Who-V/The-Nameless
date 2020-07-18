package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumber extends AppCompatActivity {

    private Button verifyButton;
    private EditText codeEditText;
    private ProgressBar progressBar;

    private String phoneNumber;

    private FirebaseAuth mAuth;

    private String verificationId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        mAuth = FirebaseAuth.getInstance();

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phoneNumber");

        codeEditText = findViewById(R.id.verify_code_editText);
        verifyButton = findViewById(R.id.verify_button);
        progressBar = findViewById(R.id.verify_progressBar);

        sendVerificationCode(phoneNumber);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(codeEditText.getText().toString().trim())
                        && codeEditText.getText().toString().trim().length() == 6 ) {
                    verifyCode(codeEditText.getText().toString().trim());
                }
                else {
                    Toast.makeText(VerifyPhoneNumber.this, "Enter code!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void verifyCode(String code) {

        if(verificationId == code) {

            Namelesser namelesser = Namelesser.getInstance();

            namelesser.setUserNumber(phoneNumber);

            Intent intent = new Intent(VerifyPhoneNumber.this, AccountDetails.class);

            intent.putExtra("type", "2");

            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "Enter correct code!", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendVerificationCode(String phoneNumber) {

        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                        90, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId += s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            codeEditText.setText(code);

            Namelesser namelesser = Namelesser.getInstance();

            namelesser.setUserNumber(phoneNumber);

            Intent intent = new Intent(VerifyPhoneNumber.this, AccountDetails.class);

            intent.putExtra("type", "2");

            startActivity(intent);
            finish();

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(VerifyPhoneNumber.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };
}