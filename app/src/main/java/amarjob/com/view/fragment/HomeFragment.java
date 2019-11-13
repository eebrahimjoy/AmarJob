package amarjob.com.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amarjob.com.Interface.OnMenuItemClickedListener;
import amarjob.com.R;
import amarjob.com.databinding.FragmentHomeBinding;
import amarjob.com.model.Job;
import amarjob.com.model.User;
import amarjob.com.otherClasses.SharedPref;
import amarjob.com.view.activity.ProfileActivity;
import amarjob.com.view.adapter.FilterJobAdapter;
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
    private FilterJobAdapter filterJobAdapter;
    private List<Job> jobList;

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
        binding.profileL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Under Development", Toast.LENGTH_SHORT).show();
            }
        });

        init();
        initViewModel();
        //setProfileInfo();
        initRecyclerView();
        return view;


    }

    private void refreshItem() {
        setProfileInfo();

    }
    private void init() {
        jobList = new ArrayList<>();
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
    private void initRecyclerView() {

        //dummy

        Job job = new Job("1","dummy","07/11/2019","10.08 AM","Rice","01");
        Job job1 = new Job("2","dummy","01/11/2019","01.30 PM","Egg","02");
        jobList.add(job);
        jobList.add(job1);

        //
        binding.jobFilterRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        filterJobAdapter = new FilterJobAdapter(jobList,getActivity());
        binding.jobFilterRV.setAdapter(filterJobAdapter);
    }
    private void onItemLoadComplete() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }


}
