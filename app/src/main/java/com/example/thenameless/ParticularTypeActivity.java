package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.thenameless.model.ProductDetails;
import com.example.thenameless.view.RecyclerViewHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParticularTypeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView message;

    private String type;

    private Bundle bundle;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("productDetails");

    private RecyclerViewHome recyclerViewHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_type);

        firebaseAuth = FirebaseAuth.getInstance();

        bundle = getIntent().getExtras();
        type = bundle.getString("type");

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

        progressBar = findViewById(R.id.particular_progressBar);
        message = findViewById(R.id.particular_mess_textView);

        recyclerView = findViewById(R.id.particular_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    private void selectParticularType(final String type) {

        collectionReference.whereEqualTo("type",type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<ProductDetails> typeList = new ArrayList<>();

                        if(task.isSuccessful()){

                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){

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

                                typeList.add(productDetails);

                            }

                            recyclerViewHome = new RecyclerViewHome(ParticularTypeActivity.this, typeList);

                            recyclerView.setAdapter(recyclerViewHome);

                            if(typeList.size() == 0) {
                                message.setVisibility(View.VISIBLE);
                            }

                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.particular_type_page_menu_bar,menu);

        MenuItem item = menu.findItem(R.id.particular_search_button);
        SearchView searchView = (SearchView) item.getActionView();
        //Toast.makeText(HomePage.this, "here", Toast.LENGTH_SHORT).show();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                recyclerViewHome = new RecyclerViewHome(HomePage.this, list);
//                recyclerView.setAdapter(recyclerViewHome);
//
//                Toast.makeText(HomePage.this, "here", Toast.LENGTH_SHORT).show();

                recyclerViewHome.filter(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.close)
        {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

        progressBar.setVisibility(View.VISIBLE);

        selectParticularType(type);

    }
}