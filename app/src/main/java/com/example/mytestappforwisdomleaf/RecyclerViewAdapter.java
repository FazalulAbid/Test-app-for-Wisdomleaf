package com.example.mytestappforwisdomleaf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private ArrayList<MainData> dataArrayList;
    private Context context;
    private RecyclerViewClickListener listener;

    public RecyclerViewAdapter(ArrayList<MainData> dataArrayList, Context context, RecyclerViewClickListener listener) {
        this.dataArrayList = dataArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MainData data = dataArrayList.get(position);
        holder.tvHeader.setText(data.getTitle());
        holder.tvDescription.setText(data.getDescription());

        //Change Image size on download link
        String imageUrl = data.getImage();
        boolean flag = false;
        for (int i = imageUrl.length()-1; i > 0; i--) {
            if (imageUrl.charAt(i) == '/' && flag) {
                break;
            } else {
                if (imageUrl.charAt(i) == '/') {
                    flag = true;
                }
                imageUrl = imageUrl.substring(0, imageUrl.length() - 1);
            }
        }
        imageUrl += "50/50";
        //Set image on view
        Glide.with(context).load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView tvHeader, tvDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.card_image);
            tvHeader = itemView.findViewById(R.id.card_tv_head);
            tvDescription = itemView.findViewById(R.id.card_tv_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(itemView, getAdapterPosition());
        }
    }
}
