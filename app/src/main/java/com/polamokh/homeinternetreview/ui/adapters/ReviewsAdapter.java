package com.polamokh.homeinternetreview.ui.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.data.User;
import com.polamokh.homeinternetreview.data.dao.UserDao;
import com.polamokh.homeinternetreview.ui.listeners.IOnItemSelectListener;
import com.polamokh.homeinternetreview.utils.CompanyUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private final RequestManager glide;
    private IOnItemSelectListener listener;
    private List<Review> reviews;

    public ReviewsAdapter(RequestManager glide) {
        this.glide = glide;
    }

    public ReviewsAdapter(RequestManager glide, IOnItemSelectListener listener) {
        this.glide = glide;
        this.listener = listener;
    }

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

        UserDao.getInstance().getById(review.getUserId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().getValue(User.class);
                        if (user != null) {
                            glide.load(user.getProfilePictureUrl())
                                    .placeholder(R.drawable.ic_profile_24dp)
                                    .circleCrop()
                                    .into(holder.profilePic);

                            holder.userName.setText(user.getName());
                        }
                    }
                });

        holder.rating.setRating((float) review.getRating());
        setReviewDescription(holder, review);
        holder.governorate.setText(review.getGovernorate());
        CompanyUtils.setCompanyPicture(review.getCompany(), holder.companyPic);

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

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.OnItemSelected(review);
                return true;
            }
            return false;
        });
    }

    private void setReviewDescription(ReviewViewHolder holder, Review review) {
        if (TextUtils.isEmpty(review.getDescription()))
            holder.description.setVisibility(View.GONE);
        else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(review.getDescription());
        }
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
        ImageView companyPic;
        TextView governorate;
        TextView userName;
        RatingBar rating;
        TextView description;
        TextView time;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.review_item_user_profile_pic);
            companyPic = itemView.findViewById(R.id.review_item_company_pic);
            userName = itemView.findViewById(R.id.review_item_user_name);
            governorate = itemView.findViewById(R.id.review_item_governorate);
            rating = itemView.findViewById(R.id.review_item_rating);
            description = itemView.findViewById(R.id.review_item_description);
            time = itemView.findViewById(R.id.review_item_time);
        }
    }
}
