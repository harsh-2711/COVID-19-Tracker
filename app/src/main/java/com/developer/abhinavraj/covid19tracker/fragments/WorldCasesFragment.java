package com.developer.abhinavraj.covid19tracker.fragments;


import android.app.Activity;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.activity.MainActivity;
import com.developer.abhinavraj.covid19tracker.adapter.CountryAdapter;
import com.developer.abhinavraj.covid19tracker.model.Country;
import com.developer.abhinavraj.covid19tracker.others.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorldCasesFragment extends Fragment {

    private List<Country> mCountryList;
    private CountryAdapter mAdapter;
    private EditText editText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private JSONObject jsonObject;
    private Bundle savedState = null;
    private View view;
    private View whiteBackground;
    private ProgressBar progressBar;

    public WorldCasesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            jsonObject = new JSONObject(Utility.readJSONFromAsset(getActivity(), "countries.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            mCountryList = new ArrayList<>();

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_world_cases, container, false);
            RecyclerView recyclerView = view.findViewById(R.id.countries);
            editText = view.findViewById(R.id.editTextSearch);
            mAdapter = new CountryAdapter(mCountryList, getContext());
            swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
            progressBar = view.findViewById(R.id.progress_bar);
            whiteBackground = view.findViewById(R.id.white_background);

            Utility.setTranslationZ(progressBar, 3);
            executeThreads();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mCountryList.clear();
                    executeThreads();
                }
            });

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);

            try {
                recyclerView.addItemDecoration(
                        new DividerItemDecoration(getContext(), ((LinearLayoutManager) mLayoutManager).getOrientation()) {
                            @Override
                            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                                int position = parent.getChildAdapterPosition(view);
                                if (position == parent.getAdapter().getItemCount() - 1) {
                                    outRect.setEmpty();
                                } else {
                                    super.getItemOffsets(outRect, view, parent, state);
                                }
                            }
                        });
            } catch (Exception e) {
                // pass
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    filter(editable.toString());
                }
            });

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                        editText.setActivated(false);
                    }
                }
            });
        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putSerializable("Countries", (Serializable) mCountryList);
        return state;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Countries", (Serializable) mCountryList);
    }

    private void filter(String text) {
        List<Country> filteredCountries = new ArrayList<>();

        for (int i = 0; i < mCountryList.size(); i++) {
            if (mCountryList.get(i).getCountryName().toLowerCase().contains(text.toLowerCase())) {
                filteredCountries.add(mCountryList.get(i));
            }
        }
        mAdapter.filterList(filteredCountries);
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeThreads() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new ExtractData().execute();
    }

    private class ExtractData extends AsyncTask<Void, Void, Void> {
        private boolean isExecuted = true;

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                String result = Utility.makeApiCallAndGetResponse("data", "all");

                if (!result.equals("NONE")) {
                    JSONObject jObject = new JSONObject(result);
                    String j = jObject.get("body").toString();
                    JSONArray jsonArray = new JSONArray(j);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        try {
                            String countryName = jObj.getString("country_name");
                            int activeCases = Integer.parseInt(jObj.getString("active_cases").replaceAll(",", ""));
                            int totalCases = Integer.parseInt(jObj.getString("total_cases").replaceAll(",", ""));
                            int totalRecovered = Integer.parseInt(jObj.getString("total_recovered").replaceAll(",", ""));
                            int newCases = Integer.parseInt(jObj.getString("new_cases").replaceAll(",", ""));
                            int totalDeaths = Integer.parseInt(jObj.getString("total_deaths").replaceAll(",", ""));
                            int seriouslyCritical = Integer.parseInt(jObj.getString("serious_critical").replaceAll(",", ""));
                            int newDeaths = Integer.parseInt(jObj.getString("new_deaths").replace("+-", "").replaceAll(",", ""));
                            String totalCasesPerMillion = (jObj.getString("total_cases_per_1M"));

                            String countryCode = null;

                            if (jsonObject.has(countryName)) {
                                countryCode = jsonObject.getString(countryName);
                            }

                            mCountryList.add(new Country(countryName, countryCode, totalCases, totalRecovered, activeCases,
                                    totalDeaths, newCases, newDeaths, seriouslyCritical, totalCasesPerMillion));
                        } catch (Exception ex) {
                            Log.e("Notation", ex.getMessage());
                        }

                    }
                    Collections.sort(mCountryList);
                } else {
                    isExecuted = false;
                }

            } catch (Exception ex) {
                Log.e("Async", ex.getMessage());
                isExecuted = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);

            if(isExecuted) {
                ((MainActivity) Objects.requireNonNull(getActivity())).setExecutionStatus(true);
                mAdapter.notifyDataSetChanged();
            }
            else
                ((MainActivity) Objects.requireNonNull(getActivity())).setExecutionStatus(false);
        }

    }

}
