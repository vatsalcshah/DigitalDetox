package com.vatsal.android.digitaldetox.database;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.vatsal.android.digitaldetox.models.DisplayEventEntity;

import java.util.List;


@android.arch.persistence.room.Dao
public interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(List<DisplayEventEntity> event);

    @Query("SELECT * FROM events WHERE startTime >= :startTime AND endTime <= :endTime AND "
            + "endTime <> 0 ORDER BY startTime DESC")
    List<DisplayEventEntity> getEvents(long startTime, long endTime);
    @Delete
    void deleteEvent(DisplayEventEntity unstable);
}
