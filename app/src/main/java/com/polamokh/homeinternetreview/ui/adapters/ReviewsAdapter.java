package com.polamokh.homeinternetreview.ui.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.utils.FirebaseStorageUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> reviews;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        FirebaseStorageUtils.getProfilePictureUri(review.getUserId())
                .addOnFailureListener(e -> {
                    Glide.with(holder.itemView)
                            .load(Uri.parse(""))
                            .placeholder(R.drawable.ic_profile_24dp)
                            .circleCrop()
                            .into(holder.profilePic);
                })
                .addOnSuccessListener(uri -> {
                    Glide.with(holder.itemView)
                            .load(uri)
                            .placeholder(R.drawable.ic_profile_24dp)
                            .circleCrop()
                            .into(holder.profilePic);
                });
        holder.rating.setRating((float) review.getRating());
        holder.description.setText(review.getDescription());

        Date time = new Date(review.getTime());
        String timeFormatter = DateFormat.getDateInstance().format(time);
        holder.time.setText(timeFormatter);
    }

    @Override
    public int getItemCount() {
        return (reviews == null) ? 0 : reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        if (this.reviews == null) {
            this.reviews = reviews;
            notifyItemRangeChanged(0, reviews.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return ReviewsAdapter.this.reviews.size();
                }

                @Override
                public int getNewListSize() {
                    return reviews.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return ReviewsAdapter.this.reviews.get(oldItemPosition)
                            .equals(reviews.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Review oldReview = ReviewsAdapter.this.reviews.get(oldItemPosition);
                    Review newReview = reviews.get(newItemPosition);
                    return newReview.getUserId().equals(oldReview.getUserId())
                            && newReview.getCompany().equals(oldReview.getCompany())
                            && newReview.getGovernorate().equals(oldReview.getGovernorate())
                            && newReview.getDescription().equals(oldReview.getDescription())
                            && newReview.getRating() == oldReview.getRating()
                            && newReview.getTime() == oldReview.getTime();
                }
            });

            this.reviews = reviews;
            result.dispatchUpdatesTo(this);
        }
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        RatingBar rating;
        TextView description;
        TextView time;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.review_item_profile_pic);
            rating = itemView.findViewById(R.id.review_item_rating);
            description = itemView.findViewById(R.id.review_item_description);
            time = itemView.findViewById(R.id.review_item_time);
        }
    }
}
