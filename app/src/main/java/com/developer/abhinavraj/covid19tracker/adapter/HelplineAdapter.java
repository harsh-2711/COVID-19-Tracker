package com.developer.abhinavraj.covid19tracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.model.Helpline;

import java.util.List;

public class HelplineAdapter extends RecyclerView.Adapter<HelplineAdapter.ViewHolder> {

    private List<Helpline> helplines;
    private Context context;

    public HelplineAdapter(Context context, List<Helpline> helplines) {
        this.context = context;
        this.helplines = helplines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View helplineView = inflater.inflate(R.layout.helpline_item, parent, false);
        return new ViewHolder(helplineView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Helpline helpline = helplines.get(position);

        TextView stateName = holder.stateName;
        final TextView helplineNumber = holder.helplineNumber;
        ImageView call = holder.call;

        stateName.setText(helpline.getStateName());
        helplineNumber.setText(helpline.getNumber());

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + helpline.getNumber()));
                context.startActivity(intent);
            }
        });
    }

    public void filterList(List<Helpline> filterdHelplines) {
        this.helplines = filterdHelplines;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return helplines.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView stateName, helplineNumber;
        ImageView call;

        ViewHolder(View view) {
            super(view);
            stateName = view.findViewById(R.id.state_name);
            helplineNumber = view.findViewById(R.id.helpline_number);
            call = view.findViewById(R.id.call);
        }
    }

}
