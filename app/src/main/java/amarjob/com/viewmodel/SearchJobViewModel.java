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
import amarjob.com.repository.SearchJobRepo;

public class SearchJobViewModel extends AndroidViewModel  implements ProfileInterface {
    SearchJobRepo searchJobRepo;

    public SearchJobViewModel(@NonNull Application application) {
        super(application);
        searchJobRepo = new SearchJobRepo(application);
    }
    @Override
    public MutableLiveData<Integer> updateProfile(User lawyer) {
        return searchJobRepo.updateProfile(lawyer);
    }

    @Override
    public MutableLiveData<User> getGeneralInfo() {
        return searchJobRepo.getGeneralInfo();
    }

    public MutableLiveData<String> saveImage(byte[] byteImage) {
        return searchJobRepo.saveImage(byteImage);
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
    public MutableLiveData<User> checkUser(String mobileNumber){
        return searchJobRepo.checkUser(mobileNumber);
    }
    public MutableLiveData<Integer> saveUserStatus(long createdDate, boolean isVerified) {
        return searchJobRepo.saveUserStatus(createdDate, isVerified);
    }
    public MutableLiveData<String> updateToken(String tokenId){
        return searchJobRepo.updateToken(tokenId);
    }


}
