package ycc.simplechat.javabean;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by L.Y.C on 2016/3/6.
 */
public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    private boolean isPrepared;
    private static AudioManager mInstance;

    private AudioManager(String dir) {
        mDir = dir;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * 回调
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    private AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }
    /**
     * 同步？？
     * @return AudioManager
     */
    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * end prepare后需要callback to button 然后button让dialog出现
     */
    public void prepareAudio() {
        isPrepared = false;
        File dir = new File(mDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = generateFileName();
        File file = new File(dir, fileName);
        mCurrentFilePath = file.getAbsolutePath();

        mMediaRecorder = new MediaRecorder();
        //设置输出文件
        mMediaRecorder.setOutputFile(mCurrentFilePath);
        //音频源为麦克
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //音频格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //音频编码为amr
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 随机生成文件名 文件后缀为amr
     * @return String
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public void cancel(){
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    /**
     * release 之后需要call back to Activity
     */
    public void release(){
        isPrepared = false;
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;

    }

    public int getVolumeLevel(int maxLevel) {
        if (isPrepared) {
            try {
                //getMaxAmplitude() return 1 ~ 32767
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
