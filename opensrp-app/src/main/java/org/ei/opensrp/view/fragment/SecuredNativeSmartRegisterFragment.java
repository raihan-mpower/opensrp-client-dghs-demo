package org.ei.opensrp.view.fragment;

import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ei.opensrp.R;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.domain.ReportMonth;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.customControls.FontVariant;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.ECSearchOption;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.joda.time.LocalDate;

import java.util.List;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.ei.opensrp.AllConstants.SHORT_DATE_FORMAT;

/**
 * Created by koros on 10/12/15.
 */
public abstract class SecuredNativeSmartRegisterFragment extends SecuredFragment {

    public static final String DIALOG_TAG = "dialog";
    public static final List<? extends DialogOption> DEFAULT_FILTER_OPTIONS = asList(new AllClientsFilter());

    private ListView clientsView;
    private ProgressBar clientsProgressView;
    private TextView serviceModeView;
    private TextView appliedVillageFilterView;
    private TextView appliedSortView;
    private EditText searchView;
    private View searchCancelView;
    private TextView titleLabelView;

    public EditText getSearchView() {
        return searchView;
    }

    public View getSearchCancelView() {
        return searchCancelView;
    }

    public FilterOption getCurrentVillageFilter() {
        return currentVillageFilter;
    }

    public FilterOption getCurrentSearchFilter() {
        return currentSearchFilter;
    }

    public SortOption getCurrentSortOption() {
        return currentSortOption;
    }

    public ServiceModeOption getCurrentServiceModeOption() {
        return currentServiceModeOption;
    }

    public SmartRegisterPaginatedAdapter getClientsAdapter() {
        return clientsAdapter;
    }

    public void setClientsAdapter(SmartRegisterPaginatedAdapter clientsAdapter) {
        this.clientsAdapter = clientsAdapter;
    }

    private SmartRegisterPaginatedAdapter clientsAdapter;

    private FilterOption currentVillageFilter;
    private SortOption currentSortOption;

    private View mView;

    public void setCurrentSearchFilter(FilterOption currentSearchFilter) {
        this.currentSearchFilter = currentSearchFilter;
    }

    private FilterOption currentSearchFilter;
    private ServiceModeOption currentServiceModeOption;

