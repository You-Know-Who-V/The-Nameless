package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thenameless.model.ProductDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddPriceActivity extends AppCompatActivity {

    private Button nextButton;
    private EditText priceEditText;

    private Bundle bundle;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("productDetails");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_price);

        firebaseAuth = FirebaseAuth.getInstance();

        bundle = getIntent().getExtras();

        priceEditText = findViewById(R.id.price_editText);
        nextButton = findViewById(R.id.price_next_button);

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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(priceEditText.getText().toString().trim())){

                    ProductDetails productDetails = new ProductDetails();

                    productDetails.setUserName(bundle.getString("userName"));
                    productDetails.setTitle(bundle.getString("title"));
                    productDetails.setDescription(bundle.getString("description"));
                    productDetails.setUserId(bundle.getString("userId"));
                    productDetails.setPrice(Integer.parseInt(priceEditText.getText().toString()));
                    productDetails.setImage1_url(bundle.getString("image1_url"));

                    if(bundle.containsKey("image2_url"))
                        productDetails.setImage2_url(bundle.getString("image2_url"));
                    if(bundle.containsKey("image3_url"))
                        productDetails.setImage3_url(bundle.getString("image3_url"));
                    if(bundle.containsKey("image4_url"))
                        productDetails.setImage4_url(bundle.getString("image4_url"));

                    collectionReference.add(productDetails)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Toast.makeText(AddPriceActivity.this, "Product added!", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AddPriceActivity.this, HomePage.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
                else{
                    Toast.makeText(AddPriceActivity.this, "Empty Fields Not Allowed!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}