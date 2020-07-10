package com.example.thenameless.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thenameless.ProductPreview;
import com.example.thenameless.R;
import com.example.thenameless.model.ProductDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapterMyProduct extends RecyclerView.Adapter<RecyclerViewAdapterMyProduct.ViewHolder>{

    private Context context;
    private List<ProductDetails> productDetailsList;
    private List<ProductDetails> productDetailsListFull;

    public RecyclerViewAdapterMyProduct(Context context, List<ProductDetails> productDetailsList) {
        this.context = context;
        this.productDetailsList = productDetailsList;
        productDetailsListFull = new ArrayList<>(productDetailsList);
    }

    @NonNull
    @Override
    public RecyclerViewAdapterMyProduct.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.product_recycler_view_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterMyProduct.ViewHolder holder, int position) {
        final ProductDetails productDetails = productDetailsList.get(position);

        holder.title.setText(productDetails.getTitle());
        holder.type.setText(productDetails.getType());
        holder.timeAdded.setText(productDetails.getTimeAdded());
        String imageUrl = productDetails.getImage1_url();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.cool_backgrounds)
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //TODO:goto editProduct Activity

            }
        });
    }

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

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title,type,timeAdded;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.row_imageView);
            title = itemView.findViewById(R.id.row_title);
            type = itemView.findViewById(R.id.row_type);
            timeAdded = itemView.findViewById(R.id.row_time);
            cardView = itemView.findViewById(R.id.row_cardView);
        }
    }
}