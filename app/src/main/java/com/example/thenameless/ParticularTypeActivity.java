package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

                            for(QueryDocumentSnapshot snapshot : task.getResult()){

                                ProductDetails productDetails = new ProductDetails();

                                productDetails.setType(type);
                                productDetails.setTitle(snapshot.getString("title"));
                                productDetails.setImage1_url(snapshot.getString("image1_url"));
                                productDetails.setTimeAdded(snapshot.getString("timeAdded"));
                                productDetails.setPrice(Integer.parseInt(snapshot.get("price").toString()));

                                typeList.add(productDetails);

                            }

                            recyclerViewHome = new RecyclerViewHome(ParticularTypeActivity.this, typeList);

                            recyclerView.setAdapter(recyclerViewHome);

                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.particular_type_page_menu_bar,menu);
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

        selectParticularType(type);

    }
}