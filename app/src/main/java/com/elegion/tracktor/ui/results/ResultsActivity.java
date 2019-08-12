package com.elegion.tracktor.ui.results;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.elegion.tracktor.common.SingleFragmentActivity;
import com.elegion.tracktor.event.OpenResultEvent;
import com.elegion.tracktor.event.StartActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * `
 */
public class ResultsActivity extends SingleFragmentActivity {
    public static final String RESULT_ID = "RESULT_ID";
    public static final long LIST_ID = -1L;

    public static void start(@NonNull Context context, long resultId) {
        Intent intent = new Intent(context, ResultsActivity.class);
        intent.putExtra(RESULT_ID, resultId);

        context.startActivity(intent);
    }

    @Override
    protected Fragment getFragment() {
        long resultId = getIntent().getLongExtra(RESULT_ID, LIST_ID);

        if (resultId != LIST_ID)
            return ResultsDetailsFragment.newInstance(resultId);
        else
            return ResultsFragment.newInstance();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenResultEvent(OpenResultEvent event) {
        changeFragment(ResultsDetailsFragment.newInstance(event.getTrackId()));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void shareFromHolder(StartActivityEvent event){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_TEXT, event.getValue());
        startActivity(Intent.createChooser(intent, "Результаты маршрута"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}
