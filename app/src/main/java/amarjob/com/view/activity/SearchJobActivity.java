package amarjob.com.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import amarjob.com.R;
import amarjob.com.databinding.ActivitySearchJobBinding;
import amarjob.com.model.District;
import amarjob.com.model.JobCategory;
import amarjob.com.model.JobTitle;
import amarjob.com.viewmodel.SearchJobViewModel;

public class SearchJobActivity extends AppCompatActivity {
    private ActivitySearchJobBinding binding;
    private List<String> titleList;
    private List<String> districtList;
    private List<String> jobCategoryList;
    private SearchJobViewModel searchJobViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_job);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.userProfileImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchJobActivity.this,SignInActivity.class));
            }
        });


        init();
        getJobTitle();
        getDistrict();
        getCategory();
        setTitle();
        setDistrict();
        setCategory();


    }


    private void init() {
        binding.jobTitleSP.setTitle("Title");
        binding.districtSP.setTitle("Location");
        binding.jobCatSP.setTitle("Category");
        titleList = new ArrayList<>();
        districtList = new ArrayList<>();
        jobCategoryList = new ArrayList<>();
        searchJobViewModel = ViewModelProviders.of(this).get(SearchJobViewModel.class);
    }
    private void getJobTitle() {
        searchJobViewModel.getJobTitle().observe(this, new Observer<List<JobTitle>>() {
            @Override
            public void onChanged(List<JobTitle> titles) {
                titleList.add("Select Title");
                if (titles.size() > 0) {
                    for (JobTitle title : titles) {
                        titleList.add(title.getTitleName());
                    }
                }
                if (titleList != null && titleList.size() > 0) {
                    setTitle();
                }
            }
        });
    }

    private void getDistrict() {
        searchJobViewModel.getDistrict().observe(this, new Observer<List<District>>() {
            @Override
            public void onChanged(List<District> districts) {
                districtList.add("Select Location");
                if (districts.size() > 0) {
                    for (District district : districts) {
                        districtList.add(district.getDistrictName());
                    }
                }
                if (districtList != null && districtList.size() > 0) {
                    setDistrict();
                }
            }
        });
    }

    private void getCategory() {
        searchJobViewModel.getCategory().observe(this, new Observer<List<JobCategory>>() {
            @Override
            public void onChanged(List<JobCategory> categories) {
                jobCategoryList.add("Select Category");
                if (categories.size() > 0) {
                    for (JobCategory category : categories) {
                        jobCategoryList.add(category.getCatName());
                    }
                }
                if (districtList != null && districtList.size() > 0) {
                    setCategory();
                }
            }
        });
    }

    private void setTitle() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.jobTitleSP.setAdapter(adapter);
    }


    private void setDistrict() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districtList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.districtSP.setAdapter(adapter);
    }

    private void setCategory() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jobCategoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.jobCatSP.setAdapter(adapter);
    }


}
