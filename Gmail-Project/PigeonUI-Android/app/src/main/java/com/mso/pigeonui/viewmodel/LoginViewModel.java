package com.mso.pigeonui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.mso.pigeonui.model.LoginRequest;
import com.mso.pigeonui.model.LoginResponse;
import com.mso.pigeonui.model.Result;
import com.mso.pigeonui.repository.AuthRepository;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Result<LoginResponse>> loginResult = new MutableLiveData<>();
    private final AuthRepository repository = new AuthRepository();

    public MutableLiveData<Result<LoginResponse>> getLoginResult() {
        return loginResult;
    }

    public void login(LoginRequest request) {
        repository.loginUser(request, loginResult);
    }
}
