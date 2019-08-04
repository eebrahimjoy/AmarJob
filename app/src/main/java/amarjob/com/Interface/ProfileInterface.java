package amarjob.com.Interface;

import androidx.lifecycle.MutableLiveData;

import amarjob.com.model.User;


public interface ProfileInterface {

    MutableLiveData<Integer> updateProfile(User user);

    MutableLiveData<User> getGeneralInfo();
}

