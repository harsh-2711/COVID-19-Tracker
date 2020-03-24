package com.developer.abhinavraj.covid19tracker.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.fragments.IndividualFragment;
import com.developer.abhinavraj.covid19tracker.model.Country;
import com.developer.abhinavraj.covid19tracker.others.Utility;
import com.google.gson.Gson;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private List<Country> countryList;
    private Context context;


    public CountryAdapter(List<Country> countries, Context context) {
        this.countryList = countries;
        this.context = context;
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View countryView = inflater.inflate(R.layout.country_item, parent, false);

        return new ViewHolder(countryView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Country country = countryList.get(position);

        // Set item views based on your views and data model
        TextView countryName = holder.countryName;
        TextView totalCases = holder.totalCases;
        ImageView countryImage = holder.countryImage;
        countryName.setText(country.getCountryName());
        String countryCode = country.getCountryCode();

        if (countryCode != null) {
            Glide.with(context)
                    .load(Uri.parse("file:///android_asset/flag_" + countryCode.toLowerCase() + ".png"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(countryImage);
        } else {
            countryImage.setImageResource(R.drawable.temp_image);
        }

        totalCases.setText(Utility.formatNumber(country.getTotalCases()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String cdata = gson.toJson(country);
                bundle.putString("countrydata", cdata);

                IndividualFragment individualFragment = new IndividualFragment();
                individualFragment.setArguments(bundle);

                openFragment(individualFragment);
            }
        });

    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    public void filterList(List<Country> filterdCountries) {
        this.countryList = filterdCountries;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView countryName, totalCases;
        ImageView countryImage;

        ViewHolder(View view) {
            super(view);
            countryName = view.findViewById(R.id.country_name);
            totalCases = view.findViewById(R.id.total_cases);
            countryImage = view.findViewById(R.id.country_image);
        }
    }
}
