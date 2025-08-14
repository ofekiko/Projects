package com.mso.pigeonui.viewmodel;

// Import Libraries
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

// Import Classes
import com.mso.pigeonui.model.LabelEntity;
import com.mso.pigeonui.model.LabelRequest;
import com.mso.pigeonui.repository.LabelRepository;

// The class responsible for connecting the UI to the repository
public class LabelViewModel extends ViewModel {

    // Fields of the class: a LabelRepository object and four LiveData variables
    private final LabelRepository labelRepository;
    private final MutableLiveData<Boolean> isLabelCreated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLabelDeleted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLabelEdited = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // The constructor
    public LabelViewModel(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    // Getters that expose the LiveData to check if there was an action on label and to observe errors
    public LiveData<Boolean> getIsLabelCreated() {
        return isLabelCreated;
    }
    public LiveData<Boolean> getIsLabelDeleted() {
        return isLabelDeleted;
    }
    public LiveData<Boolean> getIsLabelEdited() {
        return isLabelEdited;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<List<LabelEntity>> getAllLabels() {
        return labelRepository.getAllLabelsFromRoom();
    }

    // Function that uses the LabelRepository to create a label
    // If the operation succeeds, isLabelCreated becomes true and entity label saved to room data base
    // If it fails, the error message is posted to errorMessage
    public void createLabel(LabelRequest request) {
        labelRepository.createLabel(request,
                () -> {
                    labelRepository.syncLabelsFromServer(errorMessage::postValue);
                    isLabelCreated.postValue(true);
                },
                errorMessage::postValue
        );
    }

    // Function that uses the LabelRepository to delete a label
    // If the operation succeeds, isLabelDeleted becomes true and entity label removed from room data base
    // If it fails, the error message is posted to errorMessage
    public void deleteLabel(String id) {
        labelRepository.deleteLabel(id,
                () -> {
                    labelRepository.syncLabelsFromServer(errorMessage::postValue);
                    isLabelDeleted.postValue(true);
                },
                errorMessage::postValue
        );
    }

    // Function that uses the LabelRepository to edit a label
    // If the operation succeeds, isLabelEdited becomes true and entity label saved to room data base
    // If it fails, the error message is posted to errorMessage
    public void editLabel(String id, LabelRequest request) {
        labelRepository.editLabel(id, request,
                () -> {
                    labelRepository.syncLabelsFromServer(errorMessage::postValue);
                    isLabelEdited.postValue(true);
                },
                errorMessage::postValue
        );
    }

    public void syncLabels() {
        labelRepository.syncLabelsFromServer(errorMessage::postValue);
    }
}
