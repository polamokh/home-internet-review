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
import com.polamokh.homeinternetreview.ui.adapters.GovernoratesAdapter;
import com.polamokh.homeinternetreview.ui.listeners.IOnItemClickListener;

public class GovernoratesListFragment extends Fragment implements IOnItemClickListener {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_governorates_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.governorates_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new GovernoratesAdapter(
                requireContext().getResources().getStringArray(R.array.governorates),
                this
        ));

        return view;
    }

    @Override
    public void OnItemClicked(Object object) {
        Bundle bundle = getArguments();
        bundle.putString(CreateReviewFragment.EXTRA_GOVERNORATE_NAME, object.toString());

        Navigation.findNavController(requireView())
                .navigate(R.id.action_governoratesListFragment_to_reviewDetailsOptionFragment,
                        bundle);
    }
}
