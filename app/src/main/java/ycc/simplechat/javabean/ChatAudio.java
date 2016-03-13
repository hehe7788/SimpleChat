package ycc.simplechat.javabean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by L.Y.C on 2016/3/13.
 */
public class ChatAudio {
    private static final String JSON_TIME = "time";
    private static final String JSON_FILEPATH = "filePath";
    private float mTime;
    private String mFilePath;

    public ChatAudio(float time, String filePath) {
        mTime = time;
        mFilePath = filePath;
    }
    /**
     *
     * @param json 载入ChatAudio的json中的信息
     * @throws JSONException
     */
    public ChatAudio(JSONObject json) throws JSONException {
        mTime = (float) json.getDouble(JSON_TIME);
        mFilePath = json.getString(JSON_FILEPATH);
    }

    //信息存入json
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_TIME, mTime);
        json.put(JSON_FILEPATH, mFilePath);
        return json;
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
