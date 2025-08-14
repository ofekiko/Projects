package com.mso.pigeonui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mso.pigeonui.model.LabelEntity;
import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.repository.MailRepository;
import com.mso.pigeonui.util.UrlUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MailDetailViewModel extends AndroidViewModel {
    private static final String TAG = "MailDetailViewModel";
    // Constants for standard mailbox names
    public static final String BOX_TRASH = "Trash";
    public static final String BOX_SPAM = "Spam";
    public static final String BOX_INBOX = "Inbox";
    public static final String BOX_SENT = "Sent";
    public static final String BOX_DRAFTS = "Drafts";

    private final MailRepository mailRepository;
    private final MutableLiveData<String> _mailServerId = new MutableLiveData<>();

    public final LiveData<MailEntity> currentMail;
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MediatorLiveData<List<String>> _combinedAvailableBoxes = new MediatorLiveData<>();
    public final LiveData<List<String>> combinedAvailableBoxes = _combinedAvailableBoxes;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    // Formatter for display
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault());

    // Constructor for MailDetailViewModel
    public MailDetailViewModel(@NonNull Application application) {
        super(application);
        this.mailRepository = MailRepositoryProvider.getInstance(application); // Get repository instance
        apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentMail = Transformations.switchMap(_mailServerId, serverId -> {
            if (serverId == null || serverId.isEmpty()) {
                // If ID is invalid, return a LiveData with a null value
                MutableLiveData<MailEntity> emptyResult = new MutableLiveData<>();
                emptyResult.setValue(null);
                return emptyResult;
            }
            return mailRepository.getObservableMailDetails(serverId);
        });
        // Define a list of standard system boxes
        final List<String> systemBoxes = Arrays.asList(BOX_INBOX, BOX_SPAM);
        // Observe all defined labels from the database
        LiveData<List<LabelEntity>> allDefinedDbLabels = mailRepository.getObservableAllDefinedLabels();

        // Combine system boxes and custom labels into _combinedAvailableBoxes
        _combinedAvailableBoxes.addSource(allDefinedDbLabels, dbLabelEntities -> {
            Set<String> combinedNames = new HashSet<>(systemBoxes);
            if (dbLabelEntities != null) {
                for (LabelEntity label : dbLabelEntities) {
                    // Add custom labels, ensuring "Trash" is not included as a movable target
                    if (!BOX_TRASH.equalsIgnoreCase(label.getName())) {
                        combinedNames.add(label.getName());
                    }
                }
            }
            List<String> sortedCombinedList = new ArrayList<>(combinedNames);
            Collections.sort(sortedCombinedList);
            _combinedAvailableBoxes.setValue(sortedCombinedList);
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    // Loads mail details by its server ID
    public void loadMailById(String serverMailId) {
        if (serverMailId == null || serverMailId.isEmpty()) {
            _errorMessage.setValue("Mail ID is invalid.");
            return;
        }
        // Check if we are already loading/observing this ID
        if (serverMailId.equals(_mailServerId.getValue())) {
            if (currentMail.getValue() == null) {
                refreshMailDetailsFromServer(serverMailId); // Attempt a refresh
            }
            return;
        }

        // Set the new mail ID. This will trigger the switchMap for currentMail
        _mailServerId.setValue(serverMailId);
        refreshMailDetailsFromServer(serverMailId);
    }

    // Fetches the latest details for a given mail ID from the server
    private void refreshMailDetailsFromServer(String serverMailId) {
        if (serverMailId == null || serverMailId.isEmpty()) return;
        _isLoading.setValue(true);
        _errorMessage.setValue(null);
        mailRepository.refreshMailDetails(serverMailId,
                new Consumer<MailEntity>() { // onSuccess
                    @Override
                    public void accept(MailEntity refreshedEntity) {
                        _isLoading.postValue(false);
                    }
                },
                new Consumer<String>() { // onError
                    @Override
                    public void accept(String error) {
                        _errorMessage.postValue(error);
                        _isLoading.postValue(false);
                    }
                }
        );
    }

    // Toggles the read status of the currently loaded mail.
    public void toggleReadStatusCurrentMail() {
        MailEntity mail = currentMail.getValue();
        if (mail != null && mail.getServerMailId() != null) {
            boolean newReadStatus = !mail.isRead();
            _isLoading.setValue(true);
            mailRepository.updateMailReadStatus(mail.getServerMailId(), newReadStatus,
                    updatedEntity -> {
                        _isLoading.postValue(false);
                    },
                    error -> {
                        Log.e(TAG, "Failed to update read status for " + mail.getServerMailId() + ": " + error);
                        _errorMessage.postValue("Failed to update read status: " + error);
                        _isLoading.postValue(false);
                    }
            );
        } else {
            _errorMessage.setValue("Cannot update status: Mail not loaded.");
        }
    }

    // Moves the currently loaded mail to a specified new box
    public void moveCurrentMailToBox(String newBox) {
        MailEntity mail = currentMail.getValue();
        if (mail == null || mail.getServerMailId() == null) {
            _errorMessage.setValue("Cannot move: Mail details not fully loaded.");
            return;
        }

        if (newBox == null || newBox.isEmpty()) {
            _errorMessage.setValue("Invalid target box specified.");
            return;
        }
        String currentBoxName = mail.getBox();
        if (newBox.equals(currentBoxName)) {
            _errorMessage.setValue("Mail is already in " + newBox + ".");
            return;
        }
        _isLoading.setValue(true);

        String oldBox = mail.getBox();
        boolean movingToSpam = BOX_SPAM.equals(newBox);
        boolean movingFromSpam = BOX_SPAM.equals(oldBox) && !BOX_SPAM.equals(newBox);

        List<String> urlsToProcess = new ArrayList<>();
        if (movingToSpam || movingFromSpam) {
            if (mail.getTitle() != null) {
                urlsToProcess.addAll(UrlUtils.extractUrls(mail.getTitle()));
            }
            if (mail.getContent() != null) { // Assuming getContent() gives the email body
                urlsToProcess.addAll(UrlUtils.extractUrls(mail.getContent()));
            }
        }
        mailRepository.moveMailToBox(mail.getServerMailId(), newBox,
                updatedEntity -> {
                    if (!urlsToProcess.isEmpty()) {
                        if (movingToSpam) {
                            processUrlsForBlacklist(urlsToProcess, true, newBox); // true for adding
                        } else if (movingFromSpam) {
                            processUrlsForBlacklist(urlsToProcess, false, newBox); // false for deleting
                        } else {
                            _isLoading.postValue(false); // No URL processing needed
                        }
                    } else {
                        _isLoading.postValue(false); // No URLs found to process
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to move mail " + mail.getServerMailId() + " to " + newBox + ": " + error);
                    _errorMessage.postValue("Failed to move mail: " + error);
                    _isLoading.postValue(false);
                }
        );
    }
    // Processes a list of URLs for adding to or removing from a blacklist
    private void processUrlsForBlacklist(List<String> urls, boolean addToBlacklist, String targetBoxName) {
        if (urls.isEmpty()) {
            _isLoading.postValue(false);
            return;
        }
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        int totalUrls = urls.size();

        final String actionVerb = addToBlacklist ? "add" : "remove";
        final String preposition = addToBlacklist ? "to" : "from";

        for (String url : urls) {
            MailRepository.UrlOperationCallback urlCallback = new MailRepository.UrlOperationCallback() {
                @Override
                public void onSuccess(String processedUrl) {
                    successCount.incrementAndGet();
                    _isLoading.postValue(false);
                }

                @Override
                public void onError(String failedUrl, String errorMessage) {
                    errorCount.incrementAndGet();
                    _isLoading.postValue(false);
                }
            };

            if (addToBlacklist) {
                mailRepository.addUrlToBlacklist(url, urlCallback);
            } else {
                mailRepository.deleteUrlFromBlacklist(url, urlCallback);
            }
        }
    }

    // Formats a date string into a more readable format
    public String getFormattedDate(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "N/A";
        }
        try {
            Date date = apiDateFormat.parse(isoDateString);
            if (date != null) {
                return displayDateFormat.format(date);
            }
        } catch (ParseException e) {
            return isoDateString;
        }
        return isoDateString;
    }

    // Clears the current error message
    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }
}
