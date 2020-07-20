package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thenameless.model.Namelesser;
import com.example.thenameless.model.ProductDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductPreview extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Product Preview";
    private Bundle bundle;

    private TextView priceTextView, detailsTextView, userNameTextView, titleTextView;
    TextView nameTextView,email,phno,branch;
    ImageView profileImageView;
    Dialog myDailog;
    private ImageButton previousImage, nextImage, favButton;
    private ImageView imageView;

    private FirebaseUser currentUser;
    private int currentImageIndex = 0, mx = 1, flag = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection(Namelesser.getInstance().getUserId() + "Favourites");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preview);

        bundle = getIntent().getExtras();

        priceTextView = findViewById(R.id.preview_price_textView);
        detailsTextView = findViewById(R.id.preview_details_textView);
        userNameTextView = findViewById(R.id.preview_account_textView);
        titleTextView = findViewById(R.id.preview_title_textView);
        favButton = findViewById(R.id.preview_fav_button);
        previousImage = findViewById(R.id.preview_previous_imageButton);
        nextImage = findViewById(R.id.preview_next_imageButton);
        imageView = findViewById(R.id.preview_imageView);

        for(int i=1;i<=4;i++){
            if(bundle.getString("image" + i + "_url") == null){
                break;
            }
            mx=i;
        }

        if(mx > 1){
            nextImage.setVisibility(View.VISIBLE);
        }

        Picasso.get()
                .load(bundle.getString("image1_url"))
                .placeholder(R.drawable.cool_backgrounds)
                .into(imageView);

        titleTextView.setText(bundle.getString("title"));
        detailsTextView.setText("Type: " + bundle.getString("type")
                                    + "\n\n" + "Description:\n"
                                    + bundle.getString("description")
                                    + "\n\n" + "Date Added On: "
                                    + bundle.getString("timeAdded"));
        detailsTextView.setMovementMethod(new ScrollingMovementMethod());
        priceTextView.setText(String.valueOf(bundle.getInt("price")));
        userNameTextView.setText(bundle.getString("userName"));

        if(bundle.getString("image1_url") != null)
        collectionReference.whereEqualTo("image1_url",bundle.getString("image1_url"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().size() == 1) {
                                flag = 1;
                                //Toast.makeText(context, String.valueOf(task.getResult().size()), Toast.LENGTH_SHORT).show();
                                favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
                            }
                        }
                    }
                });
        userNameTextView.setOnClickListener(this);
        nextImage.setOnClickListener(this);
        previousImage.setOnClickListener(this);
        favButton.setOnClickListener(this);

    }

    public void getDetails()
    {
        db.collection("AccountDetails").whereEqualTo("Name",bundle.getString("userName"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            myDailog.setContentView(R.layout.custom_pop_up);
                            nameTextView=myDailog.findViewById(R.id.custom_name_textView);
                            email=myDailog.findViewById(R.id.custom_email_textView);
                            phno=myDailog.findViewById(R.id.custom_phoneNumber_textView);
                            profileImageView=myDailog.findViewById(R.id.custom_imageView);
                            branch=myDailog.findViewById(R.id.custom_branch_textView);
                            ImageView close=myDailog.findViewById(R.id.custom_cancel);
                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    myDailog.dismiss();
                                }
                            });
                            for(QueryDocumentSnapshot document: task.getResult())
                            {
                                Log.d("Info", String.valueOf(document.getData()));
                                Map<String, Object> info=document.getData();
                                nameTextView.setText("Name: " + info.get("Name").toString());
                                phno.setText("Phone Number: " + info.get("PhoneNo").toString());
                                email.setText("Email: " + info.get("EMail").toString());
                                branch.setText("Branch: "+ info.get("Branch").toString());
                                Glide.with(ProductPreview.this)
                                        .load(info.get("ProfileImg"))
                                        .into(profileImageView);
                            }
                            myDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            myDailog.show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductPreview.this,"Unable to retrieve Information  :(",Toast.LENGTH_LONG).show();
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
            finish();
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.preview_next_imageButton:

                currentImageIndex++;

                if(currentImageIndex == mx-1) {
                    nextImage.setVisibility(View.INVISIBLE);
                }
                if(currentImageIndex > 0){
                    previousImage.setVisibility(View.VISIBLE);
                }

                Picasso.get()
                        .load(bundle.getString("image" + (currentImageIndex+1) + "_url"))
                        .placeholder(R.drawable.cool_backgrounds)
                        .into(imageView);

                break;
            case R.id.preview_previous_imageButton:

                currentImageIndex--;

                if(currentImageIndex < mx-1) {
                    nextImage.setVisibility(View.VISIBLE);
                }
                if(currentImageIndex == 0){
                    previousImage.setVisibility(View.INVISIBLE);
                }

                Picasso.get()
                        .load(bundle.getString("image" + (currentImageIndex+1) + "_url"))
                        .placeholder(R.drawable.cool_backgrounds)
                        .into(imageView);
                break;
            case R.id.preview_fav_button:
                if(flag == 0) {
                    addFavourite();
                }
                else {
                    removeFavourite();
                }
            case R.id.preview_account_textView:
            {
                myDailog=new Dialog(ProductPreview.this);

                getDetails();
            }
        }

    }

    private void addFavourite() {

        ProductDetails productDetails = new ProductDetails();

        productDetails.setUserName(bundle.getString("userName"));
        productDetails.setTitle(bundle.getString("title"));
        productDetails.setDescription(bundle.getString("description"));
        productDetails.setUserId(bundle.getString("userId"));
        productDetails.setPrice(Integer.parseInt(String.valueOf(Objects.requireNonNull(bundle.getInt("price")))));
        productDetails.setImage1_url(bundle.getString("image1_url"));
        productDetails.setType(bundle.getString("type"));
        productDetails.setTimeAdded(bundle.getString("timeAdded"));

        if(bundle.containsKey("image2_url"))
            productDetails.setImage2_url(bundle.getString("image2_url"));
        if(bundle.containsKey("image3_url"))
            productDetails.setImage3_url(bundle.getString("image3_url"));
        if(bundle.containsKey("image4_url"))
            productDetails.setImage4_url(bundle.getString("image4_url"));

        collectionReference.add(productDetails)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()) {
                                        favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
                                    }
                                }
                            });
        flag = 1;

    }

    private void removeFavourite() {

        collectionReference.whereEqualTo("image1_url",bundle.getString("image1_url"))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(QueryDocumentSnapshot snapshot : task.getResult()) {
                                        snapshot.getReference().delete();
                                    }
                                    favButton.setBackgroundResource(R.drawable.ic_baseline_star_23);
                                }
                            }
                        });
        flag = 0;
    }
}