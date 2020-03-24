package com.developer.abhinavraj.covid19tracker.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.adapter.SourceAdapter;
import com.developer.abhinavraj.covid19tracker.model.Source;

import java.util.ArrayList;
import java.util.List;

public class SourceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SourceAdapter sourceAdapter;
    private List<Source> sourceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        recyclerView = findViewById(R.id.source_recycler_view);
        sourceList = new ArrayList<>();
        sourceAdapter = new SourceAdapter(getApplicationContext(), sourceList);

        new PopulateData().execute();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sourceAdapter);
    }


    private class PopulateData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                sourceList.add(new Source("Worldometers", "https://www.worldometers.info/coronavirus/", "For world statistics"));
                sourceList.add(new Source("India Today", "https://www.indiatoday.in/india/story/coronavirus-cases-in-india-covid19-states-cities-affected-1653852-2020-03-09", "For news updates"));
                sourceList.add(new Source("Ministry of Health and Family Welfare Government of India", "https://www.mohfw.gov.in/", "For Indian stats"));
                sourceList.add(new Source("Realtime feed from Channelnewsasia", "https://infographics.channelnewsasia.com/covid-19/map.html", "For regular updates"));
                sourceList.add(new Source("Economictimes ET", "https://economictimes.indiatimes.com/topic/confirmed-cases", "For news updates"));
            } catch (Exception ex) {
                Log.e("Async", ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            sourceAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }
}
