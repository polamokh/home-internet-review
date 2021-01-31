package com.polamokh.homeinternetreview.ui.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.ui.listeners.IOnItemSelectListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.MyReviewViewHolder> {

    private List<Review> reviews;
    private IOnItemSelectListener listener;

    public MyReviewsAdapter(IOnItemSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_review, parent, false);
        return new MyReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.rating.setRating((float) review.getRating());
        holder.description.setText(review.getDescription());

        Date time = new Date(review.getTime());
        String timeFormatter = DateFormat.getDateInstance().format(time);
        holder.time.setText(timeFormatter);

        holder.description.setOnClickListener(v -> {
            TextView desc = (TextView) v;
            if (desc.getMaxLines() == Integer.MAX_VALUE) {
                desc.setMaxLines(3);
                desc.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                desc.setMaxLines(Integer.MAX_VALUE);
                desc.setEllipsize(null);
            }
        });

        holder.itemView.setOnClickListener(v -> listener.OnItemSelected(review));
    }

    @Override
    public int getItemCount() {
        return (reviews == null) ? 0 : reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    static class MyReviewViewHolder extends RecyclerView.ViewHolder {
        RatingBar rating;
        TextView description;
        TextView time;

        public MyReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            rating = itemView.findViewById(R.id.my_review_item_rating);
            description = itemView.findViewById(R.id.my_review_item_description);
            time = itemView.findViewById(R.id.my_review_item_time);
        }
    }
}
