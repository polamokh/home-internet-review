package com.polamokh.homeinternetreview.data.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polamokh.homeinternetreview.data.Company;
import com.polamokh.homeinternetreview.data.Review;

public class CompanyDao implements IDao<Company> {

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
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Company company = snapshot.getValue(Company.class);
                        switch (updateState) {
                            case INSERTED:
                                updateCompaniesStandingsInsertedReview(company, review);
                                return;
                            case REMOVED:
                                updateCompaniesStandingsRemovedReview(company, review);
                                return;

                            default:
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateCompaniesStandingsInsertedReview(Company company, Review review) {
        if (company != null) {
            int newNumOfRatings = company.getNumOfRatings() + 1;
            double newRating = (company.getRating() * company.getNumOfRatings()
                    + review.getRating()) / newNumOfRatings;

            edit(company.getName(),
                    new Company(company.getName(), newRating, newNumOfRatings));
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

            edit(company.getName(),
                    new Company(company.getName(), newRating, newNumOfRatings));
        }
    }
}
