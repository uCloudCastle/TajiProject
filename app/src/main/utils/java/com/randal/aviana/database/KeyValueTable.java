package com.randal.aviana.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Usage:
 * To open a table object:
 * KeyValueTable table = SQLiteUtils.createOrOpenKeyValueTable(context, "test.db", "test_table");
 *
 * Add a listener:
 * table.addKeyValueTableListener(new KeyValueTable.KeyValueTableListener(){...}
 */

public class KeyValueTable {
    private DatabaseHelper mHelper;
    private Builder mBuilder;

    /**
     * KeyValueTable 对象代表一个 {key,value} 类型的表对象
     * 数据类型为Text, 主键值不可重复
     */
    KeyValueTable(Context context, String dbName, String tableName) {
        mBuilder = new Builder();
        mBuilder.setDatabaseName(dbName);
        mBuilder.setTableName(tableName);
        mHelper = new DatabaseHelper(context, mBuilder);
    }

    /**
     * 插入一行数据, 如果 key 值已存在则更新 value 值
     * 返回数据 id 值
     */
    public long put(String key, String value) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (key == null || value == null) {
            return -1;
        }

        List<String> keyList = getKeyList();
        long line;
        if (keyList.contains(key)) {
            ContentValues cv = new ContentValues();
            cv.put(mBuilder._KEY, key);
            cv.put(mBuilder._VALUE, value);
            line = db.update(mBuilder.getTableName(), cv, mBuilder._KEY + " = ?", new String[]{ key });
            notifyOnValueUpdated(key, value);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(mBuilder._KEY, key);
            cv.put(mBuilder._VALUE, value);
            line = db.insert(mBuilder.getTableName(), null, cv);
            notifyOnValueAdded(key, value);
        }
        return line;
    }

    /**
     * 删除 key 值对应的数据
     * 返回 true 表示删除成功
     * 未找到 key 对应的数据返回 false
     */
    public boolean remove(String key) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int line =  db.delete(mBuilder.getTableName(), mBuilder._KEY + " = ?", new String[]{ key });
        if (line != 0) {
            notifyOnValueRemoved(key);
            return true;
        }
        return false;
    }

    /**
     * 查询 key 值对应的数据
     * 查询不到对应数据返回空字符串
     */
    public String get(String key) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(mBuilder.getTableName(), null,
                mBuilder._KEY + " = ?", new String[]{ key }, null, null, null);

        String retVal = "";
        if (cursor != null && cursor.moveToFirst()) {
            retVal = cursor.getString(cursor.getColumnIndex(mBuilder._VALUE));
            cursor.close();
        }
        return retVal;
    }

    /**
     * 获取所有的Key值
     * 查询不到数据返回空List
     */
    public List<String> getKeyList() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(mBuilder.getTableName(), null,
                null, null, null, null, null);

        List<String> retList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String key = cursor.getString(cursor.getColumnIndex(mBuilder._KEY));
                retList.add(key);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return retList;
    }

    /**
     * 获取所有的键值对
     * 查询不到数据返回空List
     */
    public List<Pair<String, String>> getPairList() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(mBuilder.getTableName(), null,
                null, null, null, null, null);

        List<Pair<String, String>> retList = new ArrayList<>();
        Pair<String, String> pair;
        if (cursor != null && cursor.moveToFirst()) {
             do {
                String key = cursor.getString(cursor.getColumnIndex(mBuilder._KEY));
                String value = cursor.getString(cursor.getColumnIndex(mBuilder._VALUE));
                pair = new Pair<>(key, value);
                retList.add(pair);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return retList;
    }

    /**
     * 获取所有的键值对的String值
     * 用来打印Log
     */
    public String getPairList2String() {
        String str = "{ ";
        for (Pair<String, String> pair : getPairList()) {
            str += "[" + pair.first + " " + pair.second + "] ";
        }
        str += "}";
        return str;
    }

    /**
     * 重新初始化数据库
     */
    public void recoverDatabase() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", mBuilder.getTableName()));
        mHelper.onCreate(db);
    }

    /**
     * 返回表格数据行数
     */
    public long getSize() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, mBuilder.getTableName());
    }

    /*
     * Actual Operation Class
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, Builder builder) {
            super(context, builder.mDbName, null, builder.mDbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + mBuilder.getTableName() + " (" +
                    mBuilder._ID + " integer primary key autoincrement, ";

            sql += (mBuilder._KEY + " text, ");
            sql += (mBuilder._VALUE + " text) ;");
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + mBuilder.getTableName());
            onCreate(db);
        }
    }

    private class Builder {
        void setDatabaseName(String name) {
            mDbName = name;
        }
        void setDatabaseVersion(int version) {
            mDbVersion = version;
        }
        void setTableName(String name) {
            mTableName = name;
        }
        String getDatabaseName() {
            return mDbName;
        }
        int getDatabaseVersion() {
            return mDbVersion;
        }
        String getTableName() {
            return mTableName;
        }

        // for database
        String mDbName = "aviana.db";
        int mDbVersion = 1;

        // for table
        String mTableName = "table";
        final String _ID = "_id";
        final String _KEY = "_key";
        final String _VALUE = "_value";
    }

    private List<KeyValueTableListener> listeners = new ArrayList<>();
    public interface KeyValueTableListener {
        void onValueAdded(String key, String value);
        void onValueUpdated(String key, String value);
        void onValueRemoved(String key);
    }
    public boolean addKeyValueTableListener(KeyValueTableListener l) {
        return (l != null) && listeners.add(l);
    }
    public boolean removeKeyValueTableListener(KeyValueTableListener l) {
        return (l != null) && listeners.remove(l);
    }
    private void notifyOnValueAdded(String key, String value) {
        for (KeyValueTableListener l : listeners) l.onValueAdded(key, value);
    }
    private void notifyOnValueUpdated(String key, String value) {
        for (KeyValueTableListener l : listeners) l.onValueUpdated(key, value);
    }
    private void notifyOnValueRemoved(String key) {
        for (KeyValueTableListener l : listeners) l.onValueRemoved(key);
    }
}
