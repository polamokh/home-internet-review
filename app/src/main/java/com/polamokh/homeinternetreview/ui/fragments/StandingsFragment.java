package com.polamokh.homeinternetreview.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private RecyclerView standingsRecyclerView;
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

        standingsRecyclerView = view.findViewById(R.id.standings_recycler_view);

        adapter = new CompaniesStandingsAdapter();

        standingsViewModel.getAll().observe(getViewLifecycleOwner(), companies -> {
            adapter.setCompanies(companies);
        });

        standingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        standingsRecyclerView.setAdapter(adapter);
    }
}
