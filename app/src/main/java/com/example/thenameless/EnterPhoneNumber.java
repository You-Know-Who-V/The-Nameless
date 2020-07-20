package com.example.thenameless;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EnterPhoneNumber extends AppCompatActivity {

    private Button nextButton;
    private EditText phoneNumber, countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);

        nextButton = findViewById(R.id.enter_next_button);
        phoneNumber = findViewById(R.id.phoneNumber_editText);
        countryCode = findViewById(R.id.countryCode);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(phoneNumber.getText().toString().trim()) ||
                        phoneNumber.getText().toString().trim().length() != 10 ||
                        TextUtils.isEmpty(countryCode.getText().toString().trim())) {
                    Toast.makeText(EnterPhoneNumber.this, "Enter valid Phone Number!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String finalNumber = "+" + countryCode.getText().toString().trim() + phoneNumber.getText().toString().trim();

                    Intent intent = new Intent(EnterPhoneNumber.this, VerifyPhoneNumber.class);

                    intent.putExtra("phoneNumber",finalNumber);

                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}