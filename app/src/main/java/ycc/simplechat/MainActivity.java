package ycc.simplechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ycc.simplechat.view.AudioRecorderButton;

public class MainActivity extends AppCompatActivity {
    //下面三个好基友在使用listView时一般都一起出现
    private ListView mListView;
    private ArrayAdapter<ChatAudio> mAdapter;
    private List<ChatAudio> mAudioData = new ArrayList<>();

    private AudioRecorderButton mAudioRecorderButton;
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
                mListView.setSelection(mAudioData.size()-1);
            }
        });

        mAdapter = new ChatAudioAdapter(this, mAudioData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo 播放音频，动画
                View animView = view.findViewById(R.id.item_anim);
                animView.setBackgroundResource(R.drawable.bubble_anim);
            }
        });


    }

    class ChatAudio {
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

}
