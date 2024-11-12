package org.dailydone.mobile.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.dailydone.mobile.android.databinding.ActivityLoginBinding;
import org.dailydone.mobile.android.view_model.LoginViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private TextView textViewMailError;
    private TextView textViewPasswordError;
    private TextView textViewLoginError;
    private FrameLayout loginProgressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind view model to view
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Get views
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextNumberPassword);
        textViewMailError = findViewById(R.id.textViewMailError);
        textViewPasswordError = findViewById(R.id.textViewPasswordError);
        textViewLoginError = findViewById(R.id.textViewLoginError);
        loginProgressOverlay = findViewById(R.id.loginProgressOverlay);

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

                if(response.isSuccessful()) {
                    Boolean loginSuccessful = response.body();
                    System.out.println(response.body());
                    if(Boolean.TRUE.equals(loginSuccessful)) {
                        Intent moveToListView = new Intent(LoginActivity.this, TodoOverviewActivity.class);
                        moveToListView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(moveToListView);
                    }else{
                        viewModel.setIsLoginError(true);
                    }
                }else{
                    viewModel.setIsLoginError(true);
                }
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                viewModel.setIsAuthenticating(false);
                viewModel.setIsLoginError(true);
            }
        };

        // Logic regarding mail address input
        // Focus change listener for mail address to react to completed input
        editTextEmailAddress.setOnFocusChangeListener((view, b) -> {
            viewModel.validateEmail();
        });

        // React to closing key board for error
        editTextPassword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.validateEmail();
            }
            return false;
        });

        // Set mail error (in-) visible based on error value
        viewModel.getIsMailError().observe(this, error -> {
            setInputErrorVisibility(viewModel.getIsMailError(), editTextEmailAddress,
                    textViewMailError);
        });

        // Reset mail input error on key input
        editTextEmailAddress.setOnKeyListener((view, i, keyEvent) -> {
            if (Boolean.TRUE.equals(viewModel.getIsMailError().getValue())) {
                viewModel.setIsMailError(false);
            }
            if(Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                viewModel.setIsLoginError(false);
            }
            return false;
        });

        // Logic regarding password input
        // Focus change listener for password to react to completed input
        editTextPassword.setOnFocusChangeListener((view, b) -> {
            viewModel.validatePassword();
        });

        // React to closing key board for error
        editTextPassword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.validatePassword();
            }
            return false;
        });

        // Set password error (in-) visible based on error value
        viewModel.getIsPasswordError().observe(this, error -> {
            setInputErrorVisibility(viewModel.getIsPasswordError(), editTextPassword,
                    textViewPasswordError);
        });

        // Reset password input error on key input
        editTextPassword.setOnKeyListener((view, i, keyEvent) -> {
            if (Boolean.TRUE.equals(viewModel.getIsPasswordError().getValue())) {
                viewModel.setIsPasswordError(false);
            }
            if(Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                viewModel.setIsLoginError(false);
            }
            return false;
        });

        // Click listener for login button
        findViewById(R.id.buttonLogin).setOnClickListener((View view) -> {
            viewModel.loginIfFormCorrect(authenticationCallback);
        });

        viewModel.getIsAuthenticating().observe(this, error -> {
            if(Boolean.TRUE.equals(viewModel.getIsAuthenticating().getValue())) {
                loginProgressOverlay.setVisibility(View.VISIBLE);
            }else{
                loginProgressOverlay.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoginError().observe(this, error -> {
            System.out.println("A");
            if(Boolean.TRUE.equals(viewModel.getIsLoginError().getValue())) {
                textViewLoginError.setVisibility(View.VISIBLE);
                System.out.println("B");
            }else{
                textViewLoginError.setVisibility(View.INVISIBLE);
                System.out.println("C");
            }
        });
    }

    private void setInputErrorVisibility(MutableLiveData<Boolean> errorObject, EditText input,
                                         TextView errorLabel) {
        if (Boolean.TRUE.equals(errorObject.getValue())) {
            // Show red error symbol but display the error text below the input field
            input.setError("");
            errorLabel.setVisibility(View.VISIBLE);
        } else {
            errorLabel.setVisibility(View.INVISIBLE);
        }
    }
}