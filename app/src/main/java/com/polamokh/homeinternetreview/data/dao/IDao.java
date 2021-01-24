package com.polamokh.homeinternetreview.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public interface IDao<T> {
    Task<Void> create(T obj);

    Task<Void> edit(String id, T obj);

    Task<Void> delete(String id);

    DatabaseReference getAll();

    DatabaseReference getById(String id);
}
