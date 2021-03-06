package com.jp.baxomdistributor.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.jp.baxomdistributor.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtils {

    public static String imageFilePath = "", videoFilePath = "";
    public static File file;

    public static void openCameraIntent(Context context) {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(context.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
                file = photoFile;
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context.getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                /*if (context instanceof AddShopActivity) {
                    ((AddShopActivity) context).startActivityForResult(pictureIntent, 1);
                } else*/
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(pictureIntent, 1);
                } else {
                    Toast.makeText(context, "Context not defined!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        /*File image = new File(Environment.getExternalStorageDirectory() + "/Baxom Health Care/"
                + System.currentTimeMillis() + ".jpg");*/

        AppUtils.imageFilePath = image.getAbsolutePath();
        return image;
    }


    public static String comrpess_50(String ref_path, File file) {

        File imgfile = new File(ref_path);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath(), options);
        // Bitmap compressed_bitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 5, myBitmap.getHeight() / 5, true);
        Bitmap compressed_bitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), null, true);

        Log.i("file=", file + "");
        Log.i("file size=", Integer.parseInt(String.valueOf(file.length() / 1024)) + "");
        Log.i("file size conpress=", Integer.parseInt(String.valueOf(file.length() / 1024)) / 80 + "");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            compressed_bitmap.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(String.valueOf(file.length() / 1024)) / 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}
