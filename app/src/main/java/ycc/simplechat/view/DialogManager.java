package ycc.simplechat.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ycc.simplechat.R;

/**
 * Created by L.Y.C on 2016/3/5.
 */
public class DialogManager {
    private static final String TAG = "DialogManager.Log";
    private Dialog mDialog = null;

    private ImageView mIcon;
    private ImageView mVolume;
    private TextView mLabel;

    private Context mContext;

    public DialogManager(Context context) {
        mContext = context;
    }

    public void  showRecordingDialog() {
        if (mDialog == null) {
            Log.e(TAG, "DialogManager.showRecordingDialog()");
            mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog, null);
            mDialog.setContentView(view);
            mIcon = (ImageView) view.findViewById(R.id.dialog_recorder_icon);
            mVolume = (ImageView) view.findViewById(R.id.dialog_volume);
            mLabel = (TextView) view.findViewById(R.id.dialog_label);

            Log.e(TAG, mDialog.getContext().toString());
            mDialog.show();
        }
    }
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            Log.e(TAG, "DialogManager.recording()");

            mIcon.setVisibility(View.VISIBLE);
            mVolume.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLabel.setText(R.string.dialog_up_cancel);
        }
    }
    public void readyCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            Log.e(TAG, "DialogManager.readyCancel()");

            mIcon.setVisibility(View.VISIBLE);
            mVolume.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText(R.string.dialog_release_cancel);
        }
    }

    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            Log.e(TAG, "DialogManager.tooShort()");
            mIcon.setVisibility(View.VISIBLE);
            mVolume.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.too_short);
            mLabel.setText(R.string.dialog_too_short);
        }
    }

    public void dismissDialog() {
        Log.e(TAG, "DialogManager.dismissDialog()");

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 通过level值更新volume资源图片
     * @param level 1-7
     */
    public void updateVolume(int level) {
        if (mDialog != null && mDialog.isShowing()) {

            int resId = mContext.getResources().getIdentifier("v"+ level,
                    "drawable", mContext.getPackageName());
            mVolume.setImageResource(resId);
        }
    }

}

