//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.linlin.videodemo4.media;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.lang.reflect.Method;

public class MediaController extends FrameLayout{
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private MediaController.MediaPlayerControl mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private SeekBar mProgress;
    private TextView mEndTime;
    private TextView mCurrentTime;
    private TextView mInfoView;
    private String mTitle;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = false;
    private boolean mFromXml = false;
    private ImageButton mPauseButton;
    private AudioManager mAM;
    private MediaController.OnShownListener mShownListener;
    private MediaController.OnHiddenListener mHiddenListener;

    @SuppressLint({"HandlerLeak"})
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    MediaController.this.hide();
                    break;
                case 2:
                    long pos = MediaController.this.setProgress();
                    if(!MediaController.this.mDragging && MediaController.this.mShowing) {
                        msg = this.obtainMessage(2);
                        this.sendMessageDelayed(msg, 1000L - pos % 1000L);
                        MediaController.this.updatePausePlay();
                    }
            }

        }
    };
    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            MediaController.this.doPauseResume();
            MediaController.this.show(3000);
        }
    };
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            MediaController.this.mDragging = true;
            MediaController.this.show(3600000);
            MediaController.this.mHandler.removeMessages(2);
            if(MediaController.this.mInstantSeeking) {
                MediaController.this.mAM.setStreamMute(3, true);
            }

            if(MediaController.this.mInfoView != null) {
                MediaController.this.mInfoView.setText("");
                MediaController.this.mInfoView.setVisibility(View.VISIBLE);
            }

        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if(fromuser) {
                long newposition = MediaController.this.mDuration * (long)progress / 1000L;
                String time = StringUtils.generateTime(newposition);
                if(MediaController.this.mInstantSeeking) {
                    MediaController.this.mPlayer.seekTo(newposition);
                }

                if(MediaController.this.mInfoView != null) {
                    MediaController.this.mInfoView.setText(time);
                }

                if(MediaController.this.mCurrentTime != null) {
                    MediaController.this.mCurrentTime.setText(time);
                }

            }
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if(!MediaController.this.mInstantSeeking) {
                MediaController.this.mPlayer.seekTo(MediaController.this.mDuration * (long)bar.getProgress() / 1000L);
            }

            if(MediaController.this.mInfoView != null) {
                MediaController.this.mInfoView.setText("");
                MediaController.this.mInfoView.setVisibility(View.GONE);
            }

            MediaController.this.show(3000);
            MediaController.this.mHandler.removeMessages(2);
            MediaController.this.mAM.setStreamMute(3, false);
            MediaController.this.mDragging = false;
            MediaController.this.mHandler.sendEmptyMessageDelayed(2, 1000L);
        }
    };

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRoot = this;
        this.mFromXml = true;
        this.initController(context);
    }

    public MediaController(Context context) {
        super(context);
        if(!this.mFromXml && this.initController(context)) {
            this.initFloatingWindow();
        }

    }

    private boolean initController(Context context) {
        this.mContext = context;
        this.mAM = (AudioManager)this.mContext.getSystemService(Context.AUDIO_SERVICE);
        return true;
    }

    public void onFinishInflate() {
        if(this.mRoot != null) {
            this.initControllerView(this.mRoot);
        }

    }

    private void initFloatingWindow() {
        this.mWindow = new PopupWindow(this.mContext);
        this.mWindow.setFocusable(false);
        this.mWindow.setBackgroundDrawable((Drawable)null);
        this.mWindow.setOutsideTouchable(true);
        this.mAnimStyle = 16973824;
    }

    @TargetApi(16)
    public void setWindowLayoutType() {
        if(VERSION.SDK_INT >= 14) {
            try {
                this.mAnchor.setSystemUiVisibility(512);
                Method e = PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{Integer.TYPE});
                e.invoke(this.mWindow, new Object[]{Integer.valueOf(1003)});
            } catch (Exception var2) {
                Log.e("setWindowLayoutType", var2.getMessage());
            }
        }

    }

    public void setAnchorView(View view) {
        this.mAnchor = view;
        if(!this.mFromXml) {
            this.removeAllViews();
            this.mRoot = this.makeControllerView();
            this.mWindow.setContentView(this.mRoot);
            this.mWindow.setWidth(-1);
            this.mWindow.setHeight(-2);
        }

        this.initControllerView(this.mRoot);
    }

    protected View makeControllerView() {
        return ((LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.getResources().getIdentifier("mediacontroller", "layout", this.mContext.getPackageName()), this);
    }

    private void initControllerView(View v) {
        this.mPauseButton = (ImageButton)v.findViewById(this.getResources().getIdentifier("mediacontroller_play_pause", "id", this.mContext.getPackageName()));
        if(this.mPauseButton != null) {
            this.mPauseButton.requestFocus();
            this.mPauseButton.setOnClickListener(this.mPauseListener);
        }

        this.mProgress = (SeekBar)v.findViewById(this.getResources().getIdentifier("mediacontroller_seekbar", "id", this.mContext.getPackageName()));
        if(this.mProgress != null) {
            if(this.mProgress instanceof SeekBar) {
                SeekBar seeker = this.mProgress;
                seeker.setOnSeekBarChangeListener(this.mSeekListener);
            }

            this.mProgress.setMax(1000);
        }

        this.mEndTime = (TextView)v.findViewById(this.getResources().getIdentifier("mediacontroller_time_total", "id", this.mContext.getPackageName()));
        this.mCurrentTime = (TextView)v.findViewById(this.getResources().getIdentifier("mediacontroller_time_current", "id", this.mContext.getPackageName()));

    }

    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        this.mPlayer = player;
        this.updatePausePlay();
    }

    public void setInstantSeeking(boolean seekWhenDragging) {
        this.mInstantSeeking = seekWhenDragging;
    }

    public void show() {
        this.show(3000);
    }


    public void setInfoView(TextView v) {
        this.mInfoView = v;
    }

    public void setAnimationStyle(int animationStyle) {
        this.mAnimStyle = animationStyle;
    }

    public void show(int timeout) {
        if(!this.mShowing && this.mAnchor != null && this.mAnchor.getWindowToken() != null) {
            if(this.mPauseButton != null) {
                this.mPauseButton.requestFocus();
            }

            if(this.mFromXml) {
                this.setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];
                this.mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1], location[0] + this.mAnchor.getWidth(), location[1] + this.mAnchor.getHeight());
                this.mWindow.setAnimationStyle(this.mAnimStyle);
                this.setWindowLayoutType();
                this.mWindow.showAtLocation(this.mAnchor, 0, anchorRect.left, anchorRect.bottom);
            }

            this.mShowing = true;
            if(this.mShownListener != null) {
                this.mShownListener.onShown();
            }
        }

        this.updatePausePlay();
        this.mHandler.sendEmptyMessage(2);
        if(timeout != 0) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), (long)timeout);
        }

    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public void hide() {
        if(this.mAnchor != null) {
            if(this.mShowing) {
                try {
                    this.mHandler.removeMessages(2);
                    if(this.mFromXml) {
                        this.setVisibility(View.GONE);
                    } else {
                        this.mWindow.dismiss();
                    }
                } catch (IllegalArgumentException var2) {
                    Log.d("Exception", "MediaController already removed");
                }

                this.mShowing = false;
                if(this.mHiddenListener != null) {
                    this.mHiddenListener.onHidden();
                }
            }

        }
    }

    public void setOnShownListener(MediaController.OnShownListener l) {
        this.mShownListener = l;
    }

    public void setOnHiddenListener(MediaController.OnHiddenListener l) {
        this.mHiddenListener = l;
    }

    private long setProgress() {
        if(this.mPlayer != null && !this.mDragging) {
            long position = this.mPlayer.getCurrentPosition();
            long duration = this.mPlayer.getDuration();
            if(this.mProgress != null) {
                if(duration > 0L) {
                    long percent = 1000L * position / duration;
                    this.mProgress.setProgress((int)percent);
                }

                int percent1 = this.mPlayer.getBufferPercentage();
                this.mProgress.setSecondaryProgress(percent1 * 10);
            }

            this.mDuration = duration;
            if(this.mEndTime != null) {
                this.mEndTime.setText(StringUtils.generateTime(this.mDuration));
            }

            if(this.mCurrentTime != null) {
                this.mCurrentTime.setText(StringUtils.generateTime(position));
            }

            return position;
        } else {
            return 0L;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.show(3000);
        return true;
    }

    public boolean onTrackballEvent(MotionEvent ev) {
        this.show(3000);
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if(event.getRepeatCount() != 0 || keyCode != 79 && keyCode != 85 && keyCode != 62) {
            if(keyCode == 86) {
                if(this.mPlayer.isPlaying()) {
                    this.mPlayer.pause();
                    this.updatePausePlay();
                }

                return true;
            } else if(keyCode != 4 && keyCode != 82) {
                this.show(3000);
                return super.dispatchKeyEvent(event);
            } else {
                this.hide();
                return true;
            }
        } else {
            this.doPauseResume();
            this.show(3000);
            if(this.mPauseButton != null) {
                this.mPauseButton.requestFocus();
            }

            return true;
        }
    }

    private void updatePausePlay() {
        if(this.mRoot != null && this.mPauseButton != null) {
            if(this.mPlayer.isPlaying()) {
                this.mPauseButton.setImageResource(this.getResources().getIdentifier("mediacontroller_pause", "drawable", this.mContext.getPackageName()));
            } else {
                this.mPauseButton.setImageResource(this.getResources().getIdentifier("mediacontroller_play", "drawable", this.mContext.getPackageName()));
            }

        }
    }

    private void doPauseResume() {
        if(this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
        } else {
            this.mPlayer.start();
        }

        this.updatePausePlay();
    }

    public void setEnabled(boolean enabled) {
        if(this.mPauseButton != null) {
            this.mPauseButton.setEnabled(enabled);
        }

        if(this.mProgress != null) {
            this.mProgress.setEnabled(enabled);
        }

        super.setEnabled(enabled);
    }


    public interface MediaPlayerControl {
        void start();

        void pause();

        long getDuration();

        long getCurrentPosition();

        void seekTo(long var1);

        boolean isPlaying();

        int getBufferPercentage();
    }

    public interface OnHiddenListener {
        void onHidden();
    }

    public interface OnShownListener {
        void onShown();
    }
}
