package com.polamokh.homeinternetreview.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.adapters.CompaniesStandingsAdapter;
import com.polamokh.homeinternetreview.viewmodel.StandingsViewModel;

public class StandingsFragment extends Fragment {

    private StandingsViewModel standingsViewModel;

    private LinearLayout noDataLayout;
    private RecyclerView standingsRecyclerView;
    private ProgressBar progressBar;
    private CompaniesStandingsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_standings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeView(view);
    }

    private void initializeView(View view) {
        standingsViewModel = new ViewModelProvider(this)
                .get(StandingsViewModel.class);

        noDataLayout = view.findViewById(R.id.no_data_layout);
        progressBar = view.findViewById(R.id.progress);
        standingsRecyclerView = view.findViewById(R.id.standings_recycler_view);

        adapter = new CompaniesStandingsAdapter();

        standingsViewModel.getAll().observe(getViewLifecycleOwner(), companies -> {
            adapter.setCompanies(companies);
        });

        standingsViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                showLoading();
            else
                hideLoading();
        });

        standingsViewModel.hasAvailableCompanies().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                showCompanies();
            else
                hideCompanies();
        });

        standingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        standingsRecyclerView.setAdapter(adapter);
    }

    private void hideCompanies() {
        standingsRecyclerView.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.VISIBLE);
    }

    private void showCompanies() {
        noDataLayout.setVisibility(View.GONE);
        standingsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }
}
