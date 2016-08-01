package com.codepath.nytreader.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.nytreader.R;
import com.codepath.nytreader.models.ArticleFilter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * author yvastavaus.
 */
public class FilterDialogFragment extends DialogFragment {

    public static String TAG = FilterDialogFragment.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({NEWEST, OLDEST})
    private @interface orderType {}

    private static final String NEWEST = "Newest";
    private static final String OLDEST = "Oldest";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ART, FASHION, SPORTS})
    private @interface newsDeskType {}

    private static final String ART = "art";
    private static final String FASHION = "fashion";
    private static final String SPORTS = "sports";

    ArrayList<String> newsDeskList = new ArrayList<>();

    private DatePickerDialog beginDatePickerDialog;

    private DatePickerDialog endDatePickerDialog;


    public interface FilterCallback {
        /**
         * Callback when there is any change in filter.
         *
         * @param filter
         */
        void onFilterChanged(ArticleFilter filter);

        /**
         * On cancelled for the dialog.
         */
        void onCancelled();
    }


    @BindView(R.id.ivClearBeginDate)
    ImageView ivClearBeginDate;

    @BindView(R.id.ivClearEndDate)
    ImageView ivClearEndDate;

    @BindView(R.id.tvBeginDateValue)
    TextView tvBeginDate;

    @BindView(R.id.tvEndDateValue)
    TextView tvEndDateValue;

    @BindView(R.id.spOrder)
    Spinner spOrder;

    @BindView(R.id.cbArt)
    CheckBox cbArt;

    @BindView(R.id.cbFasion)
    CheckBox cbFasion;

    @BindView(R.id.cbSports)
    CheckBox cbSports;

    private ArticleFilter filter;

    public static FilterDialogFragment newInstance() {
        FilterDialogFragment fragment = new FilterDialogFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_dialog, container);
        ButterKnife.bind(this, view);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.order_array)));
        spOrder.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.sort_item , list));

        filter = ArticleFilter.readPrefs();
        if(filter == null) {
            filter = new ArticleFilter();
        }

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        // Setup the screen
        if(!TextUtils.isEmpty(filter.getBeginDate())) {
            tvBeginDate.setText(filter.getBeginDate());
        } else {
            resetBeginDate();

        }

        if(!TextUtils.isEmpty(filter.getEndDate())) {
            tvEndDateValue.setText(filter.getEndDate());
        } else {
            resetEndDate();
        }

        if(!TextUtils.isEmpty(filter.getSortOrder())) {
            if(NEWEST.equalsIgnoreCase(filter.getSortOrder())) {
                spOrder.setSelection(0);
            } else {
                spOrder.setSelection(1);
            }
        }

        if(filter.getNewsDesk() != null && filter.getNewsDesk().size() > 0) {
            for(String newsDesk: filter.getNewsDesk()) {
                newsDeskList.add(newsDesk);
                if(newsDesk.equalsIgnoreCase(ART)) {
                    cbArt.setChecked(true);
                }
                if(newsDesk.equalsIgnoreCase(FASHION)) {
                    cbFasion.setChecked(true);
                }
                if(newsDesk.equalsIgnoreCase(SPORTS)) {
                    cbSports.setChecked(true);
                }
            }
        }


        Calendar newCalendar = Calendar.getInstance();
        beginDatePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String beginDate = getDate(year, monthOfYear, dayOfMonth);
                setBeginDate(beginDate);
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String endDate = getDate(year, monthOfYear, dayOfMonth);
                setEndDate(endDate);
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        spOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(filter != null) {
                    if (position == 0) {
                        filter.setSortOrder(NEWEST);
                    } else {
                        filter.setSortOrder(OLDEST);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @OnCheckedChanged(R.id.cbArt)
    public void onArtChecked(boolean isEnabled) {
        if(isEnabled) {
            addToList(newsDeskList, ART);
        } else {
            removeFromList(newsDeskList, ART);
        }
        filter.setNewsDesk(newsDeskList);
    }

    private void addToList(ArrayList<String> newsDeskList, String item) {
        if(!newsDeskList.contains(item)){
            newsDeskList.add(item);
        }
    }

    private void removeFromList(ArrayList<String> newsDeskList, String item) {
        if(newsDeskList.contains(item)){
            newsDeskList.remove(item);
        }
    }

    @OnCheckedChanged(R.id.cbFasion)
    public void onFashionChecked(boolean isEnabled) {
        if(isEnabled) {
            addToList(newsDeskList, FASHION);
        } else {
            removeFromList(newsDeskList, FASHION);
        }
        filter.setNewsDesk(newsDeskList);
    }

    @OnCheckedChanged(R.id.cbSports)
    public void onSportsChecked(boolean isEnabled) {
        if(isEnabled) {
            addToList(newsDeskList, SPORTS);
        } else {
            removeFromList(newsDeskList, SPORTS);
        }
        filter.setNewsDesk(newsDeskList);
    }

    @OnClick(R.id.tvBeginDateValue)
    public void onBeginDateClicked() {
        beginDatePickerDialog.show();
    }

    @OnClick(R.id.tvEndDateValue)
    public void onEndDateClicked() {
        endDatePickerDialog.show();
    }

    /**
     * Show date in a particular format.
     * @param year
     * @param month
     * @param day
     */
    private String getDate(int year, int month, int day) {
        return new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year).toString();
    }

    @OnClick(R.id.saved)
    public void onSavedClick() {
        ((FilterCallback)getActivity()).onFilterChanged(filter);
        dismiss();
    }


    @OnClick(R.id.cancel)
    public void onCancelClick() {
        ((FilterCallback)getActivity()).onCancelled();
        dismiss();
    }

    @OnClick(R.id.ivClearBeginDate)
    public void onClearBeginDate() {
        resetBeginDate();
    }

    @OnClick(R.id.ivClearEndDate)
    public void onClearEndDate() {
        resetEndDate();
    }

    private void resetBeginDate() {
        filter.setBeginDate(null);
        tvBeginDate.setText(getString(R.string.set_begin_date));
        ivClearBeginDate.setVisibility(View.GONE);
    }

    private void resetEndDate() {
        filter.setEndDate(null);
        tvEndDateValue.setText(getString(R.string.set_end_date));
        ivClearEndDate.setVisibility(View.GONE);
    }

    private void setEndDate(String endDate) {
        tvEndDateValue.setText(endDate);
        filter.setEndDate(endDate);
        ivClearEndDate.setVisibility(View.VISIBLE);
    }

    private void setBeginDate(String beginDate) {
        tvBeginDate.setText(beginDate);
        filter.setBeginDate(beginDate);
        ivClearBeginDate.setVisibility(View.VISIBLE);
    }
}
