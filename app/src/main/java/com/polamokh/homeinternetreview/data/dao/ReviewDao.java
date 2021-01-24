package com.polamokh.homeinternetreview.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polamokh.homeinternetreview.data.Review;

public class ReviewDao implements IDao<Review> {

    private final String tableName = "reviews";

    private final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference();

    private static ReviewDao instance;

    public static ReviewDao getInstance() {
        if (instance == null)
            instance = new ReviewDao();
        return instance;
    }

    @Override
    public Task<Void> create(Review obj) {
        return databaseReference.child(tableName).push().setValue(obj);
    }

    @Override
    public Task<Void> edit(String id, Review obj) {
        return databaseReference.child(tableName).child(id).setValue(obj);
    }


    @Override
    public Task<Void> delete(String id) {
        return databaseReference.child(tableName).child(id).removeValue();
    }

    @Override
    public DatabaseReference getAll() {
        return databaseReference.child(tableName);
    }

    @Override
    public DatabaseReference getById(String id) {
        return databaseReference.child(tableName).child(id);
    }

}
