package com.elegion.tracktor.ui.results;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.event.OpenResultEvent;
import com.elegion.tracktor.event.StartActivityEvent;
import com.elegion.tracktor.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Azret Magometov
 */
public class ResultHolder extends RecyclerView.ViewHolder {

    private final View conLayout;
    private View mView;
    private TextView mDistanceText;
    private TextView mDuration;
    private TextView mDate;
    private TextView mEnergyText;
    private TextView mSpeedText;
    private TextView mAtivityText;
    private TextView mCommentText;
    private Button shareBtn;
    private Button mEditBtn;


    public ResultHolder(View view) {
        super(view);
        mView = view;
        mDistanceText = view.findViewById(R.id.li_dist_con);
        mDate = view.findViewById(R.id.li_tv_date);
        mDuration = view.findViewById(R.id.li_dur_con);
        conLayout = view.findViewById(R.id.sub_item_con);
        mEnergyText = view.findViewById(R.id.li_energy_con);
        mSpeedText = view.findViewById(R.id.li_speed_con);
        mAtivityText = view.findViewById(R.id.li_type_con);
        mCommentText = view.findViewById(R.id.li_com_con);
        shareBtn = view.findViewById(R.id.li_btn_share_con);
        mEditBtn = view.findViewById(R.id.edit_btn);

    }

    @Override
    public String toString() {
        return super.toString() + " '" + mDistanceText.getText() + "'";
    }

    public void bind(Track track) {
        mDistanceText.setText(StringUtil.getDistanceText(track.getDistance()));
        mDate.setText(StringUtil.getDateText(track.getDate()));
        mDuration.setText(StringUtil.getTimeText(track.getDuration()));
        mAtivityText.setText(track.getType());
        mEnergyText.setText(StringUtil.getEnergyText(track.getEnergy()));
        mCommentText.setText(track.getComment());
        mSpeedText.setText(StringUtil.getSpeedText(track.getDistance() / track.getDuration()));

        boolean expanded = track.isExpanded();
        conLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);




        mEditBtn.setOnClickListener(v -> {
            EventBus.getDefault().post(new OpenResultEvent(track.getId()));
        });
        shareBtn.setOnClickListener(v -> {
            String value = "Время: " + mDuration.getText() + "\nРасстояние: " + mDistanceText.getText()
                    + "\nСкорость: " + mSpeedText.getText() + "\nЗатрачено энергии: " + mEnergyText.getText() + "\nКомментарий: " + mCommentText.getText();

            EventBus.getDefault().post(new StartActivityEvent(value));

        });



    }


}
