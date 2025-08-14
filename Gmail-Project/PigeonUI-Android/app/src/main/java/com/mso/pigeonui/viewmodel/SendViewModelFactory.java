package com.mso.pigeonui.viewmodel;

// Import Libraries
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// Import Classes
import com.mso.pigeonui.repository.MailRepository;

// The class responsible for creating an instance of SendViewModel with a MailRepository
public class SendViewModelFactory implements ViewModelProvider.Factory {

    // Field of the class: the repository used to create the SendViewModel
    private final MailRepository mailRepository;

    // The constructor
    public SendViewModelFactory(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
    }

    // Function that receives a ViewModel class and returns an instance of it with the repository
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SendViewModel.class)) {
            return (T) new SendViewModel(mailRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}