package com.elegion.tracktor.ui.results;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.event.ChangeCommentEvent;
import com.elegion.tracktor.event.DeleteTrackEvent;
import com.elegion.tracktor.event.ExpandViewEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

public class ResultsAdapter extends ListAdapter<Track, ResultHolder> {


    private static final DiffUtil.ItemCallback<Track> DIFF_CALLBACK = new DiffUtil.ItemCallback<Track>() {

        @Override
        public boolean areItemsTheSame(Track oldItem, Track newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Track oldItem, Track newItem) {
            return oldItem.equals(newItem);
        }
    };

    ResultsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_track_con, parent, false);
        return new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        Track track = getItem(position);
        holder.bind(track);

        holder.itemView.findViewById(R.id.li_btn_del_con).setOnClickListener(v -> {
            EventBus.getDefault().post(new DeleteTrackEvent(track.getId()));
            notifyDataSetChanged();
        });
        holder.itemView.setOnClickListener(v->{
            EventBus.getDefault().post(new ExpandViewEvent(track.getId(), position));
            notifyItemChanged(position);
        });

        holder.itemView.findViewById(R.id.li_btn_change_com_con).setOnClickListener(v->{
            EventBus.getDefault().post(new ChangeCommentEvent(track.getComment(), track.getId()));
            notifyDataSetChanged();
        });


    }





}
