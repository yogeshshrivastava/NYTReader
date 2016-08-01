package com.codepath.nytreader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytreader.R;
import com.codepath.nytreader.adapter.ArticleAdapter;
import com.codepath.nytreader.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.nytreader.fragments.FilterDialogFragment;
import com.codepath.nytreader.models.ArticleFilter;
import com.codepath.nytreader.models.Docs;
import com.codepath.nytreader.models.Recents;
import com.codepath.nytreader.models.Response;
import com.codepath.nytreader.network.NetworkManager;
import com.codepath.nytreader.network.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Manages the list of items related to the keyword and search capability for the app.
 */
public class DetailsActivity extends AppCompatActivity implements FilterDialogFragment.FilterCallback, ArticleAdapter.OnItemClickListener {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final String EXTRA_LIST = TAG + ".EXTRA_LIST";
    private static final String EXTRA_IS_LOADING = TAG + ".EXTRA_IS_LOADING";
    private static final String EXTRA_PREVIOUS_TOTAL_PAGES = TAG + ".EXTRA_PREVIOUS_TOTAL_PAGES";
    private static final String EXTRA_CURRENT_PAGE = TAG + ".EXTRA_CURRENT_PAGE";
    public static final String EXTRA_QUERY = TAG + ".EXTRA_QUERY";
    public static final String EXTRA_TYPE = TAG + ".EXTRA_TYPE";
    public static final String SEARCH_TYPE = TAG + ".SEARCH_TYPE";
    public static final String KEYWORD_TYPE = TAG + ".KEYWORD_TYPE";

    @BindView(R.id.rvRecentList)
    RecyclerView rvRecentList;

