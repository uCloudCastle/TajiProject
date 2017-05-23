package com.randal.aviana.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by randal on 2017/5/23.
 */

public class KeyValueTable {
    private DatabaseHelper mHelper;
    private Builder mBuilder;

    /**
     * KeyValueTable 对象代表一个 {key,value} 类型的表对象
     * 数据类型为Text
     */
    KeyValueTable(Context context, String dbName, String tableName) {
        mBuilder = new Builder();
        mBuilder.setDatabaseName(dbName);
        mBuilder.setTableName(tableName);
        mHelper = new DatabaseHelper(context, mBuilder);
    }

    /**
     * 插入一行数据
     * 返回数据 id 值
     * 插入失败返回 -1
     */
    public long insert(String key, String value) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (key == null || value == null) {
            return -1;
        }

        ContentValues cv = new ContentValues();
        cv.put(mBuilder._KEY, key);
        cv.put(mBuilder._VALUE, value);
        return db.insert(mBuilder.getTableName(), null, cv);
    }

    /**
     * 删除 key 值对应的数据
     * 返回被删除的行数
     * 未找到 key 对应的数据返回 0
     */
    public int delete(String key) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db.delete(mBuilder.getTableName(), mBuilder._KEY + " = ?", new String[]{ key });
    }

    /**
     * 更新 key 值对应的数据
     * 返回被更新的行数
     */
    public int update(String key, String value) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (key == null || value == null) {
            return -1;
        }

        ContentValues cv = new ContentValues();
        cv.put(mBuilder._KEY, key);
        cv.put(mBuilder._VALUE, value);
        return db.update(mBuilder.getTableName(), cv, mBuilder._KEY + " = ?", new String[]{ key });
    }

    /**
     * 查询 key 值对应的数据
     * 查询不到对应数据返回空字符串
     */
    public String query(String key) {
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
    public long queryNumEntries() {
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

        // x,x -> ('x','x')
        String loadColumnValue(String[] values) {
            String retVal = "(\'" + values[0];
            for(int i = 1; i < values.length; ++i) {
                retVal += ( "\',\'" + values[i] );
            }
            retVal += "\')";
            return retVal;
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
}
