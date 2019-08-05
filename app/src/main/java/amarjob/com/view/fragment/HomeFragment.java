package amarjob.com.view.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.Map;

import amarjob.com.Interface.OnMenuItemClickedListener;
import amarjob.com.R;
import amarjob.com.databinding.FragmentHomeBinding;
import amarjob.com.model.User;
import amarjob.com.otherClasses.SharedPref;
import amarjob.com.viewmodel.HomeViewModel;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private OnMenuItemClickedListener onMenuItemClickedListener;
    private BottomNavigationView bottomNavigationView;
    private HomeViewModel homeViewModel;
    private String profileImageUrl;

    public HomeFragment() {

    }

    @SuppressLint("ValidFragment")
    public HomeFragment(OnMenuItemClickedListener onMenuItemClickedListener, BottomNavigationView bottomNavigationView) {
        this.onMenuItemClickedListener = onMenuItemClickedListener;
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        binding.swipeRefreshLayout.setRefreshing(true);

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItem();
            }
        });

        init();
        initViewModel();
        setProfileInfo();
        return view;


    }

    private void refreshItem() {
        setProfileInfo();

    }
    private void init() {
        sharedPreferences = getActivity().getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
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
                    binding.phoneNumberTV.setText(user.getMobileNumber());
                }
                profileImageUrl = user.getProfileImage();
                if (profileImageUrl != null && !profileImageUrl.equals("")) {
                    Glide.with(getActivity()).applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.person_icon)).load(profileImageUrl).into(binding.profileImage);
                }

                onItemLoadComplete();
            }
        });
    }
    private void onItemLoadComplete() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }


}
