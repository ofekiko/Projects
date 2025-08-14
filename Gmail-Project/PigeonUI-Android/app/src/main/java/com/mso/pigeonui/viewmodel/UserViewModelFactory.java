package com.mso.pigeonui.viewmodel;

// Import Libraries
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// Import Classes
import com.mso.pigeonui.repository.UserRepository;

// Factory for creating UserViewModel instances with a UserRepository dependency
public class UserViewModelFactory implements ViewModelProvider.Factory {

    // Field of the class: the repository used to create the UserViewModel
    private final UserRepository userRepository;

    // The constructor
    public UserViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Function that receives a ViewModel class and returns an instance of it with the repository
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