    private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();
    private final NavBarActionsHandler navBarActionsHandler = new NavBarActionsHandler();
    private final SearchCancelHandler searchCancelHandler = new SearchCancelHandler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View view = inflater.inflate(R.layout.smart_register_activity, container, false);
        mView = view;
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        onInitialization();
        setupViews(view);
        onResumption();
        return view;
    }

    protected void setupViews(View view) {
        setupNavBarViews(view);
        populateClientListHeaderView(getDefaultOptionsProvider().serviceMode().getHeaderProvider(), view);

        clientsProgressView = (ProgressBar) view.findViewById(R.id.client_list_progress);
        clientsView = (ListView) view.findViewById(R.id.list);

        setupStatusBarViews(view);
        paginationViewHandler.addPagination(clientsView);

        updateDefaultOptions();
    }

    public void refreshListView(){
        this.onResumption();
    }

    @Override
    protected void onResumption() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                publishProgress();
                setupAdapter();
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                clientsProgressView.setVisibility(VISIBLE);
                clientsView.setVisibility(INVISIBLE);
            }

            @Override
            protected void onPostExecute(Void result) {
                clientsView.setAdapter(clientsAdapter);
                paginationViewHandler.refresh();
                clientsProgressView.setVisibility(View.GONE);
                clientsView.setVisibility(VISIBLE);

            }
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    private void setupStatusBarViews(View view) {
        appliedSortView = (TextView) view.findViewById(R.id.sorted_by);
        appliedVillageFilterView = (TextView) view.findViewById(R.id.village);
    }

    private void setupNavBarViews(View view) {
        view.findViewById(R.id.btn_back_to_home).setOnClickListener(navBarActionsHandler);

        setupTitleView(view);

        View villageFilterView = view.findViewById(R.id.filter_selection);
        villageFilterView.setOnClickListener(navBarActionsHandler);

        View sortView = view.findViewById(R.id.sort_selection);
        sortView.setOnClickListener(navBarActionsHandler);

        serviceModeView = (TextView)view.findViewById(R.id.service_mode_selection);
        serviceModeView.setOnClickListener(navBarActionsHandler);

        view.findViewById(R.id.register_client).setOnClickListener(navBarActionsHandler);

        setupSearchView(view);
    }

    protected void setServiceModeViewDrawableRight(Drawable drawable) {
        serviceModeView.setCompoundDrawables(null, null, drawable, null);
    }

    private void setupTitleView(View view) {
        ViewGroup titleLayout = (ViewGroup) view.findViewById(R.id.title_layout);
        titleLayout.setOnClickListener(navBarActionsHandler);

        titleLabelView = (TextView) view.findViewById(R.id.txt_title_label);

        TextView reportMonthStartView = (TextView) view.findViewById(R.id.btn_report_month);
        setReportDates(reportMonthStartView);
    }

    private void setupSearchView(View view) {
        searchView = (EditText) view.findViewById(R.id.edt_search);
        searchView.setHint(getNavBarOptionsProvider().searchHint());
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                currentSearchFilter = new ECSearchOption(cs.toString());
                clientsAdapter
                        .refreshList(currentVillageFilter, currentServiceModeOption,
                                currentSearchFilter, currentSortOption);

                searchCancelView.setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        searchCancelView = view.findViewById(R.id.btn_search_cancel);
        searchCancelView.setOnClickListener(searchCancelHandler);
    }

    private void setReportDates(TextView titleView) {
        ReportMonth report = new ReportMonth();
        titleView.setText(report.startOfCurrentReportMonth(LocalDate.now()).toString(SHORT_DATE_FORMAT)
                + " - "
                + report.endOfCurrentReportMonth(LocalDate.now()).toString(SHORT_DATE_FORMAT));
    }

    private void updateDefaultOptions() {
        currentSearchFilter = new ECSearchOption(null);
        currentVillageFilter = getDefaultOptionsProvider().villageFilter();
        currentServiceModeOption = getDefaultOptionsProvider().serviceMode();
        currentSortOption = getDefaultOptionsProvider().sortOption();

        appliedSortView.setText(currentSortOption.name());
        appliedVillageFilterView.setText(currentVillageFilter.name());
        serviceModeView.setText(currentServiceModeOption.name());
        titleLabelView.setText(getDefaultOptionsProvider().nameInShortFormForTitle());
    }

    private void populateClientListHeaderView(SecuredNativeSmartRegisterActivity.ClientsHeaderProvider headerProvider, View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(R.id.clients_header_layout);
        clientsHeaderLayout.removeAllViewsInLayout();
        int columnCount = headerProvider.count();
        int[] weights = headerProvider.weights();
        int[] headerTxtResIds = headerProvider.headerTextResourceIds();
        clientsHeaderLayout.setWeightSum(headerProvider.weightSum());

        for (int i = 0; i < columnCount; i++) {
            clientsHeaderLayout.addView(getColumnHeaderView(i, weights, headerTxtResIds));
        }
    }

    private View getColumnHeaderView(int i, int[] weights, int[] headerTxtResIds) {
        CustomFontTextView header = new CustomFontTextView(getActivity(), null, R.style.CustomFontTextViewStyle_Header_Black);
        header.setFontVariant(FontVariant.BLACK);
        header.setTextSize(16);
        header.setTextColor(getResources().getColor(R.color.client_list_header_text_color));
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        weights[i]);

        header.setLayoutParams(lp);
        header.setText(headerTxtResIds[i]);
        return header;
    }

    private void setupAdapter() {
        clientsAdapter = adapter();
        clientsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                paginationViewHandler.refresh();
            }
        });
    }

    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    protected void onServiceModeSelection(ServiceModeOption serviceModeOption, View view) {
        currentServiceModeOption = serviceModeOption;
        serviceModeView.setText(serviceModeOption.name());
        clientsAdapter
                .refreshList(currentVillageFilter, currentServiceModeOption,
                        currentSearchFilter, currentSortOption);

        populateClientListHeaderView(serviceModeOption.getHeaderProvider(), view);
    }

    protected void onSortSelection(SortOption sortBy) {
        currentSortOption = sortBy;
        appliedSortView.setText(sortBy.name());
        clientsAdapter
                .refreshList(currentVillageFilter, currentServiceModeOption,
                        currentSearchFilter, currentSortOption);
    }

    protected void onFilterSelection(FilterOption filter) {
        currentVillageFilter = filter;
        appliedVillageFilterView.setText(filter.name());
        clientsAdapter
                .refreshList(currentVillageFilter, currentServiceModeOption,
                        currentSearchFilter, currentSortOption);
    }

    protected void onEditSelection(EditOption editOption, SmartRegisterClient client) {
        editOption.doEdit(client);
    }

    private void goBack() {
        getActivity().finish();
    }

    void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        showFragmentDialog(dialogOptionModel, null);
    }

    protected void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        ((SecuredNativeSmartRegisterActivity)getActivity()).showFragmentDialog(dialogOptionModel, tag);
    }

    protected abstract SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider();

    protected abstract SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider();

    protected abstract SmartRegisterClientsProvider clientsProvider();

    protected abstract void onInitialization();

    protected abstract void startRegistration();

    private class FilterDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().filterOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onFilterSelection((FilterOption) option);
        }
    }

    private class SortDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().sortingOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onSortSelection((SortOption) option);
        }
    }

    protected class ServiceModeDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().serviceModeOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onServiceModeSelection((ServiceModeOption) option, mView);
        }
    }

    private class PaginationViewHandler implements View.OnClickListener {
        private Button nextPageView;
        private Button previousPageView;
        private TextView pageInfoView;

        private void addPagination(ListView clientsView) {
            ViewGroup footerView = getPaginationView();
            nextPageView = (Button) footerView.findViewById(R.id.btn_next_page);
            previousPageView = (Button) footerView.findViewById(R.id.btn_previous_page);
            pageInfoView = (TextView) footerView.findViewById(R.id.txt_page_info);

            nextPageView.setOnClickListener(this);
            previousPageView.setOnClickListener(this);

            footerView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.pagination_bar_height)));

            clientsView.addFooterView(footerView);
        }

        private ViewGroup getPaginationView() {
            return (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.smart_register_pagination, null);
        }

        private int getCurrentPageCount() {
            return clientsAdapter.currentPage() + 1 > clientsAdapter.pageCount() ? clientsAdapter.pageCount() : clientsAdapter.currentPage() + 1;
        }

        public void refresh() {
            pageInfoView.setText(
                    format(getResources().getString(R.string.str_page_info),
                            (getCurrentPageCount()),
                            (clientsAdapter.pageCount())));
            nextPageView.setVisibility(clientsAdapter.hasNextPage() ? VISIBLE : INVISIBLE);
            previousPageView.setVisibility(clientsAdapter.hasPreviousPage() ? VISIBLE : INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_next_page) {
                gotoNextPage();

            } else if (i == R.id.btn_previous_page) {
                goBackToPreviousPage();

            }
        }

        private void gotoNextPage() {
            clientsAdapter.nextPage();
            clientsAdapter.notifyDataSetChanged();
        }

        private void goBackToPreviousPage() {
            clientsAdapter.previousPage();
            clientsAdapter.notifyDataSetChanged();
        }
    }

    public class NavBarActionsHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.title_layout || i == R.id.btn_back_to_home) {
                goBack();

            } else if (i == R.id.register_client) {
                startRegistration();

            } else if (i == R.id.filter_selection) {
                showFragmentDialog(new FilterDialogOptionModel());

            } else if (i == R.id.sort_selection) {
                showFragmentDialog(new SortDialogOptionModel());

            } else if (i == R.id.service_mode_selection) {
                showFragmentDialog(new ServiceModeDialogOptionModel());

            }
        }
    }

    public class SearchCancelHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            clearSearchText();
        }

        private void clearSearchText() {
            searchView.setText("");
        }
    }
}
