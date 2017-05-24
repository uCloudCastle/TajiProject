package com.randal.aviana.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randal on 2017/5/24.
 */

public class TextTable {
    private DatabaseHelper mHelper;
    private Builder mBuilder;

    /**
     * TextTable 对象代表一个皆为 Text 数据类型的表对象
     * columnValues = null 表示没有初始构造数据
     */
    TextTable(Context context, String dbName, String tableName,
                       String[] columnTitles,
                       List<String[]> columnValues) {
        mBuilder = new Builder();
        mBuilder.setDatabaseName(dbName);
        mBuilder.setTableName(tableName);
        mBuilder.setColumnTitles(columnTitles);
        mBuilder.setColumnValues(columnValues);
        mHelper = new DatabaseHelper(context, mBuilder);
    }

    /**
     * 返回表格数据行数
     */
    public long queryNumEntries() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, mBuilder.getTableName());
    }

    /**
     * 插入一行数据
     * 返回数据 id 值
     * 插入失败返回 -1
     */
    public long insert(String[] values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (values.length != mBuilder.getColumnTitles().length) {
            return -1;
        }

        ContentValues cv = new ContentValues();
        for (int i = 0; i < values.length; ++i) {
            cv.put(mBuilder.getColumnTitles()[i], values[i]);
        }
        return db.insert(mBuilder.getTableName(), null, cv);
    }

    /**
     * 删除 id 值对应的数据
     * 返回被删除的行数
     * 未找到 id 对应的数据返回 0
     */
    public int delete(String id) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db.delete(mBuilder.getTableName(), mBuilder._ID + " = ?", new String[]{ id });
    }

    /**
     * 更新 id 值对应的数据
     * 返回被更新的行数
     */
    public int update(String id, String[] values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (values.length != mBuilder.getColumnTitles().length) {
            return -1;
        }

        ContentValues cv = new ContentValues();
        for (int i = 0; i < values.length; ++i) {
            cv.put(mBuilder.getColumnTitles()[i], values[i]);
        }
        return db.update(mBuilder.getTableName(), cv, mBuilder._ID + " = ?", new String[]{ id });
    }

    /**
     * 查询 id 值对应的数据
     * 查询不到对应数据返回 null 数组
     */
    public String[] query(String id) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(mBuilder.getTableName(), null,
                mBuilder._ID + " = ?", new String[]{ id }, null, null, null);

        String[] retVals = new String[mBuilder.getColumnTitles().length];
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < mBuilder.getColumnTitles().length; ++i) {
                retVals[i] = cursor.getString(cursor.getColumnIndex(mBuilder.getColumnTitles()[i]));
            }
            cursor.close();
        }
        return retVals;
    }

    /**
     * 查询所有数据
     * 查询不到对应数据返回 null 数组
     */
    public List<String[]> queryAll() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(mBuilder.getTableName(), null,
                null, null, null, null, null);

        List<String[]> retList = new ArrayList<>();
        String[] values = new String[mBuilder.getColumnTitles().length];
        if (cursor != null && cursor.moveToFirst()) {
            do {
                for (int i = 0; i < mBuilder.getColumnTitles().length; ++i) {
                    values[i] = cursor.getString(cursor.getColumnIndex(mBuilder.getColumnTitles()[i]));
                }
                retList.add(values);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return retList;
    }

    /**
     * 获取所有的数据的String值
     * 用来打印Log
     */
    public String queryAll2String() {
        String str = "{ ";
        for (String[] array : queryAll()) {
            str += "[";
            for (String s : array) {
                str += s + " ";
            }
            str += "]";
        }
        str += " }";
        str = str.replace(" ]", "]");
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

            int length = mBuilder.getColumnTitles().length;
            for (int i = 0; i < length - 1; ++i) {
                sql += (mBuilder.getColumnTitles()[i] + " text, ");
            }
            sql += (mBuilder.getColumnTitles()[length - 1] + " text) ;");
            db.execSQL(sql);
            defaultInitial(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + mBuilder.getTableName());
            onCreate(db);
        }

        void defaultInitial(SQLiteDatabase db) {
            if (mBuilder.getColumnValues() == null) {
                return;
            }

            String sql = "INSERT OR IGNORE INTO " +  mBuilder.getTableName();
            sql += " (" + mBuilder.getColumnTitles()[0];
            int keyLen = mBuilder.getColumnTitles().length;
            for (int i = 1; i < keyLen; ++i) {
                sql += ( "," +  mBuilder.getColumnTitles()[i] );
            }
            sql += ") VALUES " + loadColumnValue(mBuilder.getColumnValues().get(0));

            int valueLen = mBuilder.getColumnValues().size();
            for (int i = 1; i < valueLen; ++i) {
                sql += " , ";
                sql += loadColumnValue(mBuilder.getColumnValues().get(i));
            }
            sql += " ;";
            db.execSQL(sql);
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

        void setColumnTitles(String[] columnTitles) {
            mColumnTitles = columnTitles;
        }

        void setColumnValues(List<String[]> values) {
            mColumnValues = values;
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

        String[] getColumnTitles() {
            return mColumnTitles;
        }

        List<String[]> getColumnValues() {
            return mColumnValues;
        }

        // for database
        String mDbName = "aviana.db";
        int mDbVersion = 1;

        // for table
        final String _ID = "_id";
        String mTableName = "table";
        String[] mColumnTitles = new String[]{"title1", "title2", "title3", "title4"};
        List<String[]> mColumnValues = null;
    }
}
