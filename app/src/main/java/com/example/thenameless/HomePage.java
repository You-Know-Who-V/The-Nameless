package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;

import com.example.thenameless.model.ProductDetails;
import com.example.thenameless.view.RecyclerViewHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Home Page";
    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    private List<ProductDetails> list = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("productDetails");

    private RecyclerViewHome recyclerViewHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        firebaseAuth = FirebaseAuth.getInstance();

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

        recyclerView = findViewById(R.id.home_recyclerView);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //Toast.makeText(this, String.valueOf(getAllProducts().size()), Toast.LENGTH_SHORT).show();



        fab = findViewById(R.id.home_fab);

        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_menu,menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        MenuItem item = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) item.getActionView();
        //Toast.makeText(HomePage.this, "here", Toast.LENGTH_SHORT).show();

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                recyclerViewHome = new RecyclerViewHome(HomePage.this, list);
                recyclerView.setAdapter(recyclerViewHome);

                Toast.makeText(HomePage.this, "here", Toast.LENGTH_SHORT).show();

                recyclerViewHome.getFilter().filter(newText);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = new Intent(HomePage.this, ParticularTypeActivity.class);

        switch(item.getItemId())
        {
            case R.id.logout:
                LoginActivity.mAuth.signOut();
                            finish();

                            startActivity(new Intent(HomePage.this, LoginActivity.class));
                            break;

            case R.id.cart: //Filter search result to show only books
                Toast.makeText(this, "Cart Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.my_acc:   //Go to Account Settings
                Toast.makeText(this, "My Account Selected", Toast.LENGTH_SHORT).show();


                break;

            case R.id.books: //Filter search result to show only books
                Toast.makeText(this, "Books Selected", Toast.LENGTH_SHORT).show();

                intent.putExtra("type","Book");

                startActivity(intent);
                break;

            case R.id.lab_coat: //Filter search result to show only Lab Coats
                Toast.makeText(this, "Lab Coat Selected", Toast.LENGTH_SHORT).show();

                intent.putExtra("type","Lab Coat");

                startActivity(intent);
                break;

            case R.id.instrument: //Filter search result to show only Instrument
                Toast.makeText(this, "Instrument Selected", Toast.LENGTH_SHORT).show();

                intent.putExtra("type","Other Category");

                startActivity(intent);
                break;

            case R.id.sports: //Filter search result to show only Sports
                Toast.makeText(this, "Sports Selected", Toast.LENGTH_SHORT).show();

                intent.putExtra("type","Other Category");

                startActivity(intent);
                break;

            case R.id.other_category: //Filter search result to show only books
                Toast.makeText(this, "Other Category Selected", Toast.LENGTH_SHORT).show();

                intent.putExtra("type","Other Category");

                startActivity(intent);
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

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {

                                ProductDetails productDetails = new ProductDetails();

                                productDetails.setTimeAdded(documentSnapshot.getString("timeAdded"));
                                productDetails.setPrice(Integer.parseInt(documentSnapshot.get("price").toString()));
                                productDetails.setTitle(documentSnapshot.getString("title"));
                                productDetails.setImage1_url(documentSnapshot.getString("image1_url"));
                                productDetails.setType(documentSnapshot.getString("type"));
                                productDetails.setUserId(documentSnapshot.getString("userId"));
                                productDetails.setDescription(documentSnapshot.getString("description"));
                                productDetails.setUserName(documentSnapshot.getString("userName"));
                                productDetails.setImage2_url(documentSnapshot.getString("image2_url"));
                                productDetails.setImage3_url(documentSnapshot.getString("image3_url"));
                                productDetails.setImage4_url(documentSnapshot.getString("image4_url"));

                                //Log.d(TAG, "onComplete: " + documentSnapshot.getString("title"));

                                list.add(productDetails);
                                //Log.d(TAG, "onComplete: " + list.size());
                            }
                            recyclerViewHome = new RecyclerViewHome(HomePage.this, list);

                            recyclerView.setAdapter(recyclerViewHome);
                            //recyclerView.notify();
                        }
                    }
                });




    }
}