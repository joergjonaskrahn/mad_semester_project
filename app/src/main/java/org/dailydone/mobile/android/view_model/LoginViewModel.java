package org.dailydone.mobile.android.view_model;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.R;
import org.dailydone.mobile.android.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<String> mailAddress = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();

    private MutableLiveData<Boolean> isMailError = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isPasswordError = new MutableLiveData<>(false);


    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void validateEmail() {
        System.out.println("!!! 1");
        String email = mailAddress.getValue();
        if (email != null && !email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isMailError.setValue(true);
            System.out.println("!!! 2");
        }
    }

    public void authenticateUser() {
        User user = new User(mailAddress.getValue(), password.getValue());
        Call<Boolean> call = ((DailyDoneApplication) getApplication()).getAuthRestService().authenticateUser(user);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    System.out.println(response.body());
                    System.out.println(mailAddress.getValue());
                    System.out.println(password.getValue());
                }else{
                    System.out.println("Error");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                System.out.println("Failure");
            }
        });
    }

    public MutableLiveData<String> getMailAddress() {
        return mailAddress;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public MutableLiveData<Boolean> getIsMailError() {
        return isMailError;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress.setValue(mailAddress);
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public void setIsMailError(Boolean isMailError) {
        this.isMailError.setValue(isMailError);
    }
}
