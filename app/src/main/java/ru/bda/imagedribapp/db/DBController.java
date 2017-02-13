package ru.bda.imagedribapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.bda.imagedribapp.entity.Shot;

public class DBController {

    private DBHelper helper;
    private Context context;
    private SQLiteDatabase mDb;
    private Cursor cursor;

    public DBController (Context context) {
        this.context = context;
        helper = new DBHelper(context);
    }


    public List<Shot> getShotList() {
        mDb = helper.getWritableDatabase();
        List<Shot> shots = new ArrayList<>();
        cursor = mDb.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int titleCOlIndex = cursor.getColumnIndex(DBHelper.TITLE);
            int descriptionCOlIndex = cursor.getColumnIndex(DBHelper.DESCRIPTION);
            int filePathCOlIndex = cursor.getColumnIndex(DBHelper.FILE_PATH);
            do {
                Shot shot = new Shot();
                shot.setTitle(cursor.getString(titleCOlIndex));
                shot.setDescription(cursor.getString(descriptionCOlIndex));
                shot.setImagePath(cursor.getString(filePathCOlIndex));
                shots.add(shot);
            } while(cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
        return shots;
    }

    public void insertShot (Shot shot) {
        List<Shot> shots = getShotList();
        mDb = helper.getWritableDatabase();
        cursor = mDb.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (!haveShotDB(shots, shot)) {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.TITLE, shot.getTitle());
            mContentValues.put(DBHelper.DESCRIPTION, shot.getDescription());
            mContentValues.put(DBHelper.FILE_PATH, shot.getImagePath());
            if (mDb != null) mDb.insert(DBHelper.TABLE_NAME, null, mContentValues);
        }
        cursor.close();
        mDb.close();
    }

    private boolean haveShotDB (List<Shot> shots, Shot shot) {
        for (Shot obj : shots) {
            if (obj.getImagePath().equals(shot.getImagePath())) {
                return true;
            }
        }
        return false;
    }

    public void deleteData() {
        mDb = helper.getWritableDatabase();
        mDb.delete(DBHelper.TABLE_NAME, null, null);
        mDb.close();
    }
}
