package com.example.thenameless;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class AccountDetails extends AppCompatActivity {

    EditText nameEditText,phone_noEditText,emailEditText,clgEmailEditText,semEditText;
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

    public void getDetails()
    {

    }
}