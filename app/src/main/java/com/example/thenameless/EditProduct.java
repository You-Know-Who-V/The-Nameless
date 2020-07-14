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

import com.example.thenameless.model.Namelesser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EditProduct extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Product Preview";
    private Bundle bundle;

    private EditText priceEditText, descriptionEditText,typeEditText,titleEditText;
    private ImageButton previousImage, nextImage;
    private ImageView imageView;

    private int currentImageIndex = 0, mx = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("productDetails");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        bundle = getIntent().getExtras();

        priceEditText=findViewById(R.id.priceEditText);
        descriptionEditText=findViewById(R.id.descriptionEditText);
        typeEditText=findViewById(R.id.typeEditText);
        titleEditText=findViewById(R.id.titleEditText);

        imageView=findViewById(R.id.imageView);
        previousImage=findViewById(R.id.prevImageButton);
        nextImage=findViewById(R.id.nextImageButton);

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

        titleEditText.setText(bundle.getString("title"));
        descriptionEditText.setText( bundle.getString("description"));
        priceEditText.setText(String.valueOf(bundle.getInt("price")));
        typeEditText.setText(bundle.getString("type"));

        nextImage.setOnClickListener(this);
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
        }
    }

    public void updateInfo(View view)
    {

    }
}