package com.mso.pigeonui.viewmodel;

// Import Libraries
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// Import Classes
import com.mso.pigeonui.model.UserResponse;
import com.mso.pigeonui.repository.UserRepository;

// The class responsible for connecting the UI to the repository
public class UserViewModel extends ViewModel {

    // Fields of the class: a UserRepository object and two LiveData variables
    private final UserRepository userRepository;
    private final MutableLiveData<UserResponse> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<UserResponse> otherUserLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    // Getters that expose the LiveData to check if there was an action on user and to observe errors
    public LiveData<UserResponse> getUser() {
        return userLiveData;
    }
    public LiveData<UserResponse> getOtherUser() { return otherUserLiveData;}
    public LiveData<String> getError() {
        return errorLiveData;
    }

    // The constructor
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Function that uses the userRepository to get my details
    // If the operation succeeds, the live data postValue get my details
    // If it fails, the error message is posted to errorLiveData
    public void loadUser() {
        userRepository.getMyUser(
                user -> userLiveData.postValue(user),
                error -> errorLiveData.postValue(error)
        );
    }

    // Function that uses the userRepository to get a user details
    // If the operation succeeds, the live data postValue get user details
    // If it fails, the error message is posted to errorLiveData
    public void loadUserByID(String userId) {
        userRepository.getUserDetailsById(userId,
                user -> otherUserLiveData.postValue(user),
                error -> errorLiveData.postValue(error)
        );
    }
}
