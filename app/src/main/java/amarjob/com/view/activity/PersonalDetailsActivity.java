package amarjob.com.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import amarjob.com.R;
import amarjob.com.databinding.ActivityPersonalDetailsBinding;
import amarjob.com.model.User;
import amarjob.com.view.fragment.ImageZoomingDialog;
import amarjob.com.viewmodel.HomeViewModel;

public class PersonalDetailsActivity extends AppCompatActivity {
    private ActivityPersonalDetailsBinding binding;
    private String profileImageUrl;
    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_personal_details);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.profileImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileImageUrl != null && !profileImageUrl.equals("")) {
                    openImageZoomingDialog(profileImageUrl);
                }
            }
        });

        initViewModel();
        setProfileInfo();
    }
    private void initViewModel() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    private void setProfileInfo() {
        homeViewModel.getGeneralInfo().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user.getName() != null && !user.getName().equals("")) {
                    binding.nameTV.setText(user.getName());
                }
                if (user.getBirthday() != null && !user.getBirthday().equals("")) {
                    binding.dateOfBirthTV.setText(user.getBirthday());

                }
                if (user.getGender() != null && !user.getGender().equals("")) {
                    binding.genderTV.setText(user.getGender());
                }
                if (user.getNationalID() != null && !user.getNationalID().equals("")) {
                    binding.nidNumberTV.setText(user.getNationalID());
                }
                profileImageUrl = user.getProfileImage();
                if (profileImageUrl != null && !profileImageUrl.equals("")) {
                    Glide.with(PersonalDetailsActivity.this).applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.person_icon)).load(profileImageUrl).into(binding.profileImageIV);
                }
            }
        });
    }
    private void openImageZoomingDialog(String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        ImageZoomingDialog dialog = new ImageZoomingDialog();
        dialog.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, "TAG");
    }
}
