package ycc.simplechat;

import android.graphics.LinearGradient;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ycc.simplechat.adapter.ChatAudioAdapter;
import ycc.simplechat.javabean.ChatAudio;
import ycc.simplechat.javabean.ChatAudioJSONSerializer;
import ycc.simplechat.javabean.MediaManager;
import ycc.simplechat.view.AudioRecorderButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity.Log";
    //下面三个好基友在使用listView时一般都一起出现
    private ListView mListView;
    private ArrayAdapter<ChatAudio> mAdapter;
//    private List<ChatAudio> mAudioData = new ArrayList<>();
    private List<ChatAudio> mAudioData;
    private ChatAudioJSONSerializer mSerializer;

    private AudioRecorderButton mAudioRecorderButton;
    private View mAnimView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fileName = "audio.json";
        mSerializer = new ChatAudioJSONSerializer(getApplicationContext(), fileName);
        try {
            mAudioData = mSerializer.loadAudios();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mListView = (ListView) findViewById(R.id.chat_list_view);
        mAudioRecorderButton = (AudioRecorderButton) findViewById(R.id.record_button);

        //不理解这种回调方式
        mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                ChatAudio chatAudio = new ChatAudio(seconds, filePath);
                mAudioData.add(chatAudio);
                mAdapter.notifyDataSetChanged();
                //定位到显示最后一个
                mListView.setSelection(mAudioData.size() - 1);
                //save
                try {
                    mSerializer.saveAudios((ArrayList<ChatAudio>) mAudioData);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mAdapter = new ChatAudioAdapter(this, mAudioData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //若正有动画在播放，mAnimView不为空，先赋值为空(即停止动画）
                View preAnimView = null;
                if (mAnimView != null) {
                    Log.e(TAG,  "preAnimView " + preAnimView + "mAnimView " + mAnimView);
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    preAnimView = mAnimView;
                    mAnimView = null;
                }

                //找到点击的item的mAnimView
                mAnimView = view.findViewById(R.id.item_anim);
                //点击正在播放的音频则停止音频，停止动画
                if (preAnimView != null && mAnimView == preAnimView) {
                    Log.e(TAG, "mAnimView == preAnimView");
                    //stop the audio
                    MediaManager.stopAudio();
                    mAnimView = null;
                } else { //正常播放
                    Log.e(TAG, "else");
                    mAnimView.setBackgroundResource(R.drawable.bubble_anim);
                    AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                    anim.start();
                    //播放音频，完成后停止动画
                    MediaManager.playAudio(mAudioData.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mAnimView.setBackgroundResource(R.drawable.adj);
                            mAnimView = null;
                        }
                    });
                }
            }
        });
    }

    //重写生命周期方法
    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
        Log.e(TAG, "onResume");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        Log.e(TAG, "onDestroy");

    }

}
