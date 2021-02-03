package com.polamokh.homeinternetreview.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polamokh.homeinternetreview.data.User;

public class UserDao implements IDao<User> {

    private final String tableName = "users";

    private final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference(tableName);

    private static UserDao instance;

    public static UserDao getInstance() {
        if (instance == null)
            instance = new UserDao();
        return instance;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public Task<Void> create(User obj) {
        return databaseReference.child(obj.getId()).setValue(obj);
    }

    @Override
    public Task<Void> edit(String id, User obj) {
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
