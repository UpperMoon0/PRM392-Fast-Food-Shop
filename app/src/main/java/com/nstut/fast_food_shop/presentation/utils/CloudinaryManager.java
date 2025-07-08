package com.nstut.fast_food_shop.presentation.utils;

import android.os.Handler;
import android.os.Looper;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CloudinaryManager {
    private final Cloudinary cloudinary;

    public CloudinaryManager() {
        Map config = new HashMap();
        config.put("cloud_name", "dyqh4xomc");
        config.put("api_key", "166747438877458");
        config.put("api_secret", "9ObU3EEqf2Z8_51p9GFFJvz5iaU");
        cloudinary = new Cloudinary(config);
    }

    public String uploadImageToFolder(File file, String folderName) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap("folder", folderName);
        Map uploadResult = cloudinary.uploader().upload(file, options);
        return uploadResult.get("secure_url").toString();
    }

    // Asynchronous, optimized
    public void uploadImage(File file, String folderName, Consumer<String> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Map<String, Object> options = ObjectUtils.asMap("folder", folderName);
                Map uploadResult = cloudinary.uploader().upload(file, options);
                String url = uploadResult.get("secure_url").toString();
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(url));
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(""));
            }
        });
    }
}

