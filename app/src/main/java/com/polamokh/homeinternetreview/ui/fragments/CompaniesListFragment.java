package com.polamokh.homeinternetreview.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.adapters.CompaniesAdapter;
import com.polamokh.homeinternetreview.ui.listeners.IOnItemSelectListener;

public class CompaniesListFragment extends Fragment implements IOnItemSelectListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_companies_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.companies_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new CompaniesAdapter(
                requireContext().getResources().getStringArray(R.array.companies), this));

        return view;
    }

    @Override
    public void OnItemSelected(Object object) {
        Bundle bundle = new Bundle();
        bundle.putString(CreateReviewFragment.EXTRA_COMPANY_NAME, object.toString());

        Navigation.findNavController(requireView())
                .navigate(R.id.action_companiesListFragment_to_governoratesListFragment,
                        bundle);
    }
}
