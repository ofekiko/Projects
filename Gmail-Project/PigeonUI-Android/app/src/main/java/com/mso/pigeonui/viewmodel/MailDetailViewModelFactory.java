package com.mso.pigeonui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// Factory for creating instances of MailDetailViewModel
public class MailDetailViewModelFactory implements ViewModelProvider.Factory {
    // Holds the Application instance passed to the constructor.
    private final Application application;

    // Constructor for MailDetailViewModelFactory
    public MailDetailViewModelFactory(@NonNull Application application) {
        this.application = application;
    }

    // Creates a new instance of the given class
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MailDetailViewModel.class)) {
            return (T) new MailDetailViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}