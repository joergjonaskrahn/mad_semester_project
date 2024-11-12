package org.dailydone.mobile.android.view_model;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.dailydone.mobile.android.DailyDoneApplication;
import org.dailydone.mobile.android.model.User;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

// AndroidViewModel allows access to the application context
public class LoginViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mailAddress = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isMailError = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPasswordError = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoginError = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> isAuthenticationPossible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isAuthenticating = new MutableLiveData<>(false);


    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    // Checks whether the E-Mail input matches the criteria
    public void validateEmail() {
        String email = mailAddress.getValue();
        isMailError.setValue(email != null && !email.isEmpty() &&
                !Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    // Checks whether the Password input matches the criteria
    public void validatePassword() {
        String passwordString = password.getValue();
        isPasswordError.setValue(passwordString != null && !passwordString.isEmpty()
                && passwordString.length() != 6);
    }

    // Checks whether criteria for authentication are met and sets the state
    public void validateAuthenticationPossible() {
        isAuthenticationPossible.setValue(areLoginCriteriaFulfilled());
    }

    // Executes an authentication attempt if the input matches the requirements and executes
    // the passed callback
    public void loginIfFormCorrect(Callback authenticationCallback) {
        if (areLoginCriteriaFulfilled()) {
            isAuthenticating.setValue(true);
            authenticateUser(authenticationCallback);
        }
    }

    // Returns whether login criteria are fulfilled
    private boolean areLoginCriteriaFulfilled() {
        boolean isMailErrorVal = Objects.requireNonNullElse(isMailError.getValue(), true);
        boolean isPasswordErrorVal = Objects.requireNonNullElse(isPasswordError.getValue(), true);
        String mailInput = mailAddress.getValue();
        boolean isMailEntered = mailInput != null && !mailInput.isEmpty();
        String passwordInput = password.getValue();
        boolean isPasswordEntered = passwordInput != null && !passwordInput.isEmpty();

        return !isMailErrorVal && !isPasswordErrorVal && isMailEntered && isPasswordEntered;
    }

    // Executes an authentication attempt calling the passed callback
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

    public MutableLiveData<Boolean> getIsLoginError() {
        return isLoginError;
    }

    public void setIsLoginError(boolean isLoginError) {
        this.isLoginError.postValue(isLoginError);
    }

    public MutableLiveData<Boolean> getIsAuthenticationPossible() {
        return this.isAuthenticationPossible;
    }

    public void setIsAuthenticationPossible(boolean isAuthenticationPossible) {
        this.isAuthenticationPossible.setValue(isAuthenticationPossible);
    }

    public MutableLiveData<Boolean> getIsAuthenticating() {
        return isAuthenticating;
    }

    public void setIsAuthenticating(boolean isAuthenticating) {
        this.isAuthenticating.postValue(isAuthenticating);
    }
}
