package amarjob.com.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import amarjob.com.R;
import amarjob.com.databinding.ActivityPickLocationMapBinding;
import amarjob.com.model.PlaceAPI.Prediction;
import amarjob.com.view.adapter.PlacesAutoCompleteAdapter;


public class PickLocationMapActivity extends AppCompatActivity implements
        OnMapReadyCallback {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final Float DEFAULT_ZOOM = 14.5f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FragmentManager fragmentManager;
    private GoogleMapOptions googleMapOptions;
    private GoogleMap map;
    private Boolean locationPermissionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String address;
    private LatLng addressLatLng;
    private boolean isItemClicked = false;


    private ActivityPickLocationMapBinding binding;

    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_location_map);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initialize();

        getLocationPermission();
    }

    private void initialize() {
        fragmentManager = getSupportFragmentManager();
    }


    private void initializeMap() {
        googleMapOptions = new GoogleMapOptions();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.frameContainerID, supportMapFragment);
        fragmentTransaction.commitAllowingStateLoss();
        supportMapFragment.getMapAsync(this);
    }


    private void getDeviceLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        count = 0;
        try {
            if (locationPermissionsGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            binding.myLocationFAB.hide();
                            animateCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                        } else {
                            Toast.makeText(PickLocationMapActivity.this,"Unable to access current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }


    /*-------------------------------------------------------------
    on map ready -- start
    -------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        changeMapStyle(map);

        if (locationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            getDeviceLocation();

            map.setMyLocationEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    binding.progressBarId.setVisibility(View.VISIBLE);
                    binding.doneBtnID.setEnabled(false);
                    binding.doneBtnID.setTextColor(getResources().getColor(R.color.black));
                    binding.doneBtnID.setBackground(getResources().getDrawable(R.drawable.backgroud_disable));

                    if (count == 1) {
                        binding.myLocationFAB.show();
                    }

                }
            });


            map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {

                    count = 1;
                    LatLng cameraLatLng = map.getCameraPosition().target;

                    binding.pickLocationATVID.setEnabled(false);
                    binding.pickLocationATVID.dismissDropDown();

                    address = getAddress(cameraLatLng.latitude, cameraLatLng.longitude);
                    addressLatLng = cameraLatLng;
                    if (isItemClicked == false) {
                        binding.pickLocationATVID.setText(address);

                    }
                    isItemClicked = false;

                    if (address != null && !address.equals("") && !address.isEmpty()) {
                        binding.progressBarId.setVisibility(View.GONE);
                        binding.doneBtnID.setEnabled(true);
                        binding.doneBtnID.setTextColor(getResources().getColor(R.color.white));
                        binding.doneBtnID.setBackground(getResources().getDrawable(R.drawable.background_enabled));
                    } else {
                        binding.progressBarId.setVisibility(View.VISIBLE);
                        binding.doneBtnID.setEnabled(false);
                        binding.doneBtnID.setTextColor(getResources().getColor(R.color.black));
                        binding.doneBtnID.setBackground(getResources().getDrawable(R.drawable.backgroud_disable));
                    }
                }
            });

        }
    }

    private void animateCamera(LatLng latLng, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionsGranted = true;
                    initializeMap();
                } else {
                    ActivityCompat.requestPermissions(this,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            locationPermissionsGranted = true;
            initializeMap();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        locationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionsGranted = false;
                            return;
                        }
                    }
                    locationPermissionsGranted = true;
                    //initialize our map
                    initializeMap();
                }
            }
        }
    }

    public String getAddress(double lat, double lon) {
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


    public void onDoneBtnClick(View view) {

        address = binding.pickLocationATVID.getText().toString();
        if (address!=null && !address.equals("") && addressLatLng!=null){
            Intent intent = new Intent();
            intent.putExtra("Address", address);
            intent.putExtra("latitude", addressLatLng.latitude);
            intent.putExtra("longitude", addressLatLng.longitude);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void MyLocationBtnTap(View view) {
        getDeviceLocation();
    }


    private void openKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }


    //---------------------Top-left back button or phone's back button clicked---------------------

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        hideSoftKeyboard();
        super.onBackPressed();
    }


    private void changeMapStyle(GoogleMap map) {
        try {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style));


        } catch (Resources.NotFoundException e) {

        }
    }

    public void searchBtnTap(View view) {
        openKeyboard(binding.pickLocationATVID);
        binding.pickLocationATVID.setEnabled(true);
        binding.pickLocationATVID.setText("");
        loadData();

        addressLatLng = null;
        binding.doneBtnID.setEnabled(false);
        binding.doneBtnID.setTextColor(getResources().getColor(R.color.black));
        binding.doneBtnID.setBackground(getResources().getDrawable(R.drawable.backgroud_disable));

    }

    private void loadData() {

        final List<Prediction> predictions = new ArrayList<>();

        PlacesAutoCompleteAdapter placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getApplicationContext(), predictions);
        binding.pickLocationATVID.setThreshold(1);
        binding.pickLocationATVID.setAdapter(placesAutoCompleteAdapter);


        binding.pickLocationATVID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                binding.pickLocationATVID.setEnabled(true);
                binding.pickLocationATVID.setSelection(0);

                binding.pickLocationATVID.setEnabled(false);
                String address = predictions.get(position).getDescription();
                LatLng addressLatLng = getLatLng(address);
                if (addressLatLng != null) {
                    isItemClicked = true;
                    animateCamera(addressLatLng, DEFAULT_ZOOM);
                    binding.pickLocationATVID.dismissDropDown();
                }
            }
        });

    }


    private LatLng getLatLng(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(address, 5);

            if (addresses != null && addresses.size() > 0) {

                Address location = addresses.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;

    }


    private void hideSoftKeyboard() {
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}

