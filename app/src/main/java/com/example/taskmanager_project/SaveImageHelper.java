package com.example.taskmanager_project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SaveImageHelper {
    private ImageView imageView;
    private ExecutorService executorService;
    private Handler handler;

    public SaveImageHelper(ImageView imageView) {
        this.imageView = imageView;
        this.executorService = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void loadImage(String url) {
        executorService.execute(() -> {
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalBitmap = bitmap;
            handler.post(() -> imageView.setImageBitmap(finalBitmap));
        });
    }
}