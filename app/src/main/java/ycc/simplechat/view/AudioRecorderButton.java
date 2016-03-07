package ycc.simplechat.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import ycc.simplechat.R;

public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener{

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_READY_CANCEL = 3;

    private static final int DISTANCE_Y_CANCEL = 50;
    private static final String TAG = "AudioRecorderButton.Log";
    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOLUME_CHANGED = 0x111;
    private static final int MSG_DIALOG_DIMISS = 0x112;


    private int mCurState = STATE_NORMAL;
    private DialogManager mDialogManager;
    private boolean isRecording = false;
    private boolean isLongClicked = false;

    private AudioManager mAudioManager;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());

        String dir = Environment.getExternalStorageDirectory() + "/simple_chat_audios";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG, "longClick");
                //prepareAudio()成功后会回调wellprepared方法
                isLongClicked = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    /**
     * 计时与音量
     */
    private float mTime;
    private Runnable mGetVolumeLevelRunnable = new Runnable() {
        @Override
        public void run() {
            //定时获取音量大小,计时
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOLUME_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示for test 真正显示在audio end prepared后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    //开启线程为了定时检测音量
                    new Thread(mGetVolumeLevelRunnable).start();
                    break;
                case MSG_VOLUME_CHANGED:
                    mDialogManager.updateVolume(mAudioManager.getVolumeLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dismissDialog();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (readyCancel(x, y)) {
                        changeState(STATE_READY_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP");
                if (!isLongClicked) {
                    Log.e(TAG, "!isLongClicked");
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.6f) {
                    Log.e(TAG, "!isRecording || mTime < 0.6f");
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    // TODO 此时应该显示对话框，里面是感叹号图片，在3.3秒后消失， 然而什么都没有
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 3300);
                } else if (mCurState == STATE_RECORDING) {
                    Log.e(TAG, "mCurState == STATE_RECORDING");
                    //TODO 正常录制结束 releae callbacktoactivity
                    mDialogManager.dismissDialog();
                    mAudioManager.release();

                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }
                } else if (mCurState == STATE_READY_CANCEL) {
                    Log.e(TAG, "mCurState == STATE_READY_CANCEL");
                    //cancel
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * recovery
     */
    private void reset() {
        isRecording = false;
        isLongClicked = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    private boolean readyCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        //从上超出和从下超出
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    /**
     * 改变button的样式
     * @param state int
     */
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.recorder_normal);
                    break;

                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_READY_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.recorder_ready_cancel);
                    mDialogManager.readyCancel();
                    break;

            }
        }
    }


}