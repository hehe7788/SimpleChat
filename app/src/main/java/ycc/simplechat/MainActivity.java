package ycc.simplechat;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ycc.simplechat.adapter.ChatAudioAdapter;
import ycc.simplechat.javabean.ChatAudio;
import ycc.simplechat.javabean.MediaManager;
import ycc.simplechat.view.AudioRecorderButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity.Log";
    //下面三个好基友在使用listView时一般都一起出现
    private ListView mListView;
    private ArrayAdapter<ChatAudio> mAdapter;
    private List<ChatAudio> mAudioData = new ArrayList<>();

    private AudioRecorderButton mAudioRecorderButton;
    private View mAnimView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            }
        });

        mAdapter = new ChatAudioAdapter(this, mAudioData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //播放动画，若正有动画在播放，mAnimView不为空，先赋值为空，然后找到点击的item的mAnimView
                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                mAnimView = view.findViewById(R.id.item_anim);
                mAnimView.setBackgroundResource(R.drawable.bubble_anim);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                //播放音频
                MediaManager.playAudio(mAudioData.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                });
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
