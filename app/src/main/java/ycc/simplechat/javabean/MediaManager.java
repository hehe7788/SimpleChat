package ycc.simplechat.javabean;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by L.Y.C on 2016/3/7.
 */
public class MediaManager {

    private static final String TAG = "MediaManager";
    private static MediaPlayer sMediaPlayer;

    private static boolean sIsPause;

    public static void playAudio(String path, MediaPlayer.OnCompletionListener listener) {
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
            sMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    sMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            sMediaPlayer.reset();
        }
        try {
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            sMediaPlayer.setOnCompletionListener(listener);
            sMediaPlayer.setDataSource(path);
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void stopAudio() {
        if (sMediaPlayer != null) {
                sMediaPlayer.setOnCompletionListener(null);
                sMediaPlayer.stop();
        }
    }
    public static void pause() {
        if (sMediaPlayer != null && sMediaPlayer.isPlaying()) {
            sMediaPlayer.pause();
            sIsPause = true;
        }
    }

    public static void resume() {
        if (sMediaPlayer != null && sIsPause) {
            sMediaPlayer.start();
            sIsPause = false;
        }
    }

    public static void release() {
        if (sMediaPlayer != null) {
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
    }

}
