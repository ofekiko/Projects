package com.mso.pigeonui.data.local.dao;

// Import Libraries
import android.icu.number.NumberRangeFormatter;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.io.FileDescriptor;
import java.util.List;

// Import Classes
import com.mso.pigeonui.model.MailEntity;

// DAO interface for accessing mail data in the Room database
@Dao
public interface MailDao {

    // Returns the latest 50 mails from the specified box, ordered by sent date descending
    @Query("SELECT * FROM emails WHERE box = :box ORDER BY sentAt DESC LIMIT 50 ")
    LiveData<List<MailEntity>> get50Mails(String box);

    // Returns a single mail by its serverMailId as LiveData
    @Query("SELECT * FROM emails WHERE serverMailId = :serverMailId LIMIT 1")
    LiveData<MailEntity> getMailEntityByServerId(String serverMailId);

    // Returns a single mail by its serverMailId (synchronously, without LiveData)
    @Query("SELECT * FROM emails WHERE serverMailId = :serverMailId LIMIT 1")
    MailEntity findByServerIdSync(String serverMailId);

    // Inserts a mail into the Room database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMail(MailEntity mail);

    // Inserts a list of mails into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMails(List<MailEntity> mails);

    // Delete a specific mail from the room database
    @Delete
    void deleteMail(MailEntity mail);

    // Update a specific mail
    @Update
    void updateMail(MailEntity mail);

    // Searches for all mails in the box that match the query, ordered by sent date descending
    @Query("SELECT * FROM emails WHERE box = :boxName AND " +
            "(title LIKE :query OR author LIKE :query OR content LIKE :query OR recipientsEmails LIKE :query) " +
            "ORDER BY sentAt DESC")
    LiveData<List<MailEntity>> searchMailsInBox(String boxName, String query);
    // Searches for all mails that match the query, ordered by sent date descending
    @Query("SELECT * FROM emails WHERE " +
            "(title LIKE :query OR author LIKE :query OR content LIKE :query OR recipientsEmails LIKE :query) " +
            "ORDER BY sentAt DESC")
    LiveData<List<MailEntity>> searchAllMails(String query);
    // Delete all mails from a box
    @Query("DELETE FROM emails WHERE box = :box")
    void clearBox(String box);

    // Delete all mails from the database
    @Query("DELETE FROM emails")
    void deleteAllMails();
}
