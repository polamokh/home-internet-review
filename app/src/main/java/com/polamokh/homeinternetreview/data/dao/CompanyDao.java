package com.polamokh.homeinternetreview.data.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.polamokh.homeinternetreview.data.Company;
import com.polamokh.homeinternetreview.data.Review;

public class CompanyDao implements IDao<Company> {

    private static final String TAG = CompanyDao.class.getSimpleName();

    public enum updateState {
        INSERTED, REMOVED
    }

    private final String tableName = "companies";

    private final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference(tableName);

    private static CompanyDao instance;

    public static CompanyDao getInstance() {
        if (instance == null)
            instance = new CompanyDao();
        return instance;
    }

    @Override
    public Task<Void> create(Company obj) {
        return databaseReference.child(obj.getName()).setValue(obj);
    }

    @Override
    public Task<Void> edit(String id, Company obj) {
        return databaseReference.child(id).setValue(obj);
    }

    @Override
    public Task<Void> delete(String id) {
        return databaseReference.child(id).removeValue();
    }

    @Override
    public DatabaseReference getAll() {
        return databaseReference;
    }

    @Override
    public DatabaseReference getById(String id) {
        return databaseReference.child(id);
    }

    public void updateCompaniesStandings(Review review, updateState updateState) {
        getById(review.getCompany())
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Company company = currentData.getValue(Company.class);
                        switch (updateState) {
                            case INSERTED:
                                updateCompaniesStandingsInsertedReview(company, review);
                                break;
                            case REMOVED:
                                updateCompaniesStandingsRemovedReview(company, review);
                                break;

                            default:
                        }
                        currentData.setValue(company);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed,
                                           @Nullable DataSnapshot currentData) {
                        Log.d(TAG, "onComplete: " + error);
                    }
                });
    }

    private void updateCompaniesStandingsInsertedReview(Company company, Review review) {
        if (company != null) {
            int newNumOfRatings = company.getNumOfRatings() + 1;
            double newRating = (company.getRating() * company.getNumOfRatings()
                    + review.getRating()) / newNumOfRatings;

            company.setNumOfRatings(newNumOfRatings);
            company.setRating(newRating);
            edit(company.getName(), company);
        } else
            create(new Company(review.getCompany(),
                    review.getRating(), 1));
    }

    private void updateCompaniesStandingsRemovedReview(Company company, Review review) {
        if (company != null) {
            int newNumOfRatings = company.getNumOfRatings() - 1;
            if (newNumOfRatings == 0) {
                delete(company.getName());
                return;
            }

            double newRating = (company.getRating() * company.getNumOfRatings()
                    - review.getRating()) / newNumOfRatings;

            company.setNumOfRatings(newNumOfRatings);
            company.setRating(newRating);
            edit(company.getName(), company);
        }
    }
}
