package com.polamokh.homeinternetreview.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Company;
import com.polamokh.homeinternetreview.utils.CompanyUtils;

import java.util.List;
import java.util.Locale;

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

        holder.rank.setText(String.format(Locale.getDefault(), "%d", position + 1));
        CompanyUtils.setCompanyPicture(company.getName(), holder.picture);
        holder.name.setText(company.getName());
        holder.ratingNum.setText(String.format(Locale.getDefault(), "%.1f", company.getRating()));
        holder.rating.setRating((float) company.getRating());
        holder.numOfRatings.setText(String.format(Locale.getDefault(), "%d", company.getNumOfRatings()));
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
        private TextView numOfRatings;
        private ImageView picture;
        private TextView rank;
        private TextView ratingNum;

        CompaniesStandingsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.company_standing_item_name);
            rating = itemView.findViewById(R.id.company_standing_item_rating);
            numOfRatings = itemView.findViewById(R.id.company_standing_item_num_of_ratings);
            picture = itemView.findViewById(R.id.company_standing_item_picture);
            rank = itemView.findViewById(R.id.company_standing_item_rank);
            ratingNum = itemView.findViewById(R.id.company_standing_item_rating_num);
        }
    }
}
