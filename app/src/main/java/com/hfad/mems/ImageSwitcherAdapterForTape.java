package com.hfad.mems;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ImageSwitcherAdapterForTape extends RecyclerView.Adapter<ImageSwitcherAdapterForTape.MyViewHolder> {
    private Context context;
    private List<String> urls;
    public ImageSwitcherAdapterForTape(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_for_tape, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Uri uri = Uri.parse(urls.get(position));
        Picasso.with(context).load(uri).into(holder.image);
        final Bitmap[] b = new Bitmap[1];
        Picasso.with(context).load(uri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                b[0] =bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view,uri.toString(),b[0]);
            }
        });

    }

    @Override
    public int getItemCount() {
        return urls.size();
    }




    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private Button button;
        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewTape);
            button = itemView.findViewById(R.id.button2);


        }
    }
    private void showPopupMenu(View v, final String tag, final Bitmap bitmap1) {
        PopupMenu popupMenu = new PopupMenu(context, v);

        popupMenu.inflate(R.menu.menu1);
        final String tagg = tag;
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:



                                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                        bitmap1, "Image Description", null);

                                Uri imageUri = Uri.parse(path);








                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                shareIntent.setType("image/jpeg");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                context.startActivity(Intent.createChooser(shareIntent, "send"));



/*
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("image/jpeg");
                                String shareBody = tagg;
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, R.drawable.none);
                                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                Toast.makeText(context,
                                        "Вы выбрали PopupMenu 2",
                                        Toast.LENGTH_SHORT).show();

*/





                                return true;
                            case R.id.menu2:
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                Uri uri = Uri.parse(tagg);
                                intent.setType("image/*");
                                intent.putExtra(Intent.EXTRA_STREAM, String.valueOf(uri));
                                context.startActivity(intent);


                              /*  Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                sharingIntent.setType("image/*");
                         //      Bitmap bitmap = bitok[0].getBitmap();
                               sharingIntent.putExtra(Intent.EXTRA_STREAM,bitok[0]);
                                context.startActivity(Intent.createChooser(sharingIntent, "Share Image"));*/
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }


}
