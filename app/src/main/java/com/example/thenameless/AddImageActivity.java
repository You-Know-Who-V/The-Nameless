package com.example.thenameless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thenameless.model.Namelesser;
import com.example.thenameless.model.ProductDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GET_IMAGE_CODE = 1111;
    private static final String TAG = "Add Image Activity";
    private ImageView imageView;
    private FloatingActionButton fab;
    private Button nextButton;
    private ImageButton prevImage,nextImage;
    private ProgressBar progressBar;

    private int currentImageIndex = 0;

    private String currentUserName;
    private String currentUserId;

    private List<Uri> imageUriList = new ArrayList<>();
    private List<String> imageUrlList = new ArrayList<>();

    private Bundle bundle;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("productDetails");

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        currentUserName = Namelesser.getInstance().getUserName();
        currentUserId = Namelesser.getInstance().getUserId();

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        bundle = getIntent().getExtras();

        imageView = findViewById(R.id.image_imageView);
        progressBar = findViewById(R.id.image_progressBar);
        fab = findViewById(R.id.image_fab);
        nextButton = findViewById(R.id.image_next_button);
        prevImage = findViewById(R.id.image_previousImage_button);
        nextImage = findViewById(R.id.image_nextImage_button);

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

        fab.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevImage.setOnClickListener(this);
        nextImage.setOnClickListener(this);

    }

    public void getPhoto()
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GET_IMAGE_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void chooseimg(View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            getPhoto();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.image_fab:
                //add new image
                if(imageUriList.size() <= 3) {
                    progressBar.setVisibility(View.VISIBLE);
                    chooseimg(view);
                }
                else{
                    Toast.makeText(this, "Reached maximum images!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.image_next_button:
                //goto next PriceActivity

                if(imageUriList.size() != 0){

                    Intent intent = new Intent(AddImageActivity.this, AddPriceActivity.class);

                    intent.putExtra("userName",currentUserName);
                    intent.putExtra("userId",currentUserId);
                    intent.putExtra("type",bundle.getString("type"));
                    intent.putExtra("title",bundle.getString("title"));
                    intent.putExtra("description",bundle.getString("description"));

                    for(int i=1;i<=imageUrlList.size();i++){
                        Toast.makeText(this, "image" + i + "_url", Toast.LENGTH_SHORT).show();
                        intent.putExtra("image" + i + "_url", imageUrlList.get(i-1));
                    }

                    startActivity(intent);

                }
                else{
                    Toast.makeText(this, "Atleast 1 image required!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.image_nextImage_button:
                //show next Image

                currentImageIndex++;

                if(currentImageIndex == imageUriList.size()-1){
                    nextImage.setVisibility(View.GONE);
                }
                if(currentImageIndex > 0){
                    prevImage.setVisibility(View.VISIBLE);
                }

                imageView.setImageURI(imageUriList.get(currentImageIndex));
                break;
            case R.id.image_previousImage_button:
                //show previous Image

                currentImageIndex--;

                if(currentImageIndex == 0){
                    prevImage.setVisibility(View.GONE);
                }
                if(currentImageIndex < imageUriList.size()-1){
                    nextImage.setVisibility(View.VISIBLE);
                }

                imageView.setImageURI(imageUriList.get(currentImageIndex));
                break;

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.cancel)
        {
            startActivity(new Intent(AddImageActivity.this,HomePage.class));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_IMAGE_CODE && resultCode == RESULT_OK){

            if(data != null){

                Uri imageUri = data.getData();

                imageUriList.add(imageUri);

                addImageToStorage(imageUri);

                if(currentImageIndex < imageUriList.size()-1){
                    nextImage.setVisibility(View.VISIBLE);
                }
                if(currentImageIndex > 0 ){
                    prevImage.setVisibility(View.VISIBLE);
                }

                imageView.setImageURI(imageUriList.get(currentImageIndex));

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

                                progressBar.setVisibility(View.INVISIBLE);

                                String imageUrl = uri.toString();

                                imageUrlList.add(imageUrl);

                                Toast.makeText(AddImageActivity.this, imageUrl, Toast.LENGTH_SHORT).show();
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

                progressBar.setVisibility(View.INVISIBLE);

                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
}