    @BindView(R.id.tvEmptyList)
    TextView tvEmptyList;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    MenuItem searchItem;

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private ArticleFilter filter;
    private ArticleAdapter rcAdapter;
    private ArrayList<Docs> articleList;
    private SearchView searchView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private NetworkManager.NYTArticleInterface nytArticleInterface;
    private String searchQuery;
    private boolean isListLoading;
    private int previousTotalItemCount;
    private int currentPage;
    private String type;


    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!TextUtils.isEmpty(query.trim())) {
                if(query.equalsIgnoreCase(searchQuery)) {
                    Toast.makeText(DetailsActivity.this, "Please enter a different keyword", Toast.LENGTH_LONG).show();
                    return true;
                }
                // Set current search query.
                DetailsActivity.this.searchQuery = query;

                // Update the recents cache.
                Recents.addToPref(query);
                searchView.clearFocus();
                setToolbarTitle(searchQuery);

                // Clear the existing data
                clearSearchList();
                // do a search based on query and filters.
                doSearch(query, filter, 0);
            } else {
                Toast.makeText(DetailsActivity.this, "Please enter valid search keyword", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    private MenuItemCompat.OnActionExpandListener onActionExpandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            if(articleList.isEmpty()) {
                tvEmptyList.setVisibility(View.GONE);
            }
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            if(articleList.isEmpty()) {
                finish();
            } else {
                tvEmptyList.setVisibility(View.GONE);
            }
            return true;
        }
    };

    private final Callback<Response> callback = new Callback<Response>() {
        @Override
        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
            hideRefreshing();
            if (response.isSuccessful()) {
                Response result = response.body();
                if(result != null && result.getResponse() != null) {
                    showResults(result.getResponse().getDocs());
                }
            } else {
                Log.e(TAG, "onResponse: error while getting response from the NYT server: " + response.errorBody());
            }
        }

        @Override
        public void onFailure(Call<Response> call, Throwable t) {
            hideRefreshing();
            Log.e(TAG, "onFailure: error while getting NYT article. ", t);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        if(savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(EXTRA_QUERY);
            isListLoading = savedInstanceState.getBoolean(EXTRA_IS_LOADING, false);
            previousTotalItemCount = savedInstanceState.getInt(EXTRA_PREVIOUS_TOTAL_PAGES, 0);
            currentPage = savedInstanceState.getInt(EXTRA_CURRENT_PAGE, 0);
            articleList = (ArrayList) savedInstanceState.getParcelableArrayList(EXTRA_LIST);
        } else {
            type = getIntent().getStringExtra(EXTRA_TYPE);
            if(KEYWORD_TYPE.equalsIgnoreCase(type)) {
                searchQuery = getIntent().getStringExtra(EXTRA_QUERY);
            }
        }

        init();
    }

    private void init() {
        setupViews();
        nytArticleInterface = NetworkManager.getClient();
        filter = ArticleFilter.readPrefs();

        // Setup the screen depending on the items we have.
        if(!articleList.isEmpty()) {
            // This means there was a rotation and there were items in the list.
            showResults(articleList);
        } else if(!TextUtils.isEmpty(searchQuery)) {
            // Search query with the parameter passed.
            doSearch(searchQuery, filter, 0);
        }
    }

    private void setupViews() {
        setSupportActionBar(toolbar);

        if(TextUtils.isEmpty(searchQuery)) {
            setToolbarDefaultTitle();
        } else {
            setToolbarTitle(searchQuery);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearSearchList();
                doSearch(searchQuery, filter, 0);
            }
        });

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.grid_column_count), 1);
        rvRecentList.setLayoutManager(gaggeredGridLayoutManager);

        // If article list is not initialized
        if(articleList == null) {
            articleList = new ArrayList<>();
        }
        rcAdapter = new ArticleAdapter(this, articleList);
        rcAdapter.setOnItemClickListener(this);
        rvRecentList.setAdapter(rcAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(gaggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreDataFromApi(page);
            }
        };
        rvRecentList.addOnScrollListener(scrollListener);
        scrollListener.setCurrentPage(currentPage);
        scrollListener.setPreviousTotalItemCount(previousTotalItemCount);
        scrollListener.setLoading(isListLoading);
    }

    private void setToolbarDefaultTitle() {
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getAssets(), "fonts/newyorktimes.ttf"));
        SpannableStringBuilder sBuilder = new SpannableStringBuilder();
        sBuilder.append(getString(R.string.title_new_york_times_reader));
        sBuilder.setSpan(typefaceSpan, 0, sBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(sBuilder, TextView.BufferType.SPANNABLE);
    }


    private void setToolbarTitle(String toolbarTitle) {
        title.setText(toolbarTitle);
    }


    private void loadMoreDataFromApi(int page) {
        Log.d(TAG, "loadMoreDataFromApi: page Number: " + page);
        doSearch(searchQuery, filter, page);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_LIST, articleList);
        outState.putBoolean(EXTRA_IS_LOADING, scrollListener.isLoading());
        outState.putInt(EXTRA_PREVIOUS_TOTAL_PAGES, scrollListener.getPreviousTotalItemCount());
        outState.putInt(EXTRA_CURRENT_PAGE, scrollListener.getCurrentPage());
        outState.putString(EXTRA_QUERY, searchQuery);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView)MenuItemCompat.getActionView(searchItem);

        // Set search icon.
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);

        // Set close search icon.
        int searchCloseId = android.support.v7.appcompat.R.id.search_close_btn;
        ImageView close = (ImageView) searchView.findViewById(searchCloseId);
        close.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        // Customize searchview text and hint colors
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setHint(getString(R.string.search_hint));
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);

        searchView.setOnQueryTextListener(searchListener);
        MenuItemCompat.setOnActionExpandListener(searchItem, onActionExpandListener);

        if(type != null) {
            if(SEARCH_TYPE.equalsIgnoreCase(type)) {
                showSearch();
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                showFilterDialog();
                break;
        }
        return true;
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment dialogFragment = FilterDialogFragment.newInstance();
        dialogFragment.show(fm, FilterDialogFragment.TAG);
    }

    private void showResults(ArrayList<Docs> docs) {
        if((docs == null || docs.isEmpty()) && articleList.isEmpty()) {
            Toast.makeText(DetailsActivity.this, getString(R.string.no_content_found) + searchQuery + "\"", Toast.LENGTH_LONG).show();
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            articleList.addAll(docs);
            rcAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFilterChanged(ArticleFilter filter) {
        // Write this to the share prefs.
        ArticleFilter.writeToPref(filter);
        // Set the filters
        this.filter = filter;
        // Clear the existing search results to load the new items.
        clearSearchList();
        // Create a request to the backend with new filters
        doSearch(searchQuery, filter, 0);
    }

    /**
     * Remove all the items in the list and reset the adapter.
     */
    private void clearSearchList() {
        if(!TextUtils.isEmpty(searchQuery) && articleList.size() > 0) {
            // Clear up the list and fire the query again
            articleList.clear();
            rcAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled() {
        Toast.makeText(this, "No filters has been changed", Toast.LENGTH_LONG).show();
    }

    /**
     * Fires a search query to the backend based on the parameters passed to the function.
     *
     * @param query
     * @param filter
     * @param page
     */
    private void doSearch(String query, ArticleFilter filter, int page) {
        if(NetworkUtils.isNetworkAvailable(this)) {
            // Only show refreshing if there is a new search.
            if (page == 0) {
                setRefreshing();
            }

            // Assign the search query
            if (!TextUtils.isEmpty(query)) {
                // Show loading the new data progress bar with swipe to refresh.
                Call<Response> call;
                if (filter != null) {
                    // Only add date if begin date is mentioned.
                    String beginDate = filter.getBeginDateApiString();
                    String endDate = filter.getEndDateApiString();
                    if (TextUtils.isEmpty(beginDate)) {
                        endDate = null;
                    }
                    String newsDeskApiString = filter.getNewsDeskApiString();
                    String sortOrder = filter.getSortOrder();
                    // Combine with all the filters user has selected and fire query.
                    call = nytArticleInterface.getArticlesPages(getString(R.string.nytimes_api_key), searchQuery, newsDeskApiString, beginDate, endDate, sortOrder, page);
                } else {
                    call = nytArticleInterface.getArticlesPages(getString(R.string.nytimes_api_key), searchQuery, null, null, null, null, page);
                }
                call.enqueue(callback);
            }
        } else {
            Toast.makeText(DetailsActivity.this, getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * List item clicked on the staggered layout.
     *
     * @param pos
     */
    @Override
    public void onItemClicked(int pos) {
        String title = "";
        if(articleList.get(pos).getHeadLine() != null) {
           title = articleList.get(pos).getHeadLine().getMain();
        }
        Intent intent = new Intent(DetailsActivity.this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_TITLE, title);
        intent.putExtra(WebViewActivity.EXTRA_WEB_URL, articleList.get(pos).getWebUrl());
        startActivity(intent);
    }

    @Override
    public void onMenuClicked(final int pos, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.item_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        String headline = "";
                        if(articleList.get(pos).getHeadLine() != null) {
                            headline = articleList.get(pos).getHeadLine().getMain();
                        }
                        share(headline, articleList.get(pos).getWebUrl());
                        return true;
                    case R.id.action_web:
                        onItemClicked(pos);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @OnClick(R.id.tvEmptyList)
    public void onEmptyTextClick() {
       showSearch();
    }

    /**
     * Trigger search to show keyboard.
     */
    private void showSearch() {
        tvEmptyList.setVisibility(View.GONE);
        // Expand the search view and request focus
        searchItem.expandActionView();
        searchView.requestFocus();
    }

    private void setRefreshing() {
        if(!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.removeCallbacks(null);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    private void hideRefreshing() {
        if(swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.removeCallbacks(null);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void share(String title, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_check_story) + url);
        startActivity(Intent.createChooser(share, getString(R.string.share_title)));
    }
}
