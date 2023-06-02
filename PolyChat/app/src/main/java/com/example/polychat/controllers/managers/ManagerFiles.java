package com.example.polychat.controllers.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageAccessLevel;
import com.amplifyframework.storage.StorageException;
import com.amplifyframework.storage.options.StorageGetUrlOptions;
import com.bumptech.glide.load.data.mediastore.MediaStoreUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class ManagerFiles {
    private static final String URL="s3://polychata46caca32e3148a5a8ccd272ac18ae27113230-dev/";

    public interface OnFileAddedListener {
        void onFileAdded(String messageId);
    }

    public interface OnFileDownloadListener {
        void onFileDownload(File file);
    }

    public interface OnFileExistListener {
        void onFileExists(Boolean exist, String url);
    }

    public static void uploadFile(String name, Uri img, Context context, ManagerFiles.OnFileAddedListener listener){
        try {
            File imgFile = new File(context.getFilesDir(), name);

            // Copiar el contenido de la Uri al archivo
            InputStream inputStream = context.getContentResolver().openInputStream(img);
            FileOutputStream outputStream = new FileOutputStream(imgFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            Amplify.Storage.uploadFile(
                    name,
                    imgFile,
                    result -> {
                        Log.i("Amplify S3", "Successfully uploaded: " + result.getKey());
                        listener.onFileAdded(result.getKey());
                    },
                    storageFailure -> {
                        Log.e("Amplify S3", "Upload failed", storageFailure);
                    }
            );
        } catch (FileNotFoundException e) {
            Log.i("Amplify S3", "Error file not found: " + e);
        } catch (IOException e) {
            Log.i("Amplify S3", "IOException: " + e);
        }
    }

    public static void downloadFile(String key, Uri img, Context context, ManagerFiles.OnFileDownloadListener listener){
        File file = new File(context.getFilesDir() + key.substring(7));
        Amplify.Storage.downloadFile(
                key.substring(7),
                file,
                result -> {
                    listener.onFileDownload(result.getFile());
                    Log.i("Amplify S3", "Successfully downloaded: " + result.getFile().getName());
                    },
                error -> Log.e("Amplify S3",  "Download Failure", error)
        );

    }

    public static Uri saveImageToGallery(Bitmap imageBitmap, String path, Context context) {
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, file.getName());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getUriFileGallery(file.getName(), context);
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            }
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkExistFileGallery(String fileName, Context context){
        Uri uri = getUriFileGallery(fileName, context);
        return MediaStoreUtil.isMediaStoreImageUri(uri);
    }

    public static Uri getUriFileGallery(String fileName, Context context){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static void existFileInS3(String id, ManagerFiles.OnFileExistListener listener){
        Amplify.Storage.getUrl(
                id,
                result -> {
                    try {
                        java.net.URL url = new URL(result.getUrl().toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("HEAD");
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            Log.e("AWS S3", "Existe el fichero");
                            listener.onFileExists(true, result.getUrl().toString());
                        } else {
                            Log.e("AWS S3", "No existe el fichero");
                            listener.onFileExists(false,"");
                        }
                    } catch (IOException e) {
                        Log.e("AWS S3", "Error al verificar la existencia del fichero");
                        listener.onFileExists(false,"");
                    }
                },
                error -> {
                    Log.e("AWS S3", "Error al obtener la URL del fichero");
                    listener.onFileExists(false,"");
                }
        );
    }

}
