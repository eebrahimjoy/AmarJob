package amarjob.com.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import amarjob.com.R;
import amarjob.com.databinding.ActivitySplashBinding;
import amarjob.com.otherClasses.PrefManager;
import amarjob.com.otherClasses.SharedPref;

import static android.view.View.VISIBLE;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LocationManager manager;
    private boolean currentStatus = false;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        sharedPreferences = getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }



    @Override
    protected void onResume() {
        super.onResume();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showDialogForLocation();
        } else {
            currentStatus = true;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                startNextActivity();
            } else {
                prefManager = new PrefManager(this);
                if (prefManager.isFirstTimeLaunch()) {
                    launchHomeScreen();
                    finish();
                } else {
                    revealAction();
                }
            }

        }
    }

    private void launchHomeScreen() {
        binding.reveal.setVisibility(View.INVISIBLE);
        startActivity(new Intent(SplashActivity.this, SearchJobActivity.class));
        finish();
    }


    private void revealAction() {
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                revealButton();
                startNextActivity();

            }
        }, 2000);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealButton() {

        binding.reveal.setVisibility(VISIBLE);

        int cx = binding.reveal.getWidth();
        int cy = binding.reveal.getHeight();

        int x = (binding.reveal.getLeft() + binding.reveal.getRight()) / 2;
        int y = (binding.reveal.getTop() + binding.reveal.getBottom()) / 2;

        float finalRadius = Math.max(cx, cy) * 1.2f;

        Animator reveal = ViewAnimationUtils
                .createCircularReveal(findViewById(R.id.reveal), x, y, 64, finalRadius);

        reveal.setDuration(500);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });

        reveal.start();
    }

    private void startNextActivity() {

        if (currentStatus == true) {
            if (sharedPreferences.getString(SharedPref.USER_ID, "").equals("")) {
                startActivity(new Intent(SplashActivity.this, SearchJobActivity.class));
                finish();
                overridePendingTransition(R.anim.stay, R.anim.stay);
            } else if (sharedPreferences.getBoolean(SharedPref.IS_UPDATED, false) == false) {
                startActivity(new Intent(SplashActivity.this, UpdateProfileActivity.class).putExtra("isFirstTime",true));
                finish();
                overridePendingTransition(R.anim.stay, R.anim.stay);
            } else {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
                overridePendingTransition(R.anim.stay, R.anim.stay);
            }
        }

    }


    private void showDialogForLocation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_location, null);

        Button itsOk = mView.findViewById(R.id.itsOkBtnID);
        Button settings = mView.findViewById(R.id.settingsBtnID);

        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        itsOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                recreate();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            }
        });
    }

}

