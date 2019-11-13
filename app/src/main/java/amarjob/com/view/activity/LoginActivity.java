package amarjob.com.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.iid.FirebaseInstanceId;

import amarjob.com.R;
import amarjob.com.databinding.ActivityLoginBinding;
import amarjob.com.model.User;
import amarjob.com.otherClasses.SharedPref;
import amarjob.com.otherClasses.StaticKeys;
import amarjob.com.viewmodel.SearchJobViewModel;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private static final int APP_REQUEST_CODE = 99;
    private SearchJobViewModel searchJobViewModel;
    private String phoneNumberString;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        searchJobViewModel = ViewModelProviders.of(this).get(SearchJobViewModel.class);


        sharedPreferences = getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        binding.loginBtn.setEnabled(false);
        binding.loginBtn.setBackgroundResource(R.drawable.bg_enable_login);
        binding.termsAndConditionID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    binding.loginBtn.setEnabled(true);
                    binding.loginBtn.setTextColor(getResources().getColor(R.color.white));
                    binding.loginBtn.setBackgroundResource(R.drawable.button_background);
                } else {
                    binding.loginBtn.setEnabled(false);
                    binding.loginBtn.setTextColor(getResources().getColor(R.color.white));
                    binding.loginBtn.setBackgroundResource(R.drawable.bg_enable_login);
                }
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(LoginActivity.this,SignInActivity.class));*/
                gotoFacebookAccountKit();
            }
        });
    }

    public void gotoFacebookAccountKit() {
        Intent intent = new Intent(LoginActivity.this, AccountKitActivity.class);
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

            binding.logoIV.setVisibility(View.GONE);
            binding.loginBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.termsAndConditionID.setVisibility(View.GONE);
            binding.termAndConditionTV.setVisibility(View.GONE);


           /* binding.logoIV.setVisibility(View.GONE);
            binding.loginBtn.setVisibility(View.GONE);
            binding.agreeLayout.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);*/


            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {

                binding.logoIV.setVisibility(View.VISIBLE);
                binding.loginBtn.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.termsAndConditionID.setVisibility(View.VISIBLE);
                binding.termAndConditionTV.setVisibility(View.VISIBLE);

               /* binding.logoIV.setVisibility(View.VISIBLE);
                binding.loginBtn.setVisibility(View.VISIBLE);
                binding.agreeLayout.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);*/


                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login cancel";

                binding.logoIV.setVisibility(View.VISIBLE);
                binding.loginBtn.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.termsAndConditionID.setVisibility(View.VISIBLE);
                binding.termAndConditionTV.setVisibility(View.VISIBLE);

               /* binding.logoIV.setVisibility(View.VISIBLE);
                binding.loginBtn.setVisibility(View.VISIBLE);
                binding.agreeLayout.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);*/

            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Token access success" + loginResult.getAccessToken().getAccountId();
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
            public void onError(final AccountKitError error) {
                Toast.makeText(LoginActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
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
                    searchJobViewModel.saveUserStatus(System.currentTimeMillis(), false).observe(LoginActivity.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(@Nullable Integer responseCode) {
                            if (responseCode == 200) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("isFirstTime",true).
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

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class).
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });



    }


}

