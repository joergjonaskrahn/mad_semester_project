package org.dailydone.mobile.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import org.dailydone.mobile.android.databinding.ActivityLoginBinding;
import org.dailydone.mobile.android.util.Constants;
import org.dailydone.mobile.android.view_model.LoginViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind view model to view
        ActivityLoginBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_login);
        // Only one instance of the View Model is created and stored in the ViewModel Store
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);

        // Since LiveData inside the ViewModel is Lifecycle aware the View Model has to be
        // bound to the Lifecycle of the corresponding activity.
        binding.setLifecycleOwner(this);

        Callback<Boolean> authenticationCallback = new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, Response<Boolean> response) {
                // Because of requirements: Delay of two seconds
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                viewModel.setIsAuthenticating(false);

                if (response.isSuccessful()) {
                    Boolean loginSuccessful = response.body();
                    // loginSuccessful may be null
                    if (Boolean.TRUE.equals(loginSuccessful)) {
                        Intent moveToListView = new Intent(
                                LoginActivity.this, TodoOverviewActivity.class);
                        moveToListView.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(moveToListView);
                        finish();
                    }else{
                        viewModel.setIsLoginError(true);
                    }
                }else{
                    viewModel.setIsLoginError(true);
                }
            }

            // The login error isn`t differentiated between incorrect credentials and a server
            // error since the Demo-Backend is very simple and a server error is not expected.
            @Override
            public void onFailure(Call call, Throwable throwable) {
                viewModel.setIsAuthenticating(false);
                viewModel.setIsLoginError(true);
            }
        };

        // Listener creation
        // Focus change listener to react to completed input

        // TODO Hint
        //  You could move most listener methods to methods inside the view model and do
        //  declarative action binding. I simply learned about action binding to late.
        //  This is possible for all listener related logic which is not activity specific
        //  (for example calling another activity) and which has nothing to do with UI
        //  manipulation as ViewModels should not know about the view explicitly.
        binding.editTextEmailAddress.setOnFocusChangeListener((view, b) -> {
            viewModel.validateEmail();
            viewModel.validateAuthenticationPossible();
        });

        binding.editTextNumberPassword.setOnFocusChangeListener((view, b) -> {
            viewModel.validatePassword();
            viewModel.validateAuthenticationPossible();
        });

        // React to closing key board for error
        binding.editTextEmailAddress.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.validateEmail();
                viewModel.validateAuthenticationPossible();
            }
            // Returning false allows for further processing of the event by Android`s listeners.
            // true would indicate that the event has been fully processed yet.
            return false;
        });

        binding.editTextNumberPassword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.validatePassword();
                viewModel.validateAuthenticationPossible();
            }
            return false;
        });

        // Reset input error and login error on key input
        // Cannot use Text Change Listener since it gets triggered when display orientation changes
        // and resets the error states. According to the requirements this is not correct.
        binding.editTextEmailAddress.setOnKeyListener((view, i, keyEvent) -> {
            if (Boolean.TRUE.equals(viewModel.getIsMailError().getValue())) {
                viewModel.setIsMailError(false);
            }
            if (Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                viewModel.setIsLoginError(false);
            }
            // This implementation has the consequence, that the user has to finish the input
            // by clicking outside of the input fields or by finishing the input using the
            // keyboard. It would be better to validate if the login is possible on every key
            // stroke to set the login button to clickable, because the user would not have to
            // explicitly end the input with this. However, the requirements state that the
            // validation of mail address and password should only be done when the input
            // is finished. Because of this the described implementation alternative is not
            // possible.
            if (Boolean.TRUE.equals(viewModel.getIsAuthenticationPossible().getValue())) {
                viewModel.setIsAuthenticationPossible(false);
            }
            return false;
        });

        binding.editTextNumberPassword.setOnKeyListener((view, i, keyEvent) -> {
            if (Boolean.TRUE.equals(viewModel.getIsPasswordError().getValue())) {
                viewModel.setIsPasswordError(false);
            }
            if (Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                viewModel.setIsLoginError(false);
            }
            if (Boolean.TRUE.equals(viewModel.getIsAuthenticationPossible().getValue())) {
                viewModel.setIsAuthenticationPossible(false);
            }
            return false;
        });

        // Click listener for login button
        binding.buttonLogin.setOnClickListener((View view) -> {
            viewModel.loginIfFormCorrect(authenticationCallback);
        });

        // Observers
        // Set mail error (in-) visible based on error value state
        // TODO hint
        //  Instead of setting the errors manually the ViewModel could provide a simple error
        //  string and the View could be declaratively bound to this string.
        viewModel.getIsMailError().observe(this, isMailError -> {
            if(Boolean.TRUE.equals(viewModel.getIsMailError().getValue())) {
                binding.editTextEmailAddress.setError(getString(R.string.e_mail_error));
            }
        });

        // Set password error (in-) visible based on error value state
        viewModel.getIsPasswordError().observe(this, isPasswordError -> {
            if(Boolean.TRUE.equals(viewModel.getIsPasswordError().getValue())) {
                binding.editTextNumberPassword.setError(getString(R.string.password_error));
            }
        });

        // Set login error (in-) visible based on login error state
        viewModel.getIsLoginError().observe(this, isLoginError -> {
            if (Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                binding.textViewLoginError.setVisibility(View.VISIBLE);
            } else {
                binding.textViewLoginError.setVisibility(View.INVISIBLE);
            }
        });

        // Visualize whether a login is possible at the moment
        // TODO hint
        //  As this listener manipulates the UI not the whole logic could be moved to the
        //  ViewModel. However, the ViewModel could provide an Alpha LiveData which could be
        //  observed by this activity. Eventually, this is what ViewModels are about.
        viewModel.getIsAuthenticationPossible().observe(this, isAuthenticationPossible -> {
            binding.buttonLogin.setEnabled(isAuthenticationPossible);
            if(isAuthenticationPossible) {
                binding.buttonLogin.setAlpha(Constants.BUTTON_ENABLED_ALPHA);
            }else{
                binding.buttonLogin.setAlpha(Constants.BUTTON_DISABLED_ALPHA);
            }
        });

        // Show progress overlay based on authenticating state
        viewModel.getIsAuthenticating().observe(this, isAuthenticating -> {
            if (Boolean.TRUE.equals(viewModel.getIsAuthenticating().getValue())) {
                binding.loginProgressOverlay.setVisibility(View.VISIBLE);
            } else {
                binding.loginProgressOverlay.setVisibility(View.GONE);
            }
        });
    }
}