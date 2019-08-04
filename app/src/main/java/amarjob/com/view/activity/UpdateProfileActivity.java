package amarjob.com.view.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amarjob.com.Interface.OnNetworkStateChangeListener;
import amarjob.com.R;
import amarjob.com.databinding.ActivityUpdateProfileBinding;
import amarjob.com.model.User;
import amarjob.com.otherClasses.ConnectivityHelper;
import amarjob.com.otherClasses.CustomProgressDialog;
import amarjob.com.otherClasses.CustomVisibility;
import amarjob.com.otherClasses.EmailMatcher;
import amarjob.com.otherClasses.SharedPref;
import amarjob.com.view.activity.HomeActivity;
import amarjob.com.view.bottomsheet.SelectImageBottomSheet;
import amarjob.com.view.receiver.NetworkChangeReceiver;
import amarjob.com.viewmodel.SearchJobViewModel;

public class UpdateProfileActivity extends AppCompatActivity implements SelectImageBottomSheet.BottomSheetListener, OnNetworkStateChangeListener {

    private static final int REQUEST_CODE_ADDRESS = 3;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;
    private static final SimpleDateFormat SDFWithTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy");
    Date date1;
    private List<String> districts1 = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ActivityUpdateProfileBinding binding;
    private SearchJobViewModel searchJobViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SelectImageBottomSheet selectImageBottomSheet;
    private ProgressDialog progressDialog;
    private int i;


    private String dateOfBirth = "", profileImageUrl;
    private String gender = "";
    private String skillType = "";
    private String address = "";
    private double chamberLat, chamberLng;
    private String currentPhotoPath, districtBarName;
    private String status;
    private String nationalID = "";
    private String barAssociationId = "";
    private boolean isVerified;
    private int spinnerFirstPositionContainer = 0;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean locationPermissionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Geocoder geocoder;
    private List<Address> addresses;
    private boolean isFirstTime = false;
    private NetworkChangeReceiver mNetworkReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        init();


