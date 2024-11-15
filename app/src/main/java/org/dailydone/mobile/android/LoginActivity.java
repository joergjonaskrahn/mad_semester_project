package org.dailydone.mobile.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.dailydone.mobile.android.databinding.ActivityLoginBinding;
import org.dailydone.mobile.android.util.Constants;
import org.dailydone.mobile.android.view_model.LoginViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    private EditText editTextEmailAddress;
    private EditText editTextPassword;

    private TextView textViewLoginError;

    private Button buttonLogin;

    private FrameLayout loginProgressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind view model to view
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        // Since LiveData inside the ViewModel is Lifecycle aware the View Model has to be
        // bound to the Lifecycle of the corresponding activity.
        binding.setLifecycleOwner(this);

        // Get views

        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextNumberPassword);
        textViewLoginError = findViewById(R.id.textViewLoginError);
        buttonLogin = findViewById(R.id.buttonLogin);
        loginProgressOverlay = findViewById(R.id.loginProgressOverlay);

        // Callback gets executed asynchronously because of builder in application
        Callback<Boolean> authenticationCallback = new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, Response<Boolean> response) {
                // Delay of two seconds as stated in the requirements
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                viewModel.setIsAuthenticating(false);

                if (response.isSuccessful()) {
                    Boolean loginSuccessful = response.body();
                    if (Boolean.TRUE.equals(loginSuccessful)) {
                        Intent moveToListView = new Intent(LoginActivity.this, TodoOverviewActivity.class);
                        moveToListView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(moveToListView);
                    } else {
                        viewModel.setIsLoginError(true);
                    }
                } else {
                    viewModel.setIsLoginError(true);
                }
            }

            // The login error isn`t differentiated between incorrect credentials and a server
            // error since the demo Backend is very simple and a server error is not expected.
            @Override
            public void onFailure(Call call, Throwable throwable) {
                viewModel.setIsAuthenticating(false);
                viewModel.setIsLoginError(true);
            }
        };

        // Listener creation
        // Focus change listener for password to react to completed input
        editTextEmailAddress.setOnFocusChangeListener((view, b) -> {
            viewModel.validateEmail();
            viewModel.validateAuthenticationPossible();
        });

        editTextPassword.setOnFocusChangeListener((view, b) -> {
            viewModel.validatePassword();
            viewModel.validateAuthenticationPossible();
        });

        // React to closing key board for error
        editTextPassword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.validateEmail();
            }
            viewModel.validateAuthenticationPossible();
            // Allows for further processing of the event by Android`s listeners.
            // true would indicate that the event has been fully processed yet.
            return false;
        });

        editTextPassword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.validatePassword();
            }
            viewModel.validateAuthenticationPossible();
            return false;
        });

        // Reset input error and login error on key input
        editTextEmailAddress.setOnKeyListener((view, i, keyEvent) -> {
            if (Boolean.TRUE.equals(viewModel.getIsMailError().getValue())) {
                viewModel.setIsMailError(false);
            }
            if (Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                viewModel.setIsLoginError(false);
            }
            return false;
        });

        editTextPassword.setOnKeyListener((view, i, keyEvent) -> {
            if (Boolean.TRUE.equals(viewModel.getIsPasswordError().getValue())) {
                viewModel.setIsPasswordError(false);
            }
            if (Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                viewModel.setIsLoginError(false);
            }
            return false;
        });

        // Click listener for login button
        buttonLogin.setOnClickListener((View view) -> {
            viewModel.loginIfFormCorrect(authenticationCallback);
        });

        // Observers
        // Set mail error (in-) visible based on error value state
        viewModel.getIsMailError().observe(this, isMailError -> {
            if(Boolean.TRUE.equals(viewModel.getIsMailError().getValue())) {
                editTextEmailAddress.setError(getString(R.string.e_mail_error));
            }
        });

        // Set password error (in-) visible based on error value state
        viewModel.getIsPasswordError().observe(this, isPasswordError -> {
            if(Boolean.TRUE.equals(viewModel.getIsPasswordError().getValue())) {
                editTextPassword.setError(getString(R.string.password_error));
            }
        });

        // Set login error (in-) visible based on login error state
        viewModel.getIsLoginError().observe(this, isLoginError -> {
            if (Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                textViewLoginError.setVisibility(View.VISIBLE);
            } else {
                textViewLoginError.setVisibility(View.INVISIBLE);
            }
        });

        viewModel.getIsAuthenticationPossible().observe(this, isAuthenticationPossible -> {
            buttonLogin.setEnabled(isAuthenticationPossible);
            if(isAuthenticationPossible) {
                buttonLogin.setAlpha(Constants.BUTTON_ENABLED_ALPHA);
            }else{
                buttonLogin.setAlpha(Constants.BUTTON_DISABLED_ALPHA);
            }
        });

        // Show progress overlay based on authenticating state
        viewModel.getIsAuthenticating().observe(this, isAuthenticating -> {
            if (Boolean.TRUE.equals(viewModel.getIsAuthenticating().getValue())) {
                loginProgressOverlay.setVisibility(View.VISIBLE);
            } else {
                loginProgressOverlay.setVisibility(View.GONE);
            }
        });
    }
}