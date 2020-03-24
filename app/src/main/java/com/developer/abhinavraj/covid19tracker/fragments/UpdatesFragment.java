package com.developer.abhinavraj.covid19tracker.fragments;


import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.activity.MainActivity;
import com.developer.abhinavraj.covid19tracker.adapter.UpdatesAdapter;
import com.developer.abhinavraj.covid19tracker.model.Update;
import com.developer.abhinavraj.covid19tracker.others.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatesFragment extends Fragment {


    private List<Update> mUpdateList;
    private UpdatesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View view;

    public UpdatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_updates, container, false);

            mUpdateList = new ArrayList<>();

            RecyclerView recyclerView = view.findViewById(R.id.updates);
            mAdapter = new UpdatesAdapter(mUpdateList, getContext());
            swipeRefreshLayout = view.findViewById(R.id.update_swipe_layout);
            progressBar = view.findViewById(R.id.progress_bar);

            Utility.setTranslationZ(progressBar, 3);
            progressBar.setVisibility(View.VISIBLE);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mUpdateList.clear();
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
                                // hide the divider for the last child
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

            new ExtractData().execute();
        }


        return view;
    }

    private void executeThreads() {
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new ExtractData().execute();
    }

    private class ExtractData extends AsyncTask<Void, Void, Void> {
        private boolean isExecuted = true;

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                String result = Utility.makeApiCallAndGetResponse("updates", "30");

                if (!result.equals("NONE")) {
                    JSONObject jObject = new JSONObject(result);
                    String j = jObject.get("body").toString();
                    JSONArray jsonArray = new JSONArray(j);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);

                        String updateTitle = jObj.getString("news");
                        String source = jObj.getString("source");
                        mUpdateList.add(new Update(updateTitle, source));
                    }
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
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            if(isExecuted) {
                ((MainActivity) Objects.requireNonNull(getActivity())).setExecutionStatus(true);
                mAdapter.notifyDataSetChanged();
            }
            else
                ((MainActivity) Objects.requireNonNull(getActivity())).setExecutionStatus(false);
        }

    }


}
