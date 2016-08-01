package com.codepath.nytreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytreader.R;
import com.codepath.nytreader.models.Docs;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author yvastavaus.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Docs> itemList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(int pos);

        void onMenuClicked(int pos, View view);
    }

    public ArticleAdapter(Context context, List<Docs> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_staggered_item, parent, false);
        ArticleViewHolder articleViewHolder = new ArticleViewHolder(layout, onItemClickListener);
        return articleViewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        holder.tvHeadline.setText(itemList.get(position).getHeadLine().getMain());
        // reset the thumbnail while loading new item.
        holder.ivThumbnail.setImageResource(0);

        if(itemList.get(position).getMultimedia().size() > 0) {
            Picasso.with(context).load(itemList.get(position).getMultimedia().get(0).getUrl()).into(holder.ivThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivThumbnail)
        ImageView ivThumbnail;

        @BindView(R.id.tvHeadline)
        TextView tvHeadline;

        @BindView(R.id.menu)
        ImageView menu;

        OnItemClickListener listener;

        public ArticleViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu:
                    listener.onMenuClicked(getLayoutPosition(), v);
                    break;
                 default:
                     listener.onItemClicked(getLayoutPosition());
                     break;
            }
        }
    }
}
