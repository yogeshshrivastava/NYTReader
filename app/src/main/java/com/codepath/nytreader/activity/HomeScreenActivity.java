package com.codepath.nytreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytreader.R;
import com.codepath.nytreader.adapter.RecentsAdapter;
import com.codepath.nytreader.models.ArticleFilter;
import com.codepath.nytreader.models.Recents;
import com.codepath.nytreader.fragments.FilterDialogFragment;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Launcher screen which shows the welcome screen for the application and manages the recents list.
 */
public class HomeScreenActivity extends AppCompatActivity implements FilterDialogFragment.FilterCallback, RecentsAdapter.OnItemClickListener {

    private static final String TAG = HomeScreenActivity.class.getSimpleName();

    @BindView(R.id.rvRecentList)
    RecyclerView rvRecentList;

    @BindView(R.id.tvEmptyList)
    TextView tvEmptyList;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.recentsTitle)
    TextView recentTitle;

    private Set<String> recentsList;

    private RecentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the list from cache if there are any new items.
        Set<String> list = Recents.readPrefs();
        if(list.size() > 0) {
            recentTitle.setVisibility(View.VISIBLE);
            tvEmptyList.setVisibility(View.GONE);
            if (list.size() != recentsList.size()) {
                recentsList = list;
                adapter.setData(recentsList);
            }
        } else {
            recentTitle.setVisibility(View.GONE);
        }
    }

    private void init() {
        setSupportActionBar(toolbar);
        setToolbarDefaultTitle();
        if(recentsList == null) {
            recentsList = new HashSet<>();
        }
        // Create adapter
        adapter = new RecentsAdapter(recentsList);
        adapter.setOnItemClickListener(this);
        rvRecentList.setAdapter(adapter);
        rvRecentList.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Apply custom fonts to the title for Toolbar.
     */
    private void setToolbarDefaultTitle() {
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getAssets(), "fonts/newyorktimes.ttf"));
        SpannableStringBuilder sBuilder = new SpannableStringBuilder();
        sBuilder.append(getString(R.string.title_new_york_times_reader));
        sBuilder.setSpan(typefaceSpan, 0, sBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(sBuilder, TextView.BufferType.SPANNABLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                showFilterDialog();
                break;
            case R.id.action_search:
                showDetailsScreen(DetailsActivity.SEARCH_TYPE, null);
                break;
        }
        return true;
    }

    /**
     * Show details screen in 2 modes
     *
     * @param type either search or recent selected.
     * @param keyword if recent selected then pass the parameter.
     */
    private void showDetailsScreen(String type, String keyword) {
        Intent intent = new Intent(HomeScreenActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_TYPE, type);
        intent.putExtra(DetailsActivity.EXTRA_QUERY, keyword);
        startActivity(intent);
    }

    /**
     * Filter fragment to write change search parameters.
     */
    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment dialogFragment = FilterDialogFragment.newInstance();
        dialogFragment.show(fm, FilterDialogFragment.TAG);
    }

    @Override
    public void onFilterChanged(ArticleFilter filter) {
        // Write this to the share prefs.
        ArticleFilter.writeToPref(filter);
    }

    @Override
    public void onCancelled() {
        Toast.makeText(this, getString(R.string.no_filters_changed_message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClicked(String keyword) {
        showDetailsScreen(DetailsActivity.KEYWORD_TYPE, keyword);
    }

    @OnClick(R.id.tvEmptyList)
    public void onEmptyTextClick() {
        showDetailsScreen(DetailsActivity.SEARCH_TYPE, null);
    }

}
