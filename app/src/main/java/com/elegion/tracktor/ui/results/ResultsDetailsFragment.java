package com.elegion.tracktor.ui.results;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.di.ModelsModule;
import com.elegion.tracktor.util.ScreenshotMaker;
import com.elegion.tracktor.util.StringUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import toothpick.Scope;
import toothpick.Toothpick;

import static com.elegion.tracktor.ui.results.ResultsActivity.RESULT_ID;

/**
 * @author Azret Magometov
 */
public class ResultsDetailsFragment extends Fragment {

    @BindView(R.id.tvTime)
    TextView mTimeText;
    @BindView(R.id.tvDistance)
    TextView mDistanceText;
    @BindView(R.id.ivScreenshot)
    ImageView mScreenshotImage;
    @BindView(R.id.tvSpeed)
    TextView mSpeed;
    Unbinder unbinder;
    @BindView(R.id.tvActivityType)
    Spinner mActivityTypeSpinner;
    @BindView(R.id.tvEnergy)
    TextView mEnergy;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.comment_btn)
    ImageButton commentBtn;
    @BindView(R.id.comment_text)
    TextView mCommentText;

    private Bitmap mImage;

    @Inject
    ResultsViewModel mViewModel;

    private long mTrackId;
    private String mComment;
    private SharedPreferences mPreferences;

    public static ResultsDetailsFragment newInstance(long trackId) {
        Bundle bundle = new Bundle();
        bundle.putLong(RESULT_ID, trackId);
        ResultsDetailsFragment fragment = new ResultsDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fr_result_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final Scope scope = Toothpick.openScopes(App.class, this);
        scope.installModules(new ModelsModule(this));
        Toothpick.inject(this, scope);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        mTrackId = getArguments().getLong(RESULT_ID, 0);

        mViewModel.getTrack().observe(this, track -> {
            String distance = StringUtil.getDistanceText(track.getDistance());
            String time = StringUtil.getTimeText(track.getDuration());
            mTimeText.setText(time);
            mDistanceText.setText(distance);
            mImage = ScreenshotMaker.fromBase64(track.getImageBase64());
            mScreenshotImage.setImageBitmap(mImage);
            tvDate.setText(StringUtil.getDateText(track.getDate()));
            mSpeed.setText(StringUtil.getSpeedText(track.getDistance() / track.getDuration()));
            mCommentText.setText(track.getComment());
            mComment = track.getComment();
        });
        mViewModel.isDeleted().observe(this, aBoolean -> {
            if (aBoolean) {
                getActivity().onBackPressed();
            }
        });
        mViewModel.getEnergy().observe(this, s -> mEnergy.setText(s));

        // TODO: 31.07.2019 dialog

        mViewModel.loadTrack(mTrackId);
        mActivityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.loadEnergy(mTrackId, position, mPreferences);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick({R.id.comment_btn, R.id.comment_text})
    public void onViewClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Введите комментарий.");

        final EditText input = new EditText(getActivity());
        input.setText(mComment);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Отправить", (dialog, which) -> {
            if(!TextUtils.isEmpty(input.getText())){
                mViewModel.updateTrackComment(mTrackId, input.getText().toString());
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionShare) {
            String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), mImage, "Мой маршрут", null);
            Uri uri = Uri.parse(path);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, "Время: " + mTimeText.getText() + "\nРасстояние: " + mDistanceText.getText()
                    + "\nСкорость: " + mSpeed.getText() + "\nЗатрачено энергии: " + mEnergy.getText() + "\nКомментарий: " + mCommentText.getText());
            startActivity(Intent.createChooser(intent, "Результаты маршрута"));
            return true;
        } else if (item.getItemId() == R.id.actionDelete) {
            mViewModel.deleteTrack(mTrackId);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
//        EventBus.getDefault().post(new SaveActivityTypeComment(mCommentText.getText().toString(), mActivityTypeSpinner.getSelectedItem()));
        super.onDestroyView();
        unbinder.unbind();
    }
}
