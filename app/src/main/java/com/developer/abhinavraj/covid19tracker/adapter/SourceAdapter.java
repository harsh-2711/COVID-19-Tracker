package com.developer.abhinavraj.covid19tracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.model.Source;

import java.util.List;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {

    private List<Source> sources;
    private Context context;

    public SourceAdapter(Context context, List<Source> sources) {
        this.sources = sources;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View sourceView = inflater.inflate(R.layout.source_item, parent, false);
        return new ViewHolder(sourceView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Source source = sources.get(position);

        TextView sourceName = holder.sourceName;
        final TextView url = holder.url;
        ImageView send = holder.send;

        sourceName.setText(source.getName());
        url.setText(source.getDescription());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse(source.getUrl()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    Log.e("URL_OPEM_ERROR", ex.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sources.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sourceName, url;
        ImageView send;

        ViewHolder(View view) {
            super(view);
            sourceName = view.findViewById(R.id.source_name);
            url = view.findViewById(R.id.source_link);
            send = view.findViewById(R.id.send);
        }
    }
}
