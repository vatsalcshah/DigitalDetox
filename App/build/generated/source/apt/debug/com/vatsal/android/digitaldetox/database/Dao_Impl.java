package com.vatsal.android.digitaldetox.database;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.database.Cursor;
import com.vatsal.android.digitaldetox.models.DisplayEventEntity;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class Dao_Impl implements Dao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfDisplayEventEntity;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfDisplayEventEntity;

  public Dao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDisplayEventEntity = new EntityInsertionAdapter<DisplayEventEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `events`(`appName`,`startTime`,`endTime`,`ongoing`,`packageName`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DisplayEventEntity value) {
        if (value.appName == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.appName);
        }
        stmt.bindLong(2, value.startTime);
        stmt.bindLong(3, value.endTime);
        stmt.bindLong(4, value.ongoing);
        if (value.packageName == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.packageName);
        }
      }
    };
    this.__deletionAdapterOfDisplayEventEntity = new EntityDeletionOrUpdateAdapter<DisplayEventEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `events` WHERE `startTime` = ? AND `appName` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DisplayEventEntity value) {
        stmt.bindLong(1, value.startTime);
        if (value.appName == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.appName);
        }
      }
    };
  }

  @Override
  public void insertEvent(List<DisplayEventEntity> event) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfDisplayEventEntity.insert(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteEvent(DisplayEventEntity unstable) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfDisplayEventEntity.handle(unstable);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<DisplayEventEntity> getEvents(long startTime, long endTime) {
    final String _sql = "SELECT * FROM events WHERE startTime >= ? AND endTime <= ? AND endTime <> 0 ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfAppName = _cursor.getColumnIndexOrThrow("appName");
      final int _cursorIndexOfStartTime = _cursor.getColumnIndexOrThrow("startTime");
      final int _cursorIndexOfEndTime = _cursor.getColumnIndexOrThrow("endTime");
      final int _cursorIndexOfOngoing = _cursor.getColumnIndexOrThrow("ongoing");
      final int _cursorIndexOfPackageName = _cursor.getColumnIndexOrThrow("packageName");
      final List<DisplayEventEntity> _result = new ArrayList<DisplayEventEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DisplayEventEntity _item;
        _item = new DisplayEventEntity();
        _item.appName = _cursor.getString(_cursorIndexOfAppName);
        _item.startTime = _cursor.getLong(_cursorIndexOfStartTime);
        _item.endTime = _cursor.getLong(_cursorIndexOfEndTime);
        _item.ongoing = _cursor.getInt(_cursorIndexOfOngoing);
        _item.packageName = _cursor.getString(_cursorIndexOfPackageName);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
