package com.exam.potholes.UI.Login;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exam.potholes.MainActivity;
import com.exam.potholes.databinding.LoginFragmentBinding;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private LoginFragmentBinding binding;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.loginButton.setOnClickListener(view -> loginButtonClick() );

        return root;
    }

    private boolean validateLoginInput() {
        boolean validated = true;

        if(binding.loginNicknameInput.length() == 0){
            binding.loginNicknameInput.setError("Il campo Nickname non può essere vuoto");
            validated = false;
        }
        if(!binding.loginNicknameInput.getText().toString().matches("^[a-zA-Z0-9]*$")){
            binding.loginNicknameInput.setError("Il campo Nickname può contenere solo lettere e numeri");
            validated = false;
        }

        return validated;
    }

    private void loginButtonClick(){
        if (validateLoginInput()){
            mViewModel.login(getContext(),binding.loginNicknameInput.getText().toString());
            ((MainActivity)getActivity()).changeView("home");
        }
    }

}