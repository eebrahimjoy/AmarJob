package amarjob.com.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import amarjob.com.otherClasses.District;
import amarjob.com.otherClasses.JobCategory;
import amarjob.com.otherClasses.JobTitle;
import amarjob.com.otherClasses.SharedPref;

public class SearchJobRepo {
    private FirebaseDatabase firebaseDatabase;
    private Application application;
    private SharedPreferences sharedPreferences;

    public SearchJobRepo(Application application) {
        this.application = application;
        sharedPreferences = application.getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    public MutableLiveData<List<JobTitle>> getJobTitle() {


        final MutableLiveData<List<JobTitle>> listMutableLiveData = new MutableLiveData<>();

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("AdminSection")
                .child("JobTitleList");

        databaseReference.orderByChild("titleName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<JobTitle> titles = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        JobTitle title = data.getValue(JobTitle.class);
                        titles.add(title);
                    }

                    listMutableLiveData.postValue(titles);
                } else {
                    listMutableLiveData.postValue(titles);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return listMutableLiveData;
    }

    public MutableLiveData<List<District>> getDistrict() {


        final MutableLiveData<List<District>> listMutableLiveData = new MutableLiveData<>();

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("AdminSection")
                .child("DistrictList");

        databaseReference.orderByChild("districtName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<District> districts = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        District district = data.getValue(District.class);
                        districts.add(district);
                    }

                    listMutableLiveData.postValue(districts);
                } else {
                    listMutableLiveData.postValue(districts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return listMutableLiveData;
    }

    public MutableLiveData<List<JobCategory>> getCategory() {


        final MutableLiveData<List<JobCategory>> listMutableLiveData = new MutableLiveData<>();

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("AdminSection")
                .child("JobCategoryList");

        databaseReference.orderByChild("catName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<JobCategory> categories = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        JobCategory jobCategory = data.getValue(JobCategory.class);
                        categories.add(jobCategory);
                    }

                    listMutableLiveData.postValue(categories);
                } else {
                    listMutableLiveData.postValue(categories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return listMutableLiveData;
    }


}
