package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.example.thenameless.model.ProductDetails;
import com.example.thenameless.view.RecyclerViewHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowFavourites extends AppCompatActivity {

    private static final String TAG = "Show Favourites";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView message;

    private RecyclerViewHome recyclerViewHome;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection(Namelesser.getInstance().getUserId()
                                                                        + "Favourites");

    private List<ProductDetails> productDetailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favourites);

        progressBar = findViewById(R.id.fav_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        message = findViewById(R.id.fav_mess_textView);

        recyclerView = findViewById(R.id.fav_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            for(final QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                db.collection("productDetails").whereEqualTo("image1_url", documentSnapshot.getString("image1_url"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()) {

                                                    if(task.getResult().size() > 0) {
                                                    }
                                                    else {
                                                        documentSnapshot.getReference().delete();
                                                    }

                                                }
                                            }
                                        });

                                //Log.d(TAG, "onComplete: " + documentSnapshot.getString("title"));


                            }

                        }
                    }
                });

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            for(final QueryDocumentSnapshot documentSnapshot : task.getResult()) {

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

                                productDetailsList.add(productDetails);

                                //Log.d(TAG, "onComplete: " + documentSnapshot.getString("title"));


                            }

                            recyclerViewHome = new RecyclerViewHome(ShowFavourites.this, productDetailsList);


                            if(productDetailsList.size() == 0) {
                                message.setVisibility(View.VISIBLE);
                            }
                            recyclerView.setAdapter(recyclerViewHome);
                            progressBar.setVisibility(View.INVISIBLE);

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
            startActivity(new Intent(ShowFavourites.this,HomePage.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}