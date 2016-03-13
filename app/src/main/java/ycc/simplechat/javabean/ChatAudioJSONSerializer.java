package ycc.simplechat.javabean;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Created by L.Y.C on 2016/3/13.
 */
public class ChatAudioJSONSerializer {
    private static final String TAG = "ChatAudioJSONSerializer";
    private Context mContext;
    //mFilename是存储文件名
    private String mFilename;

    public ChatAudioJSONSerializer(Context context, String filename) {
        mContext = context;
        mFilename = filename;
    }

    public ArrayList<ChatAudio> loadAudios() throws IOException, JSONException{
        Log.e(TAG, "loadAudios");
        ArrayList<ChatAudio> audios = new ArrayList<>();
        BufferedReader reader = null;
        try {
            File sdCardDictionary = new File(Environment.getExternalStorageDirectory() + "/simple_chat_audios");
            File sdCardFile = new File(sdCardDictionary + "/" + mFilename);

            FileInputStream in = new FileInputStream(sdCardFile);
            reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder jsonString= new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for (int i = 0; i < array.length(); i++) {
                audios.add(new ChatAudio(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return audios;
    }

    public void saveAudios(ArrayList<ChatAudio> audios) throws JSONException, IOException {
        Log.e(TAG, "saveAudios");

        JSONArray array = new JSONArray();
        for (ChatAudio a : audios) {
            array.put(a.toJSON());
        }
        // same with dir in AudioRecorderButton
        File sdCardDictionary = new File(Environment.getExternalStorageDirectory() + "/simple_chat_audios");
        File sdCardFile = new File(sdCardDictionary + "/" + mFilename);
        if (!sdCardDictionary.exists()) {
            sdCardDictionary.mkdirs();
        }

        Writer writer = null;
        try {
            FileOutputStream out = new FileOutputStream(sdCardFile);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
