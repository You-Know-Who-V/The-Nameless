package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.example.thenameless.model.ProductDetails;
import com.example.thenameless.view.RecyclerViewAdapterMyProduct;
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

public class MyProductList extends AppCompatActivity {

    private static final String TAG = "My Product";
    private RecyclerView recyclerView;

    private List<ProductDetails> list = new ArrayList<>();

    private RecyclerViewAdapterMyProduct adapterMyProduct;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("productDetails");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product_list);

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

        recyclerView = findViewById(R.id.myProduct_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        list.clear();
        Toast.makeText(this, Namelesser.getInstance().getUserName(), Toast.LENGTH_SHORT).show();
        collectionReference.whereEqualTo("userName",Namelesser.getInstance().getUserName())
                .get()
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

                                Log.d(TAG, "onComplete: " + documentSnapshot.getString("title"));

                                list.add(productDetails);
                                //Log.d(TAG, "onComplete: " + list.size());
                            }
                            adapterMyProduct = new RecyclerViewAdapterMyProduct(MyProductList.this, list);

                            recyclerView.setAdapter(adapterMyProduct);
                            //recyclerView.notify();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_product_menu_bar,menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        MenuItem item = menu.findItem(R.id.myProduct_search_button);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapterMyProduct.filter(newText);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.myProduct_close)
        {
            startActivity(new Intent(MyProductList.this,HomePage.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
}