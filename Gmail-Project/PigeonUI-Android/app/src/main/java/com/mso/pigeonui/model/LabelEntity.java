package com.mso.pigeonui.model;

// Import Libraries
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Class that represent an item label in the room database.
@Entity(tableName = "labels")
public class LabelEntity {

    // Fields of label:
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    // Constructors:
    public LabelEntity() {}

    public LabelEntity(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters:
    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }

    // Setters:
    public void setId(@NonNull String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
