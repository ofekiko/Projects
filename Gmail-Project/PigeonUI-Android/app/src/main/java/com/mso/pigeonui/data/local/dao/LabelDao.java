package com.mso.pigeonui.data.local.dao;

// Import Libraries
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Import Classes
import com.mso.pigeonui.model.LabelEntity;



// DAO interface for accessing label data in the Room database
@Dao
public interface LabelDao {

    // Return the labels
    @Query("SELECT * FROM labels")
    LiveData<List<LabelEntity>> getLabels();

    // Insert a new label into the room database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLabel(LabelEntity label);

    // Delete a specific label from the room database
    @Query("DELETE FROM labels WHERE id = :mongoId")
    void deleteByMongoId(String mongoId);

    // Update a specific label
    @Update
    void updateLabel(LabelEntity label);

    // Delete all labels
    @Query("DELETE FROM labels")
    void clearAll();
}
