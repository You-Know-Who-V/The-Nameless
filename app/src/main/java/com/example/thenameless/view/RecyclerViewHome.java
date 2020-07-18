package com.example.thenameless.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thenameless.ProductPreview;
import com.example.thenameless.R;
import com.example.thenameless.model.Namelesser;
import com.example.thenameless.model.ProductDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class RecyclerViewHome extends RecyclerView.Adapter<RecyclerViewHome.ViewHolder> {

    private static final String TAG = "Recycler View Home";
    private List<ProductDetails> productDetailsList;
    private Context context;
    private List<ProductDetails> productDetailsListFull;

    private int pos;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection(Namelesser.getInstance().getUserName() +"Favourites");

    public RecyclerViewHome(Context context, List<ProductDetails> productDetailsList) {
        this.productDetailsList = productDetailsList;
        Toast.makeText(context, String.valueOf(productDetailsList.size()), Toast.LENGTH_SHORT).show();
        productDetailsListFull = new ArrayList<>(productDetailsList);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.product_recycler_view_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHome.ViewHolder holder, int position) {

        final ProductDetails productDetails = productDetailsList.get(position);

        pos = position;

        holder.title.setText(productDetails.getTitle());
        holder.type.setText(productDetails.getType());
        holder.timeAdded.setText(productDetails.getTimeAdded());
        final String imageUrl = productDetails.getImage1_url();

//        if(imageUrl != null)
//        collectionReference.whereEqualTo("image1_url",productDetails.getImage1_url())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()) {
//                            if(task.getResult().size() == 1) {
//                                holder.flag = 1;
//                                //Toast.makeText(context, String.valueOf(task.getResult().size()), Toast.LENGTH_SHORT).show();
//                                holder.favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
//                                for(QueryDocumentSnapshot snapshot : task.getResult()) {
//                                    Log.d(TAG, "onComplete: " + productDetails.getImage1_url()  + "\n" + snapshot.getString("image1_url"));
//                                }
//                            }
//                        }
//                    }
//                });


        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.cool_backgrounds)
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            //goto ProductPreview

            @Override
            public void onClick(View view) {

                Intent intent =new Intent(context, ProductPreview.class);

                intent.putExtra("title",productDetails.getTitle());
                intent.putExtra("type",productDetails.getType());
                intent.putExtra("description",productDetails.getDescription());
                intent.putExtra("userName",productDetails.getUserName());
                intent.putExtra("userId",productDetails.getUserId());
                intent.putExtra("price",productDetails.getPrice());
                intent.putExtra("timeAdded",productDetails.getTimeAdded());
                intent.putExtra("image1_url",productDetails.getImage1_url());
                intent.putExtra("image2_url",productDetails.getImage2_url());
                intent.putExtra("image3_url",productDetails.getImage3_url());
                intent.putExtra("image4_url",productDetails.getImage4_url());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }

//    @Override
//    public Filter getFilter() {
//        return titleFilter;
//    }
    public void filter(String textSearch){

        textSearch = textSearch.toLowerCase(Locale.getDefault());

        productDetailsList.clear();

        if (textSearch.length() == 0) {

            productDetailsList.addAll(productDetailsListFull);
        } else {

            for (ProductDetails productDetails : productDetailsListFull) {
                if (productDetails.getTitle().toLowerCase(Locale.getDefault()).contains(textSearch))
                    productDetailsList.add(productDetails);
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView title,type,timeAdded;
        private CardView cardView;
       // private ImageButton favButton;
        private int flag = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.row_imageView);
            title = itemView.findViewById(R.id.row_title);
            type = itemView.findViewById(R.id.row_type);
            timeAdded = itemView.findViewById(R.id.row_time);
            cardView = itemView.findViewById(R.id.row_cardView);
            //favButton = itemView.findViewById(R.id.row_fav_imageButton);


            //favButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();

            ProductDetails productDetails = productDetailsList.get(pos);

            //favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);

//            if(flag == 0 )
//                    collectionReference.add(productDetails)
//                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentReference> task) {
//                                    if(task.isSuccessful()) {
//                                        favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
//                                    }
//                                }
//                            });
//                else
//                    collectionReference.whereEqualTo("image1_url",productDetails.getImage1_url())
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if(task.isSuccessful()) {
//                                    for(QueryDocumentSnapshot snapshot : task.getResult()) {
//                                        snapshot.getReference().delete();
//                                    }
//                                    favButton.setBackgroundResource(R.drawable.ic_baseline_star_23);
//                                }
//                            }
//                        });
        }
    }
}
