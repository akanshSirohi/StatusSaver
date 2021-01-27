package com.akansh.statussaver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;


public class Utils {

    Context ctx;
    Activity activity;
    public String root= Environment.getExternalStorageDirectory().toString();

    public Utils(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }

    public void showToast(String msg) {
        Toast.makeText(ctx,msg,Toast.LENGTH_LONG).show();
    }

    public void showInfoSnackBar(String msg) {
        Snackbar snackbar=Snackbar.make(activity.findViewById(android.R.id.content),msg,4000);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(ctx,R.color.colorPrimary));
        snackbar.show();
    }

    public void showErrorSnackBar(String msg) {
        Snackbar snackbar=Snackbar.make(activity.findViewById(android.R.id.content),msg,4000);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(ctx,android.R.color.holo_red_light));
        snackbar.show();
    }

    public void openFile(String path,String type) {
        File file = new File(path);
        Uri uri;
        Intent i = new Intent(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", file);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        i.setDataAndType(uri, type);
        ctx.startActivity(i);
    }

    public void openFile(String path) {
        File file = new File(path);
        Uri uri;
        Intent i = new Intent(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", file);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        String type=getMimeType(uri);
        i.setDataAndType(uri, type);
        ctx.startActivity(i);
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = ctx.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

}