        binding.addressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(UpdateProfileActivity.this, PickLocationMapActivity.class), REQUEST_CODE_ADDRESS);
            }
        });

        binding.userProfileImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetToSelectImage();

            }
        });
        binding.dateOfBirthTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalender();
            }
        });

        binding.myCurrentLocationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.addressTV.setClickable(false);
                binding.addressErrorTV.setVisibility(View.GONE);
                getLocationPermission();
                getDeviceLocation();


            }
        });


        binding.updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        if (getIntent().getExtras() != null) {
            isFirstTime = getIntent().getBooleanExtra("isFirstTime", false);

            if (isFirstTime == false) {
                binding.backBtn.setVisibility(View.VISIBLE);
                //getLawyerInfoFromGeneralInfoFragment();
            } else if (isFirstTime == true) {
                binding.backBtn.setVisibility(View.GONE);
            }

        } else {
            binding.backBtn.setVisibility(View.GONE);
        }

    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        searchJobViewModel = ViewModelProviders.of(this).get(SearchJobViewModel.class);
        mNetworkReceiver = new NetworkChangeReceiver(this);
        registerNetworkBroadcast();
    }


   /* private void getLawyerInfoFromGeneralInfoFragment() {
        binding.nameET.setText(getIntent().getStringExtra(StaticKeys.NAME));
        binding.nameET.setSelection(binding.nameET.length());
        binding.emailET.setText(getIntent().getStringExtra(StaticKeys.EMAIL));
        binding.nationalIDET.setText(getIntent().getStringExtra(StaticKeys.NID));
        binding.barAssociationNumET.setText(getIntent().getStringExtra(StaticKeys.BAID));
        binding.emailET.setSelection(binding.emailET.length());
        dateOfBirth = getIntent().getStringExtra(StaticKeys.DATE_OF_BIRTH);
        profileImageUrl = getIntent().getStringExtra(StaticKeys.PROFILE_IMAGE);
        chamberLat = getIntent().getDoubleExtra(StaticKeys.LAT, 0);
        chamberLng = getIntent().getDoubleExtra(StaticKeys.LNG, 0);
        districtBarName = getIntent().getStringExtra(StaticKeys.DISTRICT);
        binding.searchDistrictSV.setText(districtBarName);
        gender = getIntent().getStringExtra(StaticKeys.GENDER);

        address = getIntent().getStringExtra(StaticKeys.ADDRESS);

        if (!address.equals("")) {
            binding.addressTV.setText(getIntent().getStringExtra(StaticKeys.ADDRESS));
        }

        if (!dateOfBirth.equals("")) {
            binding.dateOfBirthTV.setText(dateOfBirth);
        }


        if (gender.equals("Male")) {
            binding.maleRBID.setChecked(true);
        } else if (gender.equals("Female")) {
            binding.femaleRBID.setChecked(true);
        }

        if (profileImageUrl != null && !profileImageUrl.equals("")) {
            Glide.with(getApplicationContext()).applyDefaultRequestOptions(new RequestOptions()
                    .placeholder(R.drawable.profile_image_nipon)).load(profileImageUrl).into(binding.userProfileImageID);
        }

    }*/

    public void updateProfile() {
        String name = binding.nameET.getText().toString();
        String email = binding.emailET.getText().toString();
        nationalID = binding.nationalIDET.getText().toString();

        if (binding.maleRBID.isChecked()) {
            gender = "Male";
        } else if (binding.femaleRBID.isChecked()) {
            gender = "Female";
        }


        if (binding.specialSkillRBID.isChecked()) {
            skillType = "Special Skilled Category";
        } else if (binding.functionalCatIV.isChecked()) {
            skillType = "Functional Category";
        }

        validateFields(name, email, nationalID);

    }

    private void validateFields(String name, String email, String nationalID) {
        if (profileImageUrl == null || profileImageUrl.equals("")) {
            binding.profileErrorTV.setVisibility(View.VISIBLE);
            binding.profileErrorTV.setError("Please select profile image");
            binding.profileErrorTV.requestFocus();
        } else if (name.trim().isEmpty() | name.trim().equals("")) {
            binding.nameET.setError("Enter valid name");
            binding.nameET.requestFocus();
        } else if (email.trim().isEmpty() || email.equals("") || EmailMatcher.validate(email) == false) {
            binding.emailET.setError("Enter valid email");
            binding.emailET.requestFocus();

        } else if (!nationalID.isEmpty() && nationalID.length() < 10) {
            binding.nationalIDET.setError("Enter valid national id");
            binding.nationalIDET.requestFocus();
        } else if (address.isEmpty() || address == null || address.equals("")) {
            binding.addressErrorTV.setVisibility(View.VISIBLE);
            binding.addressErrorTV.setText("Select address");
        } else {
            progressDialog = createCustomProgressDialog();

            User user = new User(sharedPreferences.getString(SharedPref.USER_ID, ""), name,
                    profileImageUrl, sharedPreferences.getString(SharedPref.MOBILE_NUMBER, ""),
                    email, address, gender, dateOfBirth, chamberLat, chamberLng, nationalID,skillType);
            searchJobViewModel.updateProfile(user).observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer responseCode) {
                    if (responseCode == 200) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(UpdateProfileActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                        editor.putBoolean(SharedPref.IS_UPDATED, true);
                        editor.apply();

                        if (isFirstTime == true) {
                            startActivity(new Intent(UpdateProfileActivity.this, HomeActivity.class).putExtra("isFirstTime", true));
                            finish();
                        } else {
                            onBackPressed();
                        }

                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "Failed to save", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    public void openCalender() {

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                month = month + 1;
                String seletedDate = year + "/" + month + "/" + dayOfMonth + " 00:00:00";


                Date date = null;
                try {
                    date = SDFWithTime.parse(seletedDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dateOfBirth = SDF.format(date);
                binding.dateOfBirthTV.setText(dateOfBirth);

            }
        };


        Date date = null;

        if (!dateOfBirth.equals("")) {
            try {
                date = SDF.parse(dateOfBirth);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Calendar cal = Calendar.getInstance();


        if (date != null) {

            cal.setTime(date);
        }
        if (date == null) {
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH) + 1;
            cal.set(year, month, day);
            cal.add(Calendar.YEAR, -18);
            cal.add(Calendar.DATE, -1);
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this, android.app.AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }


    public void openBottomSheetToSelectImage() {
        binding.profileErrorTV.setVisibility(View.GONE);
        selectImageBottomSheet = new SelectImageBottomSheet(this);
        selectImageBottomSheet.show(getSupportFragmentManager(), "selectImage");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADDRESS) {

            if (resultCode == RESULT_OK) {

                address = data.getStringExtra("Address");
                chamberLat = data.getDoubleExtra("latitude", 0);
                chamberLng = data.getDoubleExtra("longitude", 0);
                binding.addressTV.setText(address);
                binding.addressErrorTV.setVisibility(View.GONE);

            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setImageToView();

            progressDialog = createCustomProgressDialog();

        } else if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK) {

            Uri imageUrl = data.getData();
            CropImage.activity(imageUrl)
                    .setAspectRatio(1, 1)
                    .start(this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            progressDialog = createCustomProgressDialog();
            Uri resultUri = result.getUri();
            Log.d("Joy", "onActivityResult: " + resultUri.toString());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                saveImage(bitmap);
            }

        }
    }

    @Override
    public void onCameraButtonClicked() {
        dispatchTakePictureIntent();
        selectImageBottomSheet.dismiss();
    }

    @Override
    public void onGalleryButtonClicked() {
        Intent galaryIntent = new Intent();
        galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galaryIntent.setType("image/*");
        startActivityForResult(galaryIntent, REQUEST_SELECT_PHOTO);
        selectImageBottomSheet.dismiss();
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "amarjob.com.userprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void setImageToView() {

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);


        ExifInterface ei = null;
        try {
            ei = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }


        saveImage(rotatedBitmap);
    }


    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    private void saveImage(Bitmap bitmap) {

        binding.userProfileImageID.setImageBitmap(bitmap);
        searchJobViewModel.saveImage(convertToByte(bitmap)).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String imageUrl) {
                if (imageUrl != null && !imageUrl.equals("")) {
                    profileImageUrl = imageUrl;
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }


    private byte[] convertToByte(Bitmap bitmap) {
        byte[] byteImage;

        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();
        if (originalWidth > 1200 && originalHeight >= originalWidth) {

            float destWidth = 1200;
            float destHeight = originalHeight / (originalWidth / destWidth);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, Math.round(destWidth), Math.round(destHeight), false);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos1);
            byteImage = baos1.toByteArray();

        } else if (originalWidth > 1200 && originalHeight < originalWidth) {
            float destWidth = 1400;
            float destHeight = originalHeight / (originalWidth / destWidth);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, Math.round(destWidth), Math.round(destHeight), false);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos1);
            byteImage = baos1.toByteArray();

        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); //decodedFoodBitmap is the bitmap object
            byteImage = baos.toByteArray();

        }

        return byteImage;
    }

    private ProgressDialog createCustomProgressDialog() {
        ProgressDialog progressDialog = CustomProgressDialog.createProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.isIndeterminate();
        return progressDialog;
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
        }

    }


    private void getDeviceLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionsGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            chamberLat = currentLocation.getLatitude();
                            chamberLng = currentLocation.getLongitude();
                            geocoder = new Geocoder(UpdateProfileActivity.this, Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(chamberLat, chamberLng, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses.size() > 0) {
                                address = addresses.get(0).getAddressLine(0);
                            } else {
                                Toast.makeText(UpdateProfileActivity.this, "Unable to access current location", Toast.LENGTH_SHORT).show();
                            }
                            if (address != null) {
                                binding.addressTV.setText(address);
                                binding.addressTV.setClickable(true);
                            }

                        } else {
                            Toast.makeText(UpdateProfileActivity.this, "Unable to access current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
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
                    getDeviceLocation();
                }
            }
        }
    }


    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectivityHelper.isConnected(this) == true) {
            binding.noInternetTV.setVisibility(View.GONE);
        } else {
            binding.noInternetTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChange(boolean isConnected) {
        if (isConnected) {
            binding.noInternetTV.setBackgroundColor(getResources().getColor(R.color.green));
            binding.noInternetTV.setText("Back on online");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomVisibility.collapse(binding.noInternetTV, 500);
                }
            }, 2000);
        } else {
            binding.noInternetTV.setBackgroundColor(getResources().getColor(R.color.red));
            binding.noInternetTV.setText("No internet connection");
            CustomVisibility.expand(binding.noInternetTV, 500);
        }
    }


}

