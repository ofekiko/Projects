package com.mso.pigeonui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mso.pigeonui.model.RegisterRequest;
import com.mso.pigeonui.model.RegisterResponse;
import com.mso.pigeonui.model.Result;
import com.mso.pigeonui.repository.AuthRepository;

public class RegisterViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<Result<RegisterResponse>> registerResult = new MutableLiveData<>();
    public LiveData<Result<RegisterResponse>> getRegisterResult() {
        return registerResult;
    }

    private final MutableLiveData<Boolean> isUsernameAvailable = new MutableLiveData<>();
    public LiveData<Boolean> getIsUsernameAvailable() {
        return isUsernameAvailable;
    }

    public void register(RegisterRequest request) {
        repository.registerUser(request, registerResult);
    }

    public void checkUsernameAvailability(String username) {
        repository.checkUsernameAvailability(username, available -> {
            isUsernameAvailable.postValue(available);
        });
    }
}
