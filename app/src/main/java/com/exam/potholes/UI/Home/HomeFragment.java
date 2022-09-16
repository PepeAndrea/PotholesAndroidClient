package com.exam.potholes.UI.Home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exam.potholes.DataAccess.Repository.LoginRepository;
import com.exam.potholes.databinding.HomeFragmentBinding;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;
    private HomeViewModel mViewModel;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.messageText.setOnClickListener(view -> mViewModel.Hello(view.getContext()));
        binding.messageText.setText(LoginRepository.getInstance().getSavedNickname(getContext()));

        return root;

    }


}