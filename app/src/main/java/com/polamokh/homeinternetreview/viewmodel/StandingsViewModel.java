package com.polamokh.homeinternetreview.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.polamokh.homeinternetreview.data.Company;
import com.polamokh.homeinternetreview.data.dao.CompanyDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StandingsViewModel extends ViewModel {

    private final CompanyDao companyDao = CompanyDao.getInstance();

    private List<Company> companies;
    private MutableLiveData<List<Company>> companiesLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;
    private MutableLiveData<Boolean> hasAvailableCompaniesLiveData;

    public LiveData<List<Company>> getAll() {
        if (companiesLiveData == null) {
            companiesLiveData = new MutableLiveData<>();

            companyDao.getAll().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    companies = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        companies.add(dataSnapshot.getValue(Company.class));
                    }

                    Collections.sort(companies, Collections.reverseOrder());
                    hasAvailableCompaniesLiveData.postValue(companies.size() != 0);
                    companiesLiveData.postValue(companies);
                    isLoadingLiveData.postValue(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    isLoadingLiveData.postValue(false);
                }
            });
        }

        return companiesLiveData;
    }

    public LiveData<Boolean> isLoading() {
        if (isLoadingLiveData == null) {
            isLoadingLiveData = new MutableLiveData<>();
            isLoadingLiveData.setValue(true);
        }

        return isLoadingLiveData;
    }

    public LiveData<Boolean> hasAvailableCompanies() {
        if (hasAvailableCompaniesLiveData == null) {
            hasAvailableCompaniesLiveData = new MutableLiveData<>();
            hasAvailableCompaniesLiveData.setValue(true);
        }

        return hasAvailableCompaniesLiveData;
    }
}
