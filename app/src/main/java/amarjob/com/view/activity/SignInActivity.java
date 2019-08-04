package amarjob.com.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.Locale;

import amarjob.com.R;
import amarjob.com.databinding.ActivitySignInBinding;
import amarjob.com.model.User;
import amarjob.com.otherClasses.SharedPref;
import amarjob.com.viewmodel.SearchJobViewModel;

public class SignInActivity extends AppCompatActivity {
    private static final int APP_REQUEST_CODE = 99;

    private ActivitySignInBinding binding;
    private String phoneNumberString;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SearchJobViewModel searchJobViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in);

        init();

        accountKitForLogIn();



    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        searchJobViewModel = ViewModelProviders.of(this).get(SearchJobViewModel.class);
    }

    private void accountKitForLogIn() {

        Intent intent = new Intent(SignInActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            binding.progressBar.setVisibility(View.VISIBLE);




            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {


                binding.progressBar.setVisibility(View.GONE);


                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
                binding.progressBar.setVisibility(View.GONE);


            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    getCurrentAccountInfo();
                } else {

                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));


                }
            }

        }

    }


    private void getCurrentAccountInfo() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                phoneNumberString = account.getPhoneNumber().toString();
                checkAccountInfo(phoneNumberString);
            }

            @Override
            public void onError(AccountKitError accountKitError) {

            }

        });
    }

    private void checkAccountInfo(final String phoneNumberString) {

        searchJobViewModel.checkUser(phoneNumberString).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.getName() == null || user.getName().isEmpty()) {
                    editor.putBoolean(SharedPref.IS_UPDATED, false);
                    editor.putString(SharedPref.MOBILE_NUMBER, phoneNumberString);
                    editor.apply();
                    searchJobViewModel.saveUserStatus(System.currentTimeMillis(), false).observe(SignInActivity.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(@Nullable Integer responseCode) {
                            if (responseCode == 200) {
                                startActivity(new Intent(SignInActivity.this, UpdateProfileActivity.class).putExtra("isFirstTime",true).
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }
                    });

                } else {
                    editor.putString(SharedPref.USER_ID, user.getId());
                    editor.putBoolean(SharedPref.IS_UPDATED, true);
                    editor.apply();

                    String token_id = FirebaseInstanceId.getInstance().getToken();
                    searchJobViewModel.updateToken(token_id);

                   /* startActivity(new Intent(LoginActivity.this, HomeActivity.class).
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
                }
            }
        });



    }


   /* private void searchAgentID(String mobileNumber) {
        editor.putString(StaticKeys.MOBILE_NUMBER, mobileNumber);
        editor.apply();

        viewModel.searchAgentID(mobileNumber).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String agentId) {
                if (agentId.equals("")) {
                    viewModel.getNewAgentID().observe(LoginActivity.this, new Observer<String>() {
                        @Override
                        public void onChanged(@Nullable String newAgentId) {
                            goToMyLoggedInActivity(newAgentId);
                        }
                    });
                } else {
                    goToMyLoggedInActivity(agentId);
                }
            }
        });

    }


    private void goToMyLoggedInActivity(final String agentId) {
        editor.putString(SharedPref.USER_ID, agentId);
        editor.apply();

        String token_id = FirebaseInstanceId.getInstance().getToken();
        viewModel.updateToken(token_id);

        viewModel.getAgentInfoSingleValueEvent().observe(this, new Observer<Agent>() {
            @Override
            public void onChanged(@Nullable Agent agent) {
                if (agent.getName() == null || agent.getName().isEmpty()) {
                    editor.putBoolean(SharedPref.IS_UPDATED, false);
                    editor.apply();
                    viewModel.saveAgentStatus(agentId, System.currentTimeMillis(), false).observe(LoginActivity.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(@Nullable Integer responseCode) {
                            if (responseCode == 200) {
                                startActivity(new Intent(LoginActivity.this, UpdateAgentProfileActivity.class).putExtra("isFirstTime",true)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }
                    });

                } else {
                    editor.putBoolean(SharedPref.IS_UPDATED, true);
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class).
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }*/
}
