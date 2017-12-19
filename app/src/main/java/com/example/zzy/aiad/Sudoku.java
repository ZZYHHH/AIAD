package com.example.zzy.aiad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;


import com.example.zzy.aiad.DragGridView.OnItemChangerListener;

public class Sudoku extends Activity  implements View.OnClickListener {

    DragGridView dragGridView;
    SimpleAdapter adapter;
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();

    private SurfaceView surfaceview;
    private MediaPlayer mediaPlayer;
    private ImageButton start;
    private ImageButton pause;
    private SeekBar seekBar;
    private Button next;
    private boolean isPlaying;
    private int currentPosition = 0;

    private int postion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        initView();
        initAdapter();
        initListener();
    }

    private void initView() {
        dragGridView = (DragGridView) findViewById(R.id.myDragGridView);

        surfaceview = (SurfaceView) findViewById(R.id.surfaceView);
        start = (ImageButton) findViewById(R.id.video_start);
        pause = (ImageButton) findViewById(R.id.video_pause);
        next=findViewById(R.id.next);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        mediaPlayer = new MediaPlayer();
        surfaceview.getHolder().setKeepScreenOn(true);
        surfaceview.getHolder().addCallback(new SurfaceViewLis());
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        seekBar.setOnClickListener(this);

    }

    private void initListener() {
        // 交互数据
        dragGridView.setOnItemChangeListener(new OnItemChangerListener() {

            @Override
            public void onChange(int from, int to) {
                HashMap<String, Object> temp = data.get(from);
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(data, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(data, i, i - 1);
                    }
                }

                data.set(to, temp);
                adapter.notifyDataSetChanged();
            }

        });

        dragGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(Sudoku.this, "您点击了" + position + "位置",
                        Toast.LENGTH_LONG).show();
                video_play(0);
            }
        });
    }
    private void initAdapter() {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.ic_launcher_background);
        Bitmap bitmap = drawable.getBitmap();

        for (int i = 0; i < 9; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("item_img1", bitmap);
            map.put("item_tv1", "第" + i + "项");
            data.add(map);
        }

        adapter = new SimpleAdapter(Sudoku.this, data,
                R.layout.gridview_item, new String[]{"item_img1", "item_tv1"},
                new int[]{R.id.item_img1, R.id.item_tv1});

        adapter.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub

                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageBitmap((Bitmap) data);
                    return true;
                } else {
                    return false;
                }
            }
        });

        dragGridView.setAdapter(adapter);

    }


    private class SurfaceViewLis implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (currentPosition > 0) {
                // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
                video_play(currentPosition);
                currentPosition = 0;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }

        }

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_start:
                video_play(0);
                break;
            case R.id.video_pause:
                pause();
                break;


            default:
                break;
        }
    }
    /* 开始播放*/
    protected void video_play(final int msec) {
//		// 获取视频文件地址
        try {
            mediaPlayer = new MediaPlayer();
            //设置音频流类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            AssetFileDescriptor fd = this.getAssets().openFd("3.mp4");
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
                    fd.getLength());
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(surfaceview.getHolder());//这一步是关键，制定用于显示视频的SurfaceView对象（通过setDisplay（））

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();

                    // 按照初始位置播放
                    mediaPlayer.seekTo(msec);
                    // 设置进度条的最大进度为视频流的最大播放时长
                    seekBar.setMax(mediaPlayer.getDuration());
                    // 开始线程，更新进度条的刻度
                    new Thread() {

                        @Override
                        public void run() {
                            try {
                                isPlaying = true;
                                while (isPlaying) {
                                    int current = mediaPlayer
                                            .getCurrentPosition();
                                    seekBar.setProgress(current);

                                    sleep(500);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    start.setEnabled(false);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    start.setEnabled(true);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    video_play(0);
                    isPlaying = false;
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    protected void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }
}