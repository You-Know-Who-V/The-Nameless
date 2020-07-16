package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditProduct extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Edit Preview";
    private Bundle bundle;

    private EditText priceEditText, descriptionEditText,typeEditText,titleEditText;
    private ImageButton previousImage, nextImage, clearButton;
    private ImageView imageView;

    private int currentImageIndex = 0, mx = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("productDetails");

    List<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        bundle = getIntent().getExtras();

        priceEditText=findViewById(R.id.priceEditText);
        descriptionEditText=findViewById(R.id.descriptionEditText);
        typeEditText=findViewById(R.id.typeEditText);
        titleEditText=findViewById(R.id.titleEditText);
        clearButton = findViewById(R.id.edit_clear);
        imageView=findViewById(R.id.imageView);
        previousImage=findViewById(R.id.prevImageButton);
        nextImage=findViewById(R.id.nextImageButton);

        for(int i=1;i<=4;i++){
            if(bundle.getString("image" + i + "_url") == null){
                break;
            }
            imageList.add(bundle.getString("image" + i + "_url"));
            mx=i;
        }

        Toast.makeText(this, String.valueOf(mx), Toast.LENGTH_SHORT).show();

        if(mx > 1){
            nextImage.setVisibility(View.VISIBLE);
        }

        Picasso.get()
                .load(bundle.getString("image1_url"))
                .placeholder(R.drawable.cool_backgrounds)
                .into(imageView);

        titleEditText.setText(bundle.getString("title"));
        descriptionEditText.setText( bundle.getString("description"));
        priceEditText.setText(String.valueOf(bundle.getInt("price")));
        typeEditText.setText(bundle.getString("type"));

        nextImage.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        previousImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.prevImageButton:
            {
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
            }

            case R.id.nextImageButton:{
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
            }
            case R.id.edit_clear:
                removeImage();
                break;

        }
    }

    private void removeImage() {
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot snapshot : task.getResult()) {
                                for(int i = currentImageIndex+1; i < mx; i++) {

                                    String temp = "image"+ (i) + "_url";

                                    snapshot.getReference().update(temp, imageList.get(i));

                                    Log.d(TAG, "onComplete: " + imageList.get(i) + " " + String.valueOf(i));

                                }
                                snapshot.getReference().update("image"+ (mx) + "_url", null);
                                for(int i= currentImageIndex; i<mx-1;i++) {
                                    imageList.set(i,imageList.get(i+1));
                                }
                                imageList.remove(mx-1);

                                mx--;

                                if(currentImageIndex == imageList.size()) {
                                    currentImageIndex--;
                                }

                                if(imageList.size() > 1) {
                                    Picasso.get()
                                            .load(imageList.get(currentImageIndex + 1))
                                            .placeholder(R.drawable.cool_backgrounds)
                                            .into(imageView);

                                }

                            }
                        }
                    }
                });
    }

    public void updateInfo(View view)
    {

    }
}