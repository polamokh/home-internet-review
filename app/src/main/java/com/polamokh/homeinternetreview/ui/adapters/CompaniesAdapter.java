package com.polamokh.homeinternetreview.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.listeners.IOnItemSelectListener;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.CompanyViewHolder> {
    private String[] names;
    private int[] images;
    private IOnItemSelectListener listener;

    public CompaniesAdapter(String[] names, IOnItemSelectListener listener) {
        this.names = names;
        this.images = new int[]{R.drawable.we,
                R.drawable.orange,
                R.drawable.etisalat,
                R.drawable.vodafone};
        this.listener = listener;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        holder.name.setText(names[position]);
        holder.image.setImageResource(images[position]);

        holder.itemView.setOnClickListener(v -> {
            listener.OnItemSelected(names[position]);
        });
    }

    @Override
    public int getItemCount() {
        return (names == null) ? 0 : names.length;
    }

    static class CompanyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        CompanyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.company_item_name);
            image = itemView.findViewById(R.id.company_item_image);
        }
    }
}
