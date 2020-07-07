package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddDetailsActivity extends AppCompatActivity {

    private EditText titleEditText,descriptionEditText;
    private Button nextButton;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        bundle = getIntent().getExtras();

        titleEditText = findViewById(R.id.detail_title);
        descriptionEditText = findViewById(R.id.detail_description);
        nextButton = findViewById(R.id.detail_next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(titleEditText.getText().toString().trim())
                        && !TextUtils.isEmpty(descriptionEditText.getText().toString().trim())){

                        Intent intent = new Intent(AddDetailsActivity.this, AddImageActivity.class);

                        intent.putExtra("title", titleEditText.getText().toString().trim());
                        intent.putExtra("description", descriptionEditText.getText().toString().trim());
                        intent.putExtra("type",bundle.getString("type"));

                        startActivity(intent);
                }
                else{
                    Toast.makeText(AddDetailsActivity.this, "Empty Fields Not Allowed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.cancel)
        {
            startActivity(new Intent(AddDetailsActivity.this,HomePage.class));
            return true;
        }
        return false;
    }
}