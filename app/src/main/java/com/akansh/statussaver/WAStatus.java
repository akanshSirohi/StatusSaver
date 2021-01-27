package com.akansh.statussaver;

import android.graphics.Bitmap;

import java.io.File;

public class WAStatus {
    private File file;
    private Bitmap thumbnail;

    public WAStatus(File file, Bitmap thumbnail) {
        this.file = file;
        this.thumbnail = thumbnail;
    }

    public File getFile() {
        return file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
