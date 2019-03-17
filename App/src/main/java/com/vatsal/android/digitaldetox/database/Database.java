package com.vatsal.android.digitaldetox.database;

import android.arch.persistence.room.RoomDatabase;

import com.vatsal.android.digitaldetox.models.DisplayEventEntity;


@android.arch.persistence.room.Database(entities = DisplayEventEntity.class, version = 1)
public abstract class Database extends RoomDatabase{
    public abstract Dao dao();
}
