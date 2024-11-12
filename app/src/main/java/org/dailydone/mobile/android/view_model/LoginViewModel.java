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

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mailAddress = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isMailError = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPasswordError = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoginError = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> isAuthenticating = new MutableLiveData<>(false);


    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void validateEmail() {
        String email = mailAddress.getValue();
        if (email != null && !email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isMailError.setValue(true);
        }
    }

    public void validatePassword() {
        String passwordString = password.getValue();
        if(passwordString != null && !passwordString.isEmpty() && passwordString.length() != 6) {
            isPasswordError.setValue(true);
        }
    }

    public void loginIfFormCorrect(Callback authenticationCallback) {
        boolean isMailErrorVal = Objects.requireNonNullElse(isMailError.getValue(), false);
        boolean isPasswordErrorVal = Objects.requireNonNullElse(isPasswordError.getValue(), false);
        String mailInput = mailAddress.getValue();
        boolean isMailEntered = mailInput != null && !mailInput.isEmpty();
        String passwordInput = password.getValue();
        boolean isPasswordEntered = passwordInput != null && !passwordInput.isEmpty();

        if(!isMailErrorVal && !isPasswordErrorVal && isMailEntered && isPasswordEntered) {
            isAuthenticating.setValue(true);
            authenticateUser(authenticationCallback);
        }
    }

    private void authenticateUser(Callback authenticationCallback) {
        User user = new User(mailAddress.getValue(), password.getValue());
        Call<Boolean> call = ((DailyDoneApplication) getApplication()).getAuthRestService().authenticateUser(user);

        call.enqueue(authenticationCallback);
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

    public void setIsMailError(boolean isMailError) {
        // postValue is used as the setter methods may be called from asynchronous threads
        this.isMailError.postValue(isMailError);
    }

    public MutableLiveData<Boolean> getIsPasswordError() {
        return isPasswordError;
    }

    public void setIsPasswordError(boolean isPasswordError) {
        this.getIsPasswordError().postValue(isPasswordError);
    }

    public MutableLiveData<Boolean> getIsAuthenticating() {
        return isAuthenticating;
    }

    public void setIsAuthenticating(boolean isAuthenticating) {
        this.isAuthenticating.postValue(isAuthenticating);
    }

    public MutableLiveData<Boolean> getIsLoginError() {
        return isLoginError;
    }

    public void setIsLoginError(boolean isLoginError) {
        this.isLoginError.postValue(isLoginError);
    }
}
