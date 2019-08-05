package amarjob.com.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amarjob.com.Interface.ProfileInterface;
import amarjob.com.model.District;
import amarjob.com.model.JobCategory;
import amarjob.com.model.JobTitle;
import amarjob.com.model.User;
import amarjob.com.otherClasses.SharedPref;

public class HomeRepo {
    private FirebaseDatabase firebaseDatabase;
    private Application application;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MutableLiveData<User> mutableLiveData;


    public HomeRepo(Application application) {
        this.application = application;
        sharedPreferences = application.getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    public MutableLiveData<User> getGeneralInfo() {


        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }

        final DatabaseReference showUserInfoDB = firebaseDatabase.getReference().child("UserList");
        showUserInfoDB.child(sharedPreferences.getString(SharedPref.USER_ID, "")).child("GeneralInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("name").exists()) {
                        final User user = dataSnapshot.getValue(User.class);
                        mutableLiveData.postValue(user);
                    }
                    else {
                        mutableLiveData.postValue(new User());
                    }
                } else {
                    mutableLiveData.postValue(new User());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mutableLiveData;
    }

}