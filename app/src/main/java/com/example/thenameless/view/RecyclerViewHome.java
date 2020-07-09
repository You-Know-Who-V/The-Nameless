package com.example.thenameless.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thenameless.ProductPreview;
import com.example.thenameless.R;
import com.example.thenameless.model.ProductDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewHome extends RecyclerView.Adapter<RecyclerViewHome.ViewHolder> {

    private List<ProductDetails> productDetailsList;
    private Context context;

    public RecyclerViewHome(Context context, List<ProductDetails> productDetailsList) {
        this.productDetailsList = productDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHome.ViewHolder holder, int position) {

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
