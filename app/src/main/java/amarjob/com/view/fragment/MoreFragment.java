package amarjob.com.view.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import amarjob.com.R;
import amarjob.com.databinding.FragmentMoreBinding;
import amarjob.com.otherClasses.CustomProgressDialog;
import amarjob.com.otherClasses.SharedPref;
import amarjob.com.view.activity.LoginActivity;
import amarjob.com.view.activity.SplashActivity;
import amarjob.com.viewmodel.SearchJobViewModel;

public class MoreFragment extends Fragment {
    private FragmentMoreBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private SearchJobViewModel searchJobViewModel;

    private Resources res;


    public MoreFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_more, container, false);
        View view = binding.getRoot();

        init();

        binding.termsOfUseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* startActivity(new Intent(getActivity(), TermsAndConditionActivity.class));*/
            }
        });

        binding.helpLineTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* startActivity(new Intent(getActivity(), HelplineActivity.class));*/
            }
        });

        binding.banglaRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true){
                    binding.englishRB.setChecked(false);
                    binding.englishRB.setTextColor(res.getColor(R.color.gray));
                    binding.banglaRB.setTextColor(res.getColor(R.color.colorAccent));
                                    }

            }
        });
        binding.englishRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true){
                    binding.banglaRB.setChecked(false);
                    binding.banglaRB.setTextColor(res.getColor(R.color.gray));
                    binding.englishRB.setTextColor(res.getColor(R.color.colorPrimary));
                }

            }
        });

        binding.logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = createCustomProgressDialog();

                searchJobViewModel.updateToken("").observe(getActivity(), new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        if (s.equals("201")) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            startActivity(new Intent(getActivity(), LoginActivity.class).
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            editor.remove(SharedPref.USER_ID);
                            editor.remove(SharedPref.IS_UPDATED);
                            editor.apply();
                        }
                    }
                });

            }
        });






        return view;
    }



    private ProgressDialog createCustomProgressDialog() {
        ProgressDialog progressDialog = CustomProgressDialog.createProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.isIndeterminate();
        return progressDialog;
    }



    private void init() {
        searchJobViewModel = ViewModelProviders.of(getActivity()).get(SearchJobViewModel.class);
        sharedPreferences = getActivity().getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        res = getActivity().getResources();
    }



}
