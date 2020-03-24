package com.developer.abhinavraj.covid19tracker.fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.activity.MainActivity;
import com.developer.abhinavraj.covid19tracker.others.Utility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONObject;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualWorldFragment extends Fragment {


    private View view;
    private ProgressBar progressBar;
    private TextView totalWorldCases;
    private TextView totalRecovered;
    private TextView totalDeaths;
    private TextView totalNewCases;
    private TextView totalNewDeaths;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int count = 1;

    private AdView adView;
    private AdView adView2;


    public IndividualWorldFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_individual_world, container, false);
            progressBar = view.findViewById(R.id.progress_bar);
            swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
            totalDeaths = view.findViewById(R.id.total_world_deaths);
            totalWorldCases = view.findViewById(R.id.total_world_cases);
            totalRecovered = view.findViewById(R.id.total_world_recovered);
            totalNewCases = view.findViewById(R.id.total_new_cases);
            totalNewDeaths = view.findViewById(R.id.total_new_deaths);

            adView = (AdView) view.findViewById(R.id.adView);
            adView2 = (AdView) view.findViewById(R.id.adView_2);

            Utility.setTranslationZ(progressBar, 3);

            executeThreads();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    executeThreads();
                }
            });

            try {
                AdRequest adRequest = new AdRequest.Builder().build();

                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                    }

                    @Override
                    public void onAdClosed() {
                        Log.d("ADS", "AD_BOTTOM Closed");
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.d("ADS", "AD_BOTTOM Failed");
                    }

                    @Override
                    public void onAdLeftApplication() {
                        Log.d("ADS", "AD_BOTTOM Left");
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }
                });

                adView.loadAd(adRequest);

                AdRequest adRequest2 = new AdRequest.Builder().build();

                adView2.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                    }

                    @Override
                    public void onAdClosed() {
                        Log.d("ADS", "AD_TOP Closed");
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.d("ADS", "AD_TOP Failed");
                    }

                    @Override
                    public void onAdLeftApplication() {
                        Log.d("ADS", "AD_TOP Left");
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }
                });

                adView2.loadAd(adRequest2);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return view;
    }

    private void executeThreads() {
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new ExtractData().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class ExtractData extends AsyncTask<Void,Void,Void> {
        private boolean isExecuted = true;
        String fetchedCases, fetchedDeaths, fetchedRecovered, fetchedNewCases, fetchedNewDeaths;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String result = Utility.makeApiCallAndGetResponse("data", "stats");

                if (!result.equals("NONE")) {
                    JSONObject jObject = new JSONObject(result);
                    fetchedCases = Utility.formatNumber(Integer.parseInt(jObject.get("casesCount").toString()));
                    fetchedDeaths = Utility.formatNumber(Integer.parseInt(jObject.get("deathsCount").toString()));
                    fetchedRecovered = Utility.formatNumber(Integer.parseInt(jObject.get("recoveredCount").toString()));
                    fetchedNewCases = Utility.formatNumber(Integer.parseInt(jObject.get("newCasesCount").toString()));
                    fetchedNewDeaths = Utility.formatNumber(Integer.parseInt(jObject.get("newDeathsCount").toString()));
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

            try {
                totalWorldCases.setText(fetchedCases);
                totalDeaths.setText(fetchedDeaths);
                totalRecovered.setText(fetchedRecovered);
                totalNewCases.setText("+" + fetchedNewCases);
                totalNewDeaths.setText("+" + fetchedNewDeaths);
            } catch (Exception e) {
                Log.e("Async", "Data not fetched");
            }

            progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);

            if(isExecuted)
                ((MainActivity) Objects.requireNonNull(getActivity())).setExecutionStatus(true);
            else
                ((MainActivity) Objects.requireNonNull(getActivity())).setExecutionStatus(false);
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
