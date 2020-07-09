package com.example.thenameless;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductPreview extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Product Preview";
    private Bundle bundle;

    private TextView typeTextView,priceTextView,descriptionTextView,userNameTextView,dateAddedTextView,titleTextView;
    private Button callButton,chatButton;
    private ImageButton previousImage,nextImage;
    private ImageView imageView;

    private int currentImageIndex = 0, mx = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preview);

        bundle = getIntent().getExtras();

        typeTextView = findViewById(R.id.preview_type_textView);
        priceTextView = findViewById(R.id.preview_price_textView);
        descriptionTextView = findViewById(R.id.preview_description_textView);
        userNameTextView = findViewById(R.id.preview_account_textView);
        dateAddedTextView = findViewById(R.id.preview_date_textView);
        titleTextView = findViewById(R.id.preview_title_textView);
        callButton = findViewById(R.id.preview_call_button);
        chatButton = findViewById(R.id.preview_chat_button);
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
        typeTextView.setText(bundle.getString("type"));
        dateAddedTextView.setText(bundle.getString("timeAdded"));
        //Log.d(TAG, "onCreate: " + bundle.getString("dateAdded"));
        descriptionTextView.setText(bundle.getString("description"));
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        priceTextView.setText(String.valueOf(bundle.getInt("price")));
        userNameTextView.setText(bundle.getString("userName"));

        nextImage.setOnClickListener(this);
        previousImage.setOnClickListener(this);
        callButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.preview_call_button:
                //call the user's whose product it is
                break;
            case R.id.preview_chat_button:
                //chat with the user
                break;
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
        }

    }
}