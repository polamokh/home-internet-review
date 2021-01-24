package com.polamokh.homeinternetreview.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.listeners.IOnItemClickListener;

public class GovernoratesAdapter extends RecyclerView.Adapter<GovernoratesAdapter.GovernorateViewHolder> {
    private String[] names;
    private IOnItemClickListener listener;

    public GovernoratesAdapter(String[] names, IOnItemClickListener listener) {
        this.names = names;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GovernorateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_governorate, parent, false);
        return new GovernorateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GovernorateViewHolder holder, int position) {
        holder.name.setText(names[position]);
        holder.itemView.setOnClickListener(v -> {
            listener.OnItemClicked(names[position]);
        });
    }

    @Override
    public int getItemCount() {
        return (names == null) ? 0 : names.length;
    }

    static class GovernorateViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public GovernorateViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.governorate_name);
        }
    }
}
