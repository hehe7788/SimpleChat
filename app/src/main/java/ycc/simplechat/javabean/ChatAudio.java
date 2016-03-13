package ycc.simplechat.javabean;

/**
 * Created by L.Y.C on 2016/3/13.
 */
public class ChatAudio {
    private float mTime;
    private String mFilePath;

    public ChatAudio(float time, String filePath) {
        mTime = time;
        mFilePath = filePath;
    }

    public float getTime() {
        return mTime;
    }

    public void setTime(float time) {
        mTime = time;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }
}
