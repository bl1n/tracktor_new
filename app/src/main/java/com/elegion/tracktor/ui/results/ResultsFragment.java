package com.elegion.tracktor.ui.results;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.event.ChangeCommentEvent;
import com.elegion.tracktor.event.ExpandViewEvent;
import com.elegion.tracktor.util.CustomViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ResultsFragment extends Fragment {

    public static final String TAG = "Debug";
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty_list)
    TextView tvEmptyList;
    Unbinder unbinder;


    private ResultsViewModel mResultsViewModel;
    private ResultsAdapter mResultsAdapter;

    public ResultsFragment() {
    }

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomViewModelFactory factory = new CustomViewModelFactory();
        mResultsViewModel = ViewModelProviders.of(this, factory).get(ResultsViewModel.class);
        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_results, container, false);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mResultsAdapter = new ResultsAdapter();
        mResultsViewModel.getTracks().observe(this, tracks -> {
            if (tracks != null && tracks.size() > 0) {
                mResultsAdapter.submitList(tracks);
                Log.d(TAG, "onActivityCreated: load tracks");
            } else {
                mRecyclerView.setVisibility(View.GONE);
                tvEmptyList.setVisibility(View.VISIBLE);
            }
        });
        mResultsViewModel.loadTracks();


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mResultsAdapter);

        mRecyclerView.setHasFixedSize(true);

//        mResultsViewModel.createExample();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateComment(ChangeCommentEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Введите комментарий.");

        final EditText input = new EditText(getActivity());
        input.setText(event.getComment());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Отправить", (dialog, which) -> {
            if (!TextUtils.isEmpty(input.getText())) {
                mResultsViewModel.updateTrackComment(event.getTrackId(), input.getText().toString());
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  expandHolder(ExpandViewEvent event){
        mResultsViewModel.expandHolder(event);
        mResultsAdapter.notifyItemChanged(event.getPosition());
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionDirection: {
                mResultsViewModel.changeSortOrder();
                return true;
            }
            case R.id.actionField: {
                mResultsViewModel.changeSortBy();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }


    }


}
