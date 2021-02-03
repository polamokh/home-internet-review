package com.polamokh.homeinternetreview.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polamokh.homeinternetreview.data.Review;

public class ReviewDao implements IDao<Review> {

    private final String tableName = "reviews";

    private final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference(tableName);

    private static ReviewDao instance;

    public static ReviewDao getInstance() {
        if (instance == null)
            instance = new ReviewDao();
        return instance;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public Task<Void> create(Review obj) {
        return databaseReference.push().setValue(obj);
    }

    @Override
    public Task<Void> edit(String id, Review obj) {
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

}
