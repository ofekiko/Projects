package com.mso.pigeonui.viewmodel;

// Import Libraries
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// Import Classes
import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.model.MailRequest;
import com.mso.pigeonui.repository.MailRepository;

// The class responsible for connecting the UI to the repository
public class SendViewModel extends ViewModel {

    // Fields of the class: a MailRepository object and two LiveData variables
    private final MailRepository mailRepository;
    private final MutableLiveData<Boolean> isMailSent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDraftSaved = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // The constructor
    public SendViewModel(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
    }

    // Getters that expose the LiveData to check if the mail was sent and to observe errors
    public LiveData<Boolean> getIsMailSent() {
        return isMailSent;
    }

    public LiveData<Boolean> getIsDraftSaved() {
        return isDraftSaved;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Function that uses the MailRepository to send a mail
    // If the operation succeeds, isMailSent becomes true and entity mail saved to room data base
    // If it fails, the error message is posted to errorMessage
    public void sendMail(MailRequest request) {
        mailRepository.sendMail(request,
                () -> isMailSent.postValue(true),
                errorMessage::postValue
        );
    }

    // Function that uses the MailRepository to edit a draft mail
    // If the operation succeeds, isDraftSaved becomes true and entity mail saved to room data base
    // If it fails, the error message is posted to errorMessage
    public void editDraft(String id, MailRequest request) {
        mailRepository.editDraft(id, request,
                () -> isDraftSaved.postValue(true),
                errorMessage::postValue
        );
    }
}


