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

import amarjob.com.R;
import amarjob.com.databinding.ActivityProfileBinding;
import amarjob.com.model.User;
import amarjob.com.view.fragment.ImageZoomingDialog;
import amarjob.com.viewmodel.HomeViewModel;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    private HomeViewModel homeViewModel;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,(R.layout.activity_profile));
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
                    binding.poneTV.setText(user.getMobileNumber());
                }
                profileImageUrl = user.getProfileImage();
                if (profileImageUrl != null && !profileImageUrl.equals("")) {
                    Glide.with(ProfileActivity.this).applyDefaultRequestOptions(new RequestOptions()
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
