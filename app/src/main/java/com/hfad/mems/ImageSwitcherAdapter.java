package com.hfad.mems;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageSwitcherAdapter extends RecyclerView.Adapter<ImageSwitcherAdapter.MyViewHolder> {

    private Context context;
    private List<String> urls;

    public ImageSwitcherAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Uri uri = Uri.parse(urls.get(position));
        Picasso.with(context).load(uri).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
           image = itemView.findViewById(R.id.imageView11);
        }
    }
}