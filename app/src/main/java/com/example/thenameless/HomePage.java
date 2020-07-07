package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        fab = findViewById(R.id.home_fab);
        recyclerView = findViewById(R.id.home_recyclerView);

        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_menu,menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.logout:LoginActivity.mAuth.signOut();
                            finish();
                            break;

            case R.id.cart: //Filter search result to show only books
                Toast.makeText(this, "Cart Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.my_acc:   //Go to Account Settings
                Toast.makeText(this, "My Account Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.books: //Filter search result to show only books
                Toast.makeText(this, "Books Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.lab_coat: //Filter search result to show only Lab Coats
                Toast.makeText(this, "Lab Coat Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.instrument: //Filter search result to show only Instrument
                Toast.makeText(this, "Instrument Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.sports: //Filter search result to show only Sports
                Toast.makeText(this, "Sports Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.other_category: //Filter search result to show only books
                Toast.makeText(this, "Other Category Selected", Toast.LENGTH_SHORT).show();
                break;
        }
        if(item.getItemId()==R.id.logout)
        {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.home_fab:
                //add new item
                startActivity(new Intent(HomePage.this, ProductTypesListActivity.class));
                break;
        }
    }
}