package com.age;
import android.widget.MediaController;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageButton;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;
import android.view.LayoutInflater;
import android.util.Log;
import java.util.Locale;
import java.util.Formatter;
import android.widget.Toast;
import android.view.KeyEvent;

public class MyMediaController extends MediaController {
    private MediaPlayerControl mPlayer;//这个播放器
    private Context mContext;//主视图
    private View mRoot;//制作控制器视图
    private View mAnchor;//设置锚视图
    private ProgressBar mProgress;//进度条
    private TextView mEndTime, mCurrentTime;//当前播放时间，视频总时长
    private boolean mDragging;
    private boolean mShowing;
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
	@Override
    public MyMediaController(Context context) {
        super(context);
        mContext = context;
    }

//设置媒体播放器
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        mPlayer = player;
    }
//设置锚视图
	@Override
    public void setAnchorView(View view) 
    {
        super.setAnchorView(view);
        mAnchor=view;
        LayoutParams frameParams = new LayoutParams(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT);
        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }
    //制作控制器视图
    protected View makeControllerView() 
    {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.mymedia_controller,null);
        initControllerView(mRoot);
        return mRoot;
    }
//初始化控制器视图
    private void initControllerView(View v) 
    {
        mProgress = (ProgressBar) v.findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setMax(mPlayer.getDuration());
                seeker.setKeyProgressIncrement(8000);
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
        }
        mEndTime = (TextView) v.findViewById(R.id.time);
        mCurrentTime = (TextView) v.findViewById(R.id.time_current);
		//tv_name=v.findViewById(R.id.tv_name);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }
	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
			&& event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode ==  KeyEvent.KEYCODE_HEADSETHOOK
			|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
			|| keyCode == KeyEvent.KEYCODE_SPACE
			|| keyCode == KeyEvent.KEYCODE_ENTER
			|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (uniqueDown) {
                z暂停或恢复();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY ) {//键盘播放健
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP //键盘停止键
				   || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {//键盘暂停键 
			  if (uniqueDown && mPlayer.isPlaying()) {
				  mPlayer.pause();
				  show(sDefaultTimeout);
			  }
			  return true;
		  } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN //声音下调键
					 || keyCode == KeyEvent.KEYCODE_VOLUME_UP   //声音上调键
					 || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE //静音键
					 || keyCode == KeyEvent.KEYCODE_CAMERA) {   //打开相机键
			  // don't show the controls for volume adjustment
			  return super.dispatchKeyEvent(event);
		  } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
			  if (uniqueDown) {
				  hide();
			  }
			  return true;
		  }
		  show(sDefaultTimeout);
		  return super.dispatchKeyEvent(event);
		}
//暂停或恢复
	void z暂停或恢复() 
    {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();}
        else {mPlayer.start();}
    }
//显示控制器
    @Override
    public void show(int timeout) {
        super.show(timeout);
        if (!mShowing && mAnchor != null) {
            setProgress();
            mShowing=true;
        }
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }
//隐藏
	@Override
    public void hide() {
        super.hide();
        if (mAnchor == null)
            return;
        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
            } catch (IllegalArgumentException ex) {
                Log.w("MediaController", "already removed");
            }
            mShowing = false;
        }
    }
    private Handler mHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) 
        {
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    if (!mDragging && mShowing && mPlayer.isPlaying()) {
                        setProgress();
                        mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
//把时间格式化为字符串输出
    private String stringForTime(int timeMs) 
    {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds)
                .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
//设定进度
	private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();//获取当前播放的位置。
        int duration = mPlayer.getDuration();//获取当前播放视频的总长度。
        if (mProgress != null) {
            if (duration > 0) {
                mProgress.setProgress(position);
            }
            int percent = mPlayer.getBufferPercentage();//获取缓冲百分比
            mProgress.setSecondaryProgress(percent*duration);//设置第二进度
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }
	private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        //开始跟踪触摸
        public void onStartTrackingTouch(SeekBar bar) 
        {
            show(3600000);
            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
        }
//进度发生变化
        public void onProgressChanged(SeekBar bar, int progress,boolean fromuser) 
        {
            if (!fromuser) {return;}
            mPlayer.seekTo(progress);//从第几毫秒开始播放。
            if (mCurrentTime != null) 
            {
                mCurrentTime.setText(stringForTime(progress));
            }
        }
//停止跟踪触摸
        public void onStopTrackingTouch(SeekBar bar) 
        {
            mDragging = false;
            setProgress();
            show(sDefaultTimeout);
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };
}
