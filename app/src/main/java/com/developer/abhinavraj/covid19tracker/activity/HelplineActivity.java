package com.developer.abhinavraj.covid19tracker.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.adapter.HelplineAdapter;
import com.developer.abhinavraj.covid19tracker.model.Helpline;
import com.developer.abhinavraj.covid19tracker.others.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HelplineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HelplineAdapter helplineAdapter;
    private JSONArray jsonArray;
    private EditText editText;
    private List<Helpline> helplineList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);
        try {
            jsonArray = new JSONArray(Utility.readJSONFromAsset(getApplicationContext(), "helplines.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.helpline_recycler_view);
        editText = findViewById(R.id.editTextSearch);
        helplineList = new ArrayList<>();
        helplineAdapter = new HelplineAdapter(getApplicationContext(), helplineList);

        new ParseJson().execute();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(helplineAdapter);

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

    public void hideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filter(String text) {
        List<Helpline> filteredCountries = new ArrayList<>();

        for (int i = 0; i < helplineList.size(); i++) {
            if (helplineList.get(i).getStateName().toLowerCase().contains(text.toLowerCase())) {
                filteredCountries.add(helplineList.get(i));
            }
        }
        helplineAdapter.filterList(filteredCountries);
    }


    private class ParseJson extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                System.out.println(jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObj = jsonArray.getJSONObject(i);

                    String stateName = jObj.getString("state_name");
                    String number = jObj.getString("number");

                    helplineList.add(new Helpline(stateName, number));
                }
            } catch (Exception ex) {
                Log.e("Async", ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            helplineAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }
}
