package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditProduct extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Edit Preview";
    private static final int GET_IMAGE_CODE = 111;
    private Bundle bundle;

    private EditText priceEditText, descriptionEditText, titleEditText;
    private ImageButton previousImage, nextImage, clearButton, addImage;
    private ImageView imageView;
    private Button updateButton, deleteButton;

    private int currentImageIndex = 0, mx = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("productDetails");

    private StorageReference storageReference;

    List<String> imageList = new ArrayList<>();
    List<Uri> imageUriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        bundle = getIntent().getExtras();

        storageReference = FirebaseStorage.getInstance().getReference();

        priceEditText=findViewById(R.id.priceEditText);
        descriptionEditText=findViewById(R.id.descriptionEditText);
        titleEditText=findViewById(R.id.titleEditText);
        clearButton = findViewById(R.id.edit_clear);
        imageView=findViewById(R.id.imageView);
        deleteButton=findViewById(R.id.edit_delete_button);
        previousImage=findViewById(R.id.prevImageButton);
        nextImage=findViewById(R.id.nextImageButton);
        updateButton = findViewById(R.id.edit_update_button);
        addImage = findViewById(R.id.edit_add_imageButton);

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

        nextImage.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        addImage.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        previousImage.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
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
                        .load(imageList.get(currentImageIndex))
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
                        .load(imageList.get(currentImageIndex))
                        .placeholder(R.drawable.cool_backgrounds)
                        .into(imageView);

                break;
            }
            case R.id.edit_clear:
                removeImage();
                break;

            case R.id.edit_update_button:

                if(!TextUtils.isEmpty(titleEditText.getText().toString().trim())
                        && !TextUtils.isEmpty(descriptionEditText.getText().toString().trim())
                        && !TextUtils.isEmpty(priceEditText.getText().toString().trim())
                        && mx > 0) {
                    updateProduct();
                }
                else {
                    Toast.makeText(this, "Empty Fields not Allowed!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.edit_add_imageButton:

                if(mx < 4) {
                    chooseImg(view);
                }
                else {
                    Toast.makeText(this, "Maximum 4 images can be Added!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.edit_delete_button:

                deleteProduct();
                break;

        }
    }

    private void deleteProduct() {

        collectionReference.whereEqualTo("image1_url", bundle.getString("image1_url"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot snapshot : task.getResult()) {

                                snapshot.getReference().delete();
                            }

                            Toast.makeText(EditProduct.this, "Product deleted!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(EditProduct.this, MyProductList.class));

                        }
                    }
                });
    }

    public void getPhoto()
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GET_IMAGE_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void chooseImg(View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            getPhoto();
        }
    }

    private void updateProduct() {


        collectionReference.whereEqualTo("image1_url", bundle.getString("image1_url"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot snapshot : task.getResult()) {
                                for(int i = 0; i < mx; i++) {

                                    String temp = "image"+ (i+1) + "_url";

                                    snapshot.getReference().update(temp, imageList.get(i));

                                    Log.d(TAG, "onComplete: " + imageList.get(i) + " " + String.valueOf(i));

                                }
                                for(int i = mx+1; i <= 4; i++)
                                    snapshot.getReference().update("image"+ (i) + "_url", null);

                                snapshot.getReference().update("title",titleEditText.getText().toString().trim());
                                snapshot.getReference().update("description", descriptionEditText.getText().toString().trim());
                                snapshot.getReference().update("price", priceEditText.getText().toString().trim());

                                snapshot.getReference().update("lastUpdated",getCurrentDate());

                            }

                            Toast.makeText(EditProduct.this, "Product updated!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(EditProduct.this, MyProductList.class));

                        }
                    }
                });



    }

    private String getCurrentDate() {

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return  formattedDate;
    }

    private void removeImage() {

        //Toast.makeText(this, imageList.get(currentImageIndex), Toast.LENGTH_SHORT).show();

        for(int i = currentImageIndex; i < mx-1; i++) {
            imageList.set(i,imageList.get(i+1));
        }
        imageList.remove(mx-1);

        if(mx == 1) {
            imageView.setImageResource(R.drawable.cool_backgrounds);
            clearButton.setVisibility(View.INVISIBLE);
            mx--;
        }
        else {
            if (currentImageIndex == mx - 1) {
                currentImageIndex--;
            }
            mx--;
            Picasso.get()
                    .load(imageList.get(currentImageIndex))
                    .placeholder(R.drawable.cool_backgrounds)
                    .into(imageView);
            if (currentImageIndex == mx - 1) {
                nextImage.setVisibility(View.INVISIBLE);
            } else {
                nextImage.setVisibility(View.VISIBLE);
            }

            if (currentImageIndex == 0) {
                previousImage.setVisibility(View.INVISIBLE);
            } else {
                previousImage.setVisibility(View.VISIBLE);
            }
        }


    }

    private void addImageToStorage(Uri imageUri) {

        final StorageReference ref = storageReference.child("images")
                .child("image_" + Timestamp.now().getSeconds());

        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                //progressBar.setVisibility(View.INVISIBLE);

                                String imageUrl = uri.toString();

                                imageList.add(imageUrl);

                                if(currentImageIndex < imageList.size()-1){
                                    nextImage.setVisibility(View.VISIBLE);
                                }
                                if(currentImageIndex > 0 ){
                                    previousImage.setVisibility(View.VISIBLE);
                                }

                                mx++;

                                //Toast.makeText(EditProduct.this, imageUrl, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu_bar,menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.cancel)
        {
            startActivity(new Intent(EditProduct.this,MyProductList.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_IMAGE_CODE && resultCode == RESULT_OK){

            if(data != null){

                Uri imageUri = data.getData();

                imageUriList.add(imageUri);

                addImageToStorage(imageUri);

            }
        }
    }
}