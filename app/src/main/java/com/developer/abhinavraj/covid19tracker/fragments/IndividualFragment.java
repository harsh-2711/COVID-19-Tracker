package com.developer.abhinavraj.covid19tracker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.model.Country;
import com.developer.abhinavraj.covid19tracker.others.Utility;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualFragment extends Fragment {


    private TextView country_name, activecases, newcases, newdeaths, seriouslycritical, totalcases, totalpermillion, totaldeaths, totalrecovered, twc, twd, twr;
    private Gson gson;
    private View view;

    public IndividualFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment


        if (view == null) {
            gson = new Gson();
            try {

                String cdata = getArguments().getString("countrydata");
                Country country = gson.fromJson(cdata, Country.class);

                view = inflater.inflate(R.layout.fragment_individual, container, false);

                country_name = view.findViewById(R.id.i_country_name);
                activecases = view.findViewById(R.id.i_activecases);
                newcases = view.findViewById(R.id.i_newcases);
                newdeaths = view.findViewById(R.id.i_newdeaths);
                seriouslycritical = view.findViewById(R.id.i_seriouslycritical);
                totalcases = view.findViewById(R.id.i_TotalCases);
                totalpermillion = view.findViewById(R.id.i_TotalCasesPerMillion);
                totaldeaths = view.findViewById(R.id.i_TotalDeaths);
                totalrecovered = view.findViewById(R.id.i_TotalRecovered);


                if (cdata != null) {
                    country_name.setText(country.getCountryName());
                    activecases.setText(Utility.formatNumber(country.getActiveCases()));
                    newcases.setText(Utility.formatNumber(country.getNewCases()));
                    newdeaths.setText(Utility.formatNumber(country.getNewDeaths()));
                    seriouslycritical.setText(Utility.formatNumber(country.getSeriouslyCritical()));
                    totalcases.setText(Utility.formatNumber(country.getTotalCases()));
                    totalpermillion.setText(String.valueOf(country.getTotalCasesPerMillion()));
                    totaldeaths.setText(Utility.formatNumber(country.getTotalDeaths()));
                    totalrecovered.setText(Utility.formatNumber(country.getTotalRecovered()));
                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        return view;

    }
}