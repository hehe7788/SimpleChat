package ycc.simplechat.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ycc.simplechat.R;
import ycc.simplechat.javabean.ChatAudio;
import java.util.List;

/**
 * Created by L.Y.C on 2016/3/7.
 */
public class ChatAudioAdapter extends ArrayAdapter<ChatAudio> {

    private final int mMaxItemWidth;
    private final int mMinItemWidth;
    private final LayoutInflater mInflater;

    public ChatAudioAdapter(Context context, List<ChatAudio> audioData) {
        super(context, -1, audioData);

        mInflater = LayoutInflater.from(context);
        //通过windowManager获取屏幕宽度
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    //重写getView

    /**
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chat, parent, false);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.item_time);
            holder.bubble = convertView.findViewById(R.id.item_bubble);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //what?
        holder.time.setText(Math.round(getItem(position).getTime()) + "\"");
        ViewGroup.LayoutParams lp = holder.bubble.getLayoutParams();
        lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * getItem(position).getTime()));
        return convertView;
    }

    //ViewHolder是干嘛？
    private class ViewHolder {
        TextView time;
        View bubble;
    }
}
