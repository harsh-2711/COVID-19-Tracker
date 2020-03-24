package com.developer.abhinavraj.covid19tracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.model.Update;

import java.util.List;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.ViewHolder> {

    private List<Update> updateList;
    private Context context;


    public UpdatesAdapter(List<Update> updates, Context context) {
        this.updateList = updates;
        this.context = context;
    }

    @NonNull
    @Override
    public UpdatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View updateView = inflater.inflate(R.layout.update_item, parent, false);

        return new ViewHolder(updateView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Update update = updateList.get(position);

        TextView updateTitle = holder.updateTitle;
        TextView readmore = holder.readmore;
        updateTitle.setText(update.getNews());
        readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(update.getSource()));
                context.startActivity(browserIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return updateList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView updateTitle, readmore;

        ViewHolder(View view) {
            super(view);
            updateTitle = view.findViewById(R.id.update_title);
            readmore = view.findViewById(R.id.readmore);
        }
    }

}
