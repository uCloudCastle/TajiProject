package com.randal.aviana.database;


import android.content.Context;

import java.util.List;


public class SQLiteUtils {
    public static KeyValueTable
    createOrOpenKeyValueTable(Context context, String dbName, String tableName) {
        return new KeyValueTable(context, dbName, tableName);
    }

    public static TextTable
    createOrOpenTextTable(Context context, String dbName, String tableName,
                    String[] columnTitles,
                    List<String[]> columnValues) {
        return new TextTable(context, dbName, tableName,
                columnTitles, columnValues);
    }
}
