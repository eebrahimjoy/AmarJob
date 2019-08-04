package amarjob.com.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import amarjob.com.otherClasses.District;
import amarjob.com.otherClasses.JobCategory;
import amarjob.com.otherClasses.JobTitle;
import amarjob.com.repository.SearchJobRepo;

public class SearchJobViewModel extends AndroidViewModel {
    SearchJobRepo searchJobRepo;

    public SearchJobViewModel(@NonNull Application application) {
        super(application);
        searchJobRepo = new SearchJobRepo(application);
    }
    public MutableLiveData<List<JobTitle>> getJobTitle() {
        return searchJobRepo.getJobTitle();
    }

    public MutableLiveData<List<District>> getDistrict() {
        return searchJobRepo.getDistrict();
    }
    public MutableLiveData<List<JobCategory>> getCategory() {
        return searchJobRepo.getCategory();
    }
}
