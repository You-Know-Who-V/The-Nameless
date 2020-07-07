package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.thenameless.model.ProductType;

import java.util.List;

public class ProductTypesListActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_types);

        listView = findViewById(R.id.product_listView);

        productList = new ProductType().getProductTypes();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductTypesListActivity.this,
                                                    android.R.layout.simple_list_item_1, productList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String type = productList.get(position);

                Intent intent = new Intent(ProductTypesListActivity.this, AddDetailsActivity.class);
                intent.putExtra("type",type);

                startActivity(intent);

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.cancel)
        {
            startActivity(new Intent(ProductTypesListActivity.this,HomePage.class));
            return true;
        }
        return false;
    }
}