package amarjob.com.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import amarjob.com.Interface.ProfileInterface;
import amarjob.com.model.District;
import amarjob.com.model.JobCategory;
import amarjob.com.model.JobTitle;
import amarjob.com.model.User;
import amarjob.com.repository.HomeRepo;
import amarjob.com.repository.SearchJobRepo;

public class HomeViewModel extends AndroidViewModel {
    HomeRepo homeRepo;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        homeRepo = new HomeRepo(application);
    }

    public MutableLiveData<User> getGeneralInfo() {
        return homeRepo.getGeneralInfo();
    }
}
