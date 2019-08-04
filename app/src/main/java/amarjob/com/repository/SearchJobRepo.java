package amarjob.com.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amarjob.com.model.District;
import amarjob.com.model.JobCategory;
import amarjob.com.model.JobTitle;
import amarjob.com.model.User;
import amarjob.com.otherClasses.SharedPref;

public class SearchJobRepo {
    private FirebaseDatabase firebaseDatabase;
    private Application application;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SearchJobRepo(Application application) {
        this.application = application;
        sharedPreferences = application.getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
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

    public MutableLiveData<User> checkUser(final String mobileNumber) {
        final MutableLiveData<User> liveData = new MutableLiveData<>();
        Query lawyerDB = firebaseDatabase.getReference().child("UserList");

        lawyerDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isMatch = false;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.child("GeneralInfo").exists()) {
                            User user = data.child("GeneralInfo").getValue(User.class);
                            if (user.getMobileNumber().equals(mobileNumber)) {
                                liveData.postValue(user);
                                isMatch = true;
                                break;
                            }
                        }
                    }

                    if (isMatch == false) {
                        liveData.postValue(new User());
                    }
                } else {
                    liveData.postValue(new User());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return liveData;

    }

    public MutableLiveData<Integer> saveUserStatus(long createdDate, boolean isVerified) {
        final MutableLiveData<Integer> responseCode = new MutableLiveData<>();

        final Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("createdDate", createdDate);
        userInfo.put("isVerified", isVerified);


        Query customerDB = firebaseDatabase.getReference().child("UserList").limitToLast(1);
        customerDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int id = 1;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        id = Integer.parseInt(data.getKey());
                    }
                    id = id + 1;

                    DatabaseReference userDB = firebaseDatabase.getReference().child("UserList").child(String.valueOf(id));
                    final int finalId = id;
                    userDB.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                responseCode.postValue(200);
                                editor.putString(SharedPref.USER_ID, String.valueOf(finalId));
                                editor.apply();

                                String token_id = FirebaseInstanceId.getInstance().getToken();
                                updateToken(token_id);
                            }
                        }
                    });

                } else {

                    DatabaseReference userDB = firebaseDatabase.getReference().child("UserList").child("1");
                    userDB.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                responseCode.postValue(200);
                                editor.putString(SharedPref.USER_ID, "1");
                                editor.apply();

                                String token_id = FirebaseInstanceId.getInstance().getToken();
                                updateToken(token_id);
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return responseCode;
    }
    public MutableLiveData<String> updateToken(String tokenId) {
        final MutableLiveData<String> liveData = new MutableLiveData<>();
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("token_id", tokenId);
        userMap.put("name", "User");

        String id = sharedPreferences.getString(SharedPref.USER_ID, "");

        if (!id.equals("")) {
            mFireStore.collection("users").document(id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        liveData.postValue("201");
                    } else {
                        liveData.postValue("404");
                    }
                }
            });
        }

        return liveData;
    }


}
