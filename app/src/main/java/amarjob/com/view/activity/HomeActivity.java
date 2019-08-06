package amarjob.com.view.activity;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import amarjob.com.Interface.OnMenuItemClickedListener;
import amarjob.com.Interface.OnNetworkStateChangeListener;
import amarjob.com.R;
import amarjob.com.databinding.ActivityHomeBinding;
import amarjob.com.otherClasses.ConnectivityHelper;
import amarjob.com.otherClasses.CustomVisibility;
import amarjob.com.otherClasses.SharedPref;
import amarjob.com.view.fragment.HomeFragment;
import amarjob.com.view.fragment.MoreFragment;
import amarjob.com.view.receiver.NetworkChangeReceiver;


public class HomeActivity extends AppCompatActivity implements OnMenuItemClickedListener, OnNetworkStateChangeListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ActivityHomeBinding binding;
    private int currentPage = 0;

    private boolean isFirstTime = false;
    private NetworkChangeReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



        if (getIntent().getExtras()!=null){
            isFirstTime = getIntent().getBooleanExtra("isFirstTime",false);
        }

        init();




        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            replaceFragment(new HomeFragment(this,binding.navigation));
            currentPage = 0;

    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mNetworkReceiver = new NetworkChangeReceiver(this);
        registerNetworkBroadcast();
    }




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (currentPage!=0){
                        currentPage = 0;
                        replaceFragment(new HomeFragment(HomeActivity.this,binding.navigation));
                    }
                    return true;
                case R.id.navigation_hotJobs:
                    if (currentPage!=1){
                        currentPage = 1;
                        /*replaceFragment(new HomeFragment());*/
                    }
                    return true;
                case R.id.navigation_amarjob:
                    if (currentPage!=2){
                        currentPage = 2;
                        /*replaceFragment(new HomeFragment());*/
                    }
                    return true;
                case R.id.navigation_more:
                    if (currentPage!=3){
                        currentPage = 3;
                        replaceFragment(new MoreFragment());
                    }
                    return true;
            }
            return false;
        }
    };


    @Override
    public void onBackPressed() {
        if (currentPage==0){
            super.onBackPressed();
        }else {
            View view = binding.navigation.findViewById(R.id.navigation_home);
            view.performClick();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,fragment);
        ft.commit();
    }


    @Override
    public void onClicked(View view) {
        view.performClick();
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
            binding.noInternetTV.setText("Back to online");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomVisibility.collapse(binding.noInternetTV,500);
                }
            },2000);
        } else {
            binding.noInternetTV.setBackgroundColor(getResources().getColor(R.color.red));
            binding.noInternetTV.setText("No internet connection");
            CustomVisibility.expand(binding.noInternetTV,500);
        }
    }
}
