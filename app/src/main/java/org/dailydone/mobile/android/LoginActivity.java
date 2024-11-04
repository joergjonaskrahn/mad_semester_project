package org.dailydone.mobile.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import org.dailydone.mobile.android.databinding.ActivityLoginBinding;
import org.dailydone.mobile.android.view_model.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        findViewById(R.id.buttonLogin).setOnClickListener((View view) -> {
            viewModel.authenticateUser();
        });

        EditText editTextEmailAddress = findViewById(R.id.editTextEmailAddress);

        editTextEmailAddress.setOnFocusChangeListener((view, b) -> {
            viewModel.validateEmail();
        });

        editTextEmailAddress.setOnKeyListener((view, i, keyEvent) -> {
            if(viewModel.getIsMailError().getValue()) {
                viewModel.setIsMailError(false);
            }
            return false;
        });

        viewModel.getIsMailError().observe(this, error -> {
            if(viewModel.getIsMailError().getValue()) {
                ((EditText) findViewById(R.id.editTextEmailAddress)).setError("");
                findViewById(R.id.textViewMailError).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.textViewMailError).setVisibility(View.INVISIBLE);
            }
        });
    }
}