package com.mso.pigeonui.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.mso.pigeonui.data.local.dao.LabelDao;
import com.mso.pigeonui.data.local.dao.MailDao;
import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.network.ApiClient;
import com.mso.pigeonui.network.ApiService;
import com.mso.pigeonui.repository.MailRepository;
import com.mso.pigeonui.data.local.database.MailDatabase;


import java.util.Collections;
import java.util.List;

// ViewModel for managing inbox-related data and operations
public class InboxViewModel extends AndroidViewModel {
    private static final String TAG = "InboxViewModel";
    private final MailRepository mailRepository;
    // MediatorLiveData to hold the list of mails currently displayed to the user
    private final MediatorLiveData<List<MailEntity>> _displayedMails = new MediatorLiveData<>();
    // Stores the currently active LiveData source that _displayedMails is observing
    private LiveData<List<MailEntity>> currentActiveSource;
    // LiveData to indicate loading state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    // LiveData to hold error messages for the UI.
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    // LiveData to indicate if an individual mail item is being updated
    private final MutableLiveData<Boolean> isUpdating = new MutableLiveData<>(false);
    private LiveData<List<MailEntity>> currentSourceFromRepo;
    // Name of the currently selected mailbox ("Inbox", "Sent", or a custom label name)
    private final MediatorLiveData<List<MailEntity>> _currentBoxMails = new MediatorLiveData<>();
    private String currentBoxName = "Inbox";
    private final MutableLiveData<String> _currentSearchQuery = new MutableLiveData<>();
    public LiveData<String> getCurrentSearchQuery() {
        return _currentSearchQuery;
    }
    private boolean isSearchActive = false;
    // Constructor for InboxViewModel
    public InboxViewModel(@NonNull Application application) {
        super(application);
        this.mailRepository = MailRepositoryProvider.getInstance(application);
        setSourceToBoxView(currentBoxName);
    }
    // Getters for LiveData
    public LiveData<List<MailEntity>> getDisplayedMails() {
        return _displayedMails;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Method to switch the data source for displayedMails
    private void switchDataSource(LiveData<List<MailEntity>> newSource) {
        if (currentActiveSource != null) {
            _displayedMails.removeSource(currentActiveSource);
        }
        currentActiveSource = newSource;
        _displayedMails.addSource(currentActiveSource, mails -> {
            _displayedMails.setValue(mails);
        });
    }
    // Sets the data source to display mails for a specific mailbox
    private void setSourceToBoxView(String boxName) {
        this.currentBoxName = boxName;
        this.isSearchActive = false;
        _currentSearchQuery.setValue(null);

        LiveData<List<MailEntity>> boxMailSource = mailRepository.getObservableMailsForBox(boxName);
        switchDataSource(boxMailSource);
    }

    // Sets the data source to display search results within the current mailbox
    private void setSourceToSearchView(String query) {
        this.isSearchActive = true;
        LiveData<List<MailEntity>> searchResultSource = mailRepository.searchObservableMailsInBox(currentBoxName, query);
        switchDataSource(searchResultSource);
    }

    // Sets the search query
    public void setSearchQuery(@Nullable String query) {
        final String trimmedQuery = query != null ? query.trim() : null;
        // Avoid redundant operations if the query hasn't changed
        if (TextUtils.equals(_currentSearchQuery.getValue(), trimmedQuery)) {
            return;
        }
        _currentSearchQuery.setValue(trimmedQuery);

        if (TextUtils.isEmpty(trimmedQuery)) {
            // If the new query is empty, and a search was active, revert to box view
            if (isSearchActive) {
                setSourceToBoxView(currentBoxName);
            }
        } else {
            // If there's a new query, switch to search view
            setSourceToSearchView(trimmedQuery);
            refreshCurrentDataFromServer();
        }
    }
    // Loads mails for a specific mailbox
    public void loadMailsForBox(String boxName, boolean forceRefresh) {
        boolean needsSourceChange = !this.currentBoxName.equals(boxName) || isSearchActive;

        // Determine if the source needs to change
        if (needsSourceChange) {
            setSourceToBoxView(boxName);
        }
        // Check if data needs to be fetched from the server
        List<MailEntity> currentData = _displayedMails.getValue();
        if (forceRefresh || currentData == null || currentData.isEmpty()) {
            refreshCurrentDataFromServer();
        }
    }
    // Refreshes the current data
    public void refreshCurrentDataFromServer() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        if (isSearchActive && !TextUtils.isEmpty(_currentSearchQuery.getValue())) {
            String query = _currentSearchQuery.getValue();
            mailRepository.refreshSearchFromServer(query, currentBoxName,
                    () -> {
                        isLoading.postValue(false);
                    },
                    error -> { // onError
                        errorMessage.setValue("Search failed: " + error);
                        isLoading.postValue(false);
                    }
            );
        } else {
            mailRepository.refreshMailsForBox(currentBoxName,
                    () -> {
                        isLoading.postValue(false);
                    },
                    error -> {
                        errorMessage.setValue("Failed to refresh " + currentBoxName + ": " + error);
                        isLoading.postValue(false);
                    }
            );
        }
    }

    // Clears the displayed mails.
     public void clearMails() {
        if (currentSourceFromRepo != null) {
            _currentBoxMails.removeSource(currentSourceFromRepo);
            currentSourceFromRepo = null;
        }
        _currentBoxMails.setValue(Collections.emptyList());
    }
    public String getCurrentBoxName() {
        return currentBoxName;
    }
    // Clears all locally cached mails from the database via the repository
    public void clearAllMailsFromDatabase() {
        mailRepository.clearLocalMails();
    }
    // Marks a specific email as read or unread
    public void markEmailAsRead(String serverMailId, boolean isRead) {
        isUpdating.setValue(true);
        errorMessage.setValue(null);
        mailRepository.updateMailReadStatus(serverMailId, isRead,
                updatedEntity -> {
                    isUpdating.setValue(false);
                },
                error -> {
                    errorMessage.setValue("Failed to update read status: " + error);
                    isUpdating.setValue(false);
                }
        );
    }
    // Clears the current error message
    public void clearErrorMessage() {
        errorMessage.setValue(null); // Or errorMessage.postValue(null) if from background thread
    }
}
// Provider class for obtaining a singleton instance of MailRepository
class MailRepositoryProvider {
    private static volatile MailRepository INSTANCE;
    // Returns the singleton instance of MailRepository
    public static MailRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (MailRepositoryProvider.class) {
                if (INSTANCE == null) {
                    ApiService apiService = ApiClient.getApiService(application.getApplicationContext());
                    MailDatabase database = MailDatabase.getInstance(application.getApplicationContext());
                    MailDao mailDao = database.mailDao();
                    LabelDao labelDao = database.labelDao();

                    INSTANCE = new MailRepository(apiService, mailDao, labelDao);
                }
            }
        }
        return INSTANCE;
    }
}