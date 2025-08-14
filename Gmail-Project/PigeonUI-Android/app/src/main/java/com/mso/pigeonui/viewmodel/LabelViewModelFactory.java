package com.mso.pigeonui.viewmodel;

// Import Libraries
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// Import Classes
import com.mso.pigeonui.repository.LabelRepository;

// Factory for creating LabelViewModel instances with a LabelRepository dependency
public class LabelViewModelFactory implements ViewModelProvider.Factory{

    // Field of the class: the repository used to create the LabelViewModel
    private final LabelRepository labelRepository;

    // The constructor
    public LabelViewModelFactory(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    // Function that receives a ViewModel class and returns an instance of it with the repository
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LabelViewModel.class)) {
            return (T) new LabelViewModel(labelRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
