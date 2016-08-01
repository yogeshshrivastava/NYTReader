package com.codepath.nytreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.nytreader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author yvastavaus.
 */
public class RecentsAdapter extends RecyclerView.Adapter <RecentsAdapter.RecentsViewHolder>{

    private List<String> recentList;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(String keyword);
    }

    public RecentsAdapter(Set<String> recentList) {
        this.recentList = new ArrayList<>(recentList);
    }

    @Override
    public RecentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.recents_items, parent, false);
        RecentsViewHolder viewHolder = new RecentsViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecentsViewHolder holder, int position) {
        holder.item.setText(recentList.get(position));
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }


    // Set a new data set incase there are new keywords.
    public void setData(Set<String> recentList) {
        this.recentList = new ArrayList<>(recentList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class RecentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.list_item)
        TextView item;

        public RecentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClicked(recentList.get(getLayoutPosition()));
        }
    }
}
