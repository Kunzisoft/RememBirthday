package com.kunzisoft.remembirthday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by joker on 25/02/17.
 */
public class ContactThumbnailTask extends AsyncTask<Void, Void, Bitmap> {

    private WeakReference<ImageView> imageViewWeakReference;
    private Uri uri;
    private String path;
    private Context context;


    public ContactThumbnailTask(ImageView imageView, Uri uri, Context context) {
        this.uri = uri;
        this.imageViewWeakReference = new WeakReference<>(imageView);
        this.path = (String)imageViewWeakReference.get().getTag(); // to make sure we don't put the wrong image on callback
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap image = null;
        if (null!= is)
            image=  BitmapFactory.decodeStream(is);

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewWeakReference != null && imageViewWeakReference.get() != null && (imageViewWeakReference.get().getTag()).equals(path) && null != bitmap)
            imageViewWeakReference.get().setImageBitmap(bitmap);
    }
}