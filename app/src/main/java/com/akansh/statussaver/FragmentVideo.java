package com.akansh.statussaver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FragmentVideo extends Fragment {
    View v;
    private RecyclerView recyclerView;
    private List<WAStatus> waStatusList;
    private Activity activity;
    private Context ctx;
    WSSVidAdapter wssVidAdapter;
    Utils sysUtils;
    private CardView msgCard;
    private TextView textView_msg;

    public FragmentVideo(Activity activity, Context ctx) {
        this.activity = activity;
        this.ctx = ctx;
        sysUtils=new Utils(ctx, activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wss_video_fragment,container,false);
        recyclerView=v.findViewById(R.id.video_statuses);
        msgCard=v.findViewById(R.id.msg_card_wss_vid);
        textView_msg=v.findViewById(R.id.textViewMsg_wvid);
        wssVidAdapter = new WSSVidAdapter(getContext(),waStatusList);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        wssVidAdapter.setWssItemListener(new WSSVidAdapter.WSSItemListener() {
            @Override
            public void onClickItem(View v, int position) {
                sysUtils.openFile(wssVidAdapter.getItem(position).getFile().getAbsolutePath(),"video/*");
            }

            @Override
            public void onSaveItem(View v, int position) {
                File file=wssVidAdapter.getItem(position).getFile();
                if(file.exists()) {
                    try {
                        File folder=new File(sysUtils.root+"/WAStatuses");
                        if(!folder.exists()) {
                            folder.mkdirs();
                        }
                        File dest = new File(sysUtils.root+"/WAStatuses/" + file.getName());
                        FileInputStream inputStream = new FileInputStream(file);
                        FileOutputStream outputStream = new FileOutputStream(dest);
                        FileChannel inChannel = inputStream.getChannel();
                        FileChannel outChannel = outputStream.getChannel();
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                        inputStream.close();
                        outputStream.close();
                        verify(dest);
                        sysUtils.showInfoSnackBar("Video saved to gallery!");
                    }catch(Exception e) {
                        sysUtils.showErrorSnackBar("Something went wrong!");
                    }
                }
            }

            @Override
            public void onShareItem(View v, int position) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri uri = Uri.parse(wssVidAdapter.getItem(position).getFile().getAbsolutePath());
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,uri);
                startActivity(Intent.createChooser(sharingIntent, "Share video"));
            }
        });
        recyclerView.setAdapter(wssVidAdapter);
        StatusLoader statusLoader=new StatusLoader(activity,Constants.R_STATUSES);
        statusLoader.execute();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        waStatusList=new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    public class StatusLoader extends AsyncTask<Void,WAStatus,Void> {

        Activity activity;
        int mode;

        public StatusLoader(Activity activity, int mode) {
            this.activity = activity;
            this.mode = mode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                FileFilter fileFilter=new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        if(file.getName().toLowerCase().endsWith(".mp4")) {
                            return true;
                        }
                        return false;
                    }
                };
                File folder;

                if(mode == Constants.R_STATUSES) {
                    folder = new File(sysUtils.root+"/WhatsApp/Media/.Statuses");
                }else if(mode == Constants.B_STATUSES) {
                    folder = new File(sysUtils.root+"/WhatsApp Business/Media/.Statuses");
                }else{
                    folder = new File(sysUtils.root+"/WhatsApp/Media/.Statuses");
                }
//                File[] folders={
//                        new File(sysUtils.root+"/WhatsApp/Media/.Statuses"),
//                        new File(sysUtils.root+"/WhatsApp Business/Media/.Statuses")
//                };
//                for(File folder : folders) {
                    if(folder.exists()) {
                        File[] files = folder.listFiles(fileFilter);
                        for (File file : files) {
                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
                            Bitmap thumb = mediaMetadataRetriever.getFrameAtTime(TimeUnit.SECONDS.toMillis(2));
                            final WAStatus dataProvider = new WAStatus(file, thumb);
                            publishProgress(dataProvider);
                        }
                    }
//                }
            }catch (Exception e) {
                Log.d(Constants.DEBUG,"LoadVideoError: "+e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(final WAStatus... values) {
            super.onProgressUpdate(values);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wssVidAdapter.addItem(values[0]);
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(wssVidAdapter.getItemCount()>0) {
                msgCard.setVisibility(View.GONE);
            }else{
                msgCard.setVisibility(View.VISIBLE);
                textView_msg.setText("No status found...");
            }
            super.onPostExecute(aVoid);
        }
    }

    public void verify(File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, file.getName());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DESCRIPTION, "Whatsapp Status Saved Via Status Saver By Akansh Sirohi");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        values.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        ContentResolver cr = ctx.getContentResolver();
        cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void clear() {
        wssVidAdapter.clearAll();
    }

    public void reload(int mode) {
        msgCard.setVisibility(View.VISIBLE);
        textView_msg.setText("Loading Please Wait...");
        StatusLoader statusLoader=new StatusLoader(activity,mode);
        statusLoader.execute();
    }
}
