package com.example.apollovideoslider.fragments;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.File;
import java.util.concurrent.TimeUnit;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.apollovideoslider.App;
import com.example.apollovideoslider.R;
public class fragment_movies extends Fragment implements CacheListener{
    private VideoView myvideo;
    private ImageView myimage;
    private String URLvideos;
    private String URLimages;
    private TextView textViewtest;
    private static int oTime =0, sTime =0, eTime =0, fTime = 5000, bTime = 5000;
    private Handler hdlr = new Handler();
    //test
    private HttpProxyCacheServer proxy;
    public static boolean VIDEO_CATCHED = activity_fragment_view_pager.VIDEO_CATCHED1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            URLvideos = getArguments().getString(activity_fragment_view_pager.URLVIDEOS_KEY,"No Url");
            URLimages = getArguments().getString(activity_fragment_view_pager.URLIMAGES_KEY,"No Url");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // CREANDO LAS IMAGENES
        myimage = (ImageView)view.findViewById(R.id.myimg);
        Glide.with(this).load(URLimages).diskCacheStrategy(DiskCacheStrategy.ALL).into(myimage);
        //
        if(VIDEO_CATCHED == true){
            System.out.println("esta entrando 0");
            myimage.setVisibility(View.GONE);
        }else{
            System.out.println("no esta entrando 0");
            myimage.setVisibility(View.VISIBLE);
        }

        // CREANDO LOS VIDEOS
        /*
        Uri uri=Uri.parse(URLvideos);
        myvideo = (VideoView)view.findViewById(R.id.videoView2);
        myvideo.setVideoURI(uri);
        textViewtest = (TextView)view.findViewById(R.id.textViewtest);
         */
        //videos 2

        proxy = App.getProxy(getActivity());
        System.out.println("Este es el proxy"+proxy.isCached(URLvideos));
        myvideo = (VideoView)view.findViewById(R.id.videoView2);
        proxy.registerCacheListener((CacheListener) this, URLvideos);
        String proxyUrl = proxy.getProxyUrl(URLvideos);
        myvideo.setVideoPath(proxyUrl);
        textViewtest = (TextView)view.findViewById(R.id.textViewtest);
        //
        if(proxy.isCached(URLvideos)){
            VIDEO_CATCHED = true;
            System.out.println("esta entrando");
        }else{
            VIDEO_CATCHED = false;
            System.out.println("no esta entrando");
        }
        myvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //myimage.setVisibility(View.VISIBLE);
                mp.setLooping(true);
                mp.start();
                eTime = mp.getDuration();
                sTime = mp.getCurrentPosition();
                if(oTime == 0){
                    System.out.println(eTime);
                    oTime =1;
                }
                hdlr.postDelayed(UpdateVideoTime, 100);
                if(activity_fragment_view_pager.MY_TEXTO == "pause"){
                    if(VIDEO_CATCHED == true){
                        myimage.setVisibility(View.GONE);
                    }else{
                        myimage.setVisibility(View.VISIBLE);
                    }
                    mp.pause();
                }
            }
        });
    }
    private Runnable UpdateVideoTime = new Runnable() {
        @Override
        public void run() {
            sTime = myvideo.getCurrentPosition();
            textViewtest.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime),
                    TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))) );
            //System.out.println("*************timer1   "+ TimeUnit.MILLISECONDS.toMillis(sTime) );
            if( TimeUnit.MILLISECONDS.toMillis(sTime) > 200){
                //System.out.println("*************hola   ");
                myimage.setVisibility(View.GONE);
            }
            if( TimeUnit.MILLISECONDS.toMillis(sTime) == 0){
                if(VIDEO_CATCHED == true){
                    myimage.setVisibility(View.GONE);
                }else{
                    myimage.setVisibility(View.VISIBLE);
                }

            }
            hdlr.postDelayed(this, 100);
        }
    };

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        //setCachedState(percentsAvailable == 100);
    }
    private void setCachedState(boolean cached) {
        int statusIconId = cached ? R.drawable.ic_launcher_background : R.drawable.ic_launcher_background;
        //cacheStatusImageView.setImageResource(statusIconId);
    }
}
