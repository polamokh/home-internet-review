package com.polamokh.homeinternetreview.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Company;

import java.util.List;

public class CompaniesStandingsAdapter extends
        RecyclerView.Adapter<CompaniesStandingsAdapter.CompaniesStandingsViewHolder> {

    private List<Company> companies;

    @NonNull
    @Override
    public CompaniesStandingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company_standing, parent, false);
        return new CompaniesStandingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompaniesStandingsViewHolder holder, int position) {
        Company company = companies.get(position);
        holder.name.setText(company.getName());
        holder.rating.setRating((float) company.getRating());
    }

    @Override
    public int getItemCount() {
        return (companies == null) ? 0 : companies.size();
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
        notifyDataSetChanged();
    }

    static class CompaniesStandingsViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private RatingBar rating;

        public CompaniesStandingsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.company_standing_item_name);
            rating = itemView.findViewById(R.id.company_standing_item_rating);
        }
    }
}
