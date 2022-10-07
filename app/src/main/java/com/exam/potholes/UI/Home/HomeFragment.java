package com.exam.potholes.UI.Home;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exam.potholes.Model.Pothole;
import com.exam.potholes.R;
import com.exam.potholes.UI.Adapter.PotholesAdapter;
import com.exam.potholes.databinding.HomeFragmentBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.LocationRequest;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;
    private HomeViewModel mViewModel;
    private RecyclerView potholesList;
    private PotholesAdapter potholesAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private Double currentLat = null,currentLon = null;
    private LocationRequest locationRequest;

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("LOCATION", "onLocationResult: "+locationResult.toString());
            if (locationResult.getLastLocation() != null) {
                currentLat = locationResult.getLastLocation().getLatitude();
                currentLon = locationResult.getLastLocation().getLongitude();
                binding.latValue.setText("Lat: " + String.valueOf(currentLat));
                binding.lonValue.setText("Lon: " + String.valueOf(currentLon));
            }

        }
    };


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
        locationRequest = LocationRequest.create().setInterval(1000*60*5).setPriority(LocationRequest.PRIORITY_LOW_POWER);
        this.setCurrentPositionForFilter();

        this.potholesList = root.findViewById(R.id.potholesList);
        this.setupPathList();
        this.ObserveChange();
        this.mViewModel.setupSessionRecording(getContext(),getViewLifecycleOwner());

        binding.radiusFilterButton.setOnClickListener(view -> this.radiusFilterButtonClick(view));
        binding.startStopRecording.setOnClickListener(view -> this.startStopSession(view));

        this.setupActionButton();

        return root;

    }

    private void setupActionButton() {
        if (this.mViewModel.isServiceRunning(getContext())){
            binding.startStopRecording.setText("Ferma registrazione");
        }else{
            binding.startStopRecording.setText("Avvia registrazione");
        }
    }

    private void startStopSession(View view) {
        if (this.mViewModel.isServiceRunning(getContext())){
            this.mViewModel.stopPotholesFinder(getContext());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mViewModel.getPotholes(getContext());
                }
            }).start();
        }else{
            binding.startStopRecording.setEnabled(false);
            this.mViewModel.startPotholesFinder(getContext());
        }
    }

    private void ObserveChange() {
        mViewModel.getObservablePotholes(getContext()).observe(getViewLifecycleOwner(), new Observer<List<Pothole>>() {
            @Override
            public void onChanged(List<Pothole> potholes) {
                potholesAdapter.setPotholes(potholes);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                mViewModel.getPotholes(getContext());
            }
        }).start();
    }

    private void setupPathList() {
        this.potholesAdapter = new PotholesAdapter();
        potholesList.setAdapter(this.potholesAdapter);
        potholesList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void radiusFilterButtonClick(View view) {
        if(binding.radiusInput.length() == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mViewModel.getPotholes(getContext());
                }
            }).start();
        }else{
            if (validateFilterInput()){
                while (currentLon == null && currentLat == null)
                    this.setCurrentPositionForFilter();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.getFilterPotholes(getContext(),binding.radiusInput.getText().toString(),currentLat,currentLon);
                    }
                }).start();
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
            new AlertDialog.Builder(getContext())
                    .setTitle("Permessi mancanti")
                    .setMessage("Per utilizzare l'app è necessario abilitare i permessi per la localizzazione.\nPer fare in modo che l'app funzioni anche in background, seleziona \"Consenti sempre\"")
                    .setPositiveButton("Concedi", (dialogInterface, i) -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101))
                    .setNegativeButton("Annulla", (dialogInterface, i) -> getActivity().finish())
                    .show();
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            Log.i("Permessi concessi", "Permessi concessi: procedo a localizzare l'utente");
            setCurrentPositionForFilter();
        }
    }

}