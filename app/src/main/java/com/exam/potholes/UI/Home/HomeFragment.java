package com.exam.potholes.UI.Home;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exam.potholes.DataAccess.Repository.LoginRepository;
import com.exam.potholes.MainActivity;
import com.exam.potholes.Model.Pothole;
import com.exam.potholes.R;
import com.exam.potholes.UI.Adapter.PotholesAdapter;
import com.exam.potholes.databinding.HomeFragmentBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;
    private HomeViewModel mViewModel;
    private RecyclerView potholesList;
    private PotholesAdapter potholesAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private Double currentLat,currentLon;


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

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        this.setCurrentPositionForFilter();

        this.potholesList = root.findViewById(R.id.potholesList);
        this.setupPathList();
        this.ObserveChange();

        binding.radiusFilterButton.setOnClickListener(view -> this.radiusFilterButtonClick(view));

        return root;

    }

    private void ObserveChange() {
        mViewModel.getPotholes(getContext()).observe(getViewLifecycleOwner(), new Observer<List<Pothole>>() {
            @Override
            public void onChanged(List<Pothole> potholes) {
                potholesAdapter.setPotholes(potholes);
            }
        });
    }

    private void setupPathList() {
        this.potholesAdapter = new PotholesAdapter();
        potholesList.setAdapter(this.potholesAdapter);
        potholesList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void radiusFilterButtonClick(View view) {
        if(binding.radiusInput.length() == 0){
            mViewModel.getPotholes(getContext()).observe(getViewLifecycleOwner(), new Observer<List<Pothole>>() {
                @Override
                public void onChanged(List<Pothole> potholes) {
                    potholesAdapter.setPotholes(potholes);
                }
            });
        }else{
            if (validateFilterInput()){
                mViewModel.getFilterPotholes(getContext(),binding.radiusInput.getText().toString()).observe(getViewLifecycleOwner(), new Observer<List<Pothole>>() {
                    @Override
                    public void onChanged(List<Pothole> potholes) {
                        potholesAdapter.setPotholes(potholes);
                    }
                });
            }
        }

    }
    private boolean validateFilterInput() {
        boolean validated = true;
        if(!binding.radiusInput.getText().toString().matches("^[0-9]*$")){
            binding.radiusInput.setError("Il campo Nickname può contenere solo numeri interi");
            validated = false;
        }
        return validated;
    }

    private void setCurrentPositionForFilter() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLat = location.getLatitude();
                            currentLon = location.getLongitude();
                            binding.latValue.setText(String.valueOf(currentLat));
                            binding.lonValue.setText(String.valueOf(currentLon));
                        }
                    }
                });
    }

}