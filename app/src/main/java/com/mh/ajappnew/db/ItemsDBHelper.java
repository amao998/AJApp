package com.mh.ajappnew.db;


import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class ItemsDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "UserDBHelper";
    private static final String DB_NAME = "Itemsdb.db"; // 数据库的名称
    private static final int DB_VERSION = 1; // 数据库的版本号
    private static ItemsDBHelper mHelper = null; // 数据库帮助器的实例
    private SQLiteDatabase mDB = null; // 数据库的实例
    public static final String TABLE_NAME = "itemsinfo"; // 表的名称

    private ItemsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private ItemsDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    // 利用单例模式获取数据库帮助器的唯一实例
    public static ItemsDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new ItemsDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new ItemsDBHelper(context);
        }
        return mHelper;
    }

    // 打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    // 打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    // 关闭数据库连接
    public void closeLink() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
            mDB = null;
        }
    }

    // 创建数据库，执行建表语句
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL,"
                + "id VARCHAR NOT NULL," + "value VARCHAR NOT NULL,"
                + "idandname VARCHAR NOT NULL," + "flag VARCHAR NOT NULL,"
                + "netid VARCHAR NOT NULL," + "remark VARCHAR NOT NULL"
                //演示数据库升级时要先把下面这行注释
                + ",sort INTEGER NOT NULL,jcxlb VARCHAR NOT NULL"
                + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    // 修改数据库，执行表结构变更语句
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        if (newVersion > 1) {
            //Android的ALTER命令不支持一次添加多列，只能分多次添加
            String alter_sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + "phone VARCHAR;";
            Log.d(TAG, "alter_sql:" + alter_sql);
            db.execSQL(alter_sql);
            alter_sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + "password VARCHAR;";
            Log.d(TAG, "alter_sql:" + alter_sql);
            db.execSQL(alter_sql);
        }
    }

    // 根据指定条件删除表记录
    public int delete(String condition) {
        // 执行删除记录动作，该语句返回删除记录的数目
        return mDB.delete(TABLE_NAME, condition, null);
    }

    // 删除该表的所有记录
    public int deleteAll() {
        // 执行删除记录动作，该语句返回删除记录的数目
        return mDB.delete(TABLE_NAME, "1=1", null);
    }

    // 往该表添加一条记录
    public long insert(ItemsInfo info) {
        ArrayList<ItemsInfo> infoArray = new ArrayList<ItemsInfo>();
        infoArray.add(info);
        return insert(infoArray);
    }

    // 往该表添加多条记录
    public long insert(ArrayList<ItemsInfo> infoArray) {
        long result = -1;
        for (int i = 0; i < infoArray.size(); i++) {
            ItemsInfo info = infoArray.get(i);

            // 不存在唯一性重复的记录，则插入新记录
            ContentValues cv = new ContentValues();
            cv.put("id", info.id);
            cv.put("value", info.value);
            cv.put("idandname", info.idandname);
            cv.put("flag", info.flag);
            cv.put("netid", info.netid);
            cv.put("remark", info.remark);
            cv.put("sort", info.sort);
            cv.put("jcxlb", info.jcxlb);
            // 执行插入记录动作，该语句返回插入记录的行号
            result = mDB.insert(TABLE_NAME, "", cv);
            // 添加成功后返回行号，失败后返回-1
            if (result == -1) {
                return result;
            }
        }
        return result;
    }

    // 根据条件更新指定的表记录
    public int update(ItemsInfo info, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("id", info.id);
        cv.put("value", info.value);
        cv.put("idandname", info.idandname);
        cv.put("flag", info.flag);
        cv.put("netid", info.netid);
        cv.put("remark", info.remark);
        cv.put("sort", info.sort);
        cv.put("jcxlb", info.jcxlb);
        // 执行更新记录动作，该语句返回记录更新的数目
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    public int update(ItemsInfo info) {
        // 执行更新记录动作，该语句返回记录更新的数目
        return update(info, "id=" + info.id);
    }

    // 根据指定条件查询记录，并返回结果数据队列
    public ArrayList<ItemsInfo> query(String condition) {
        String sql = String.format("select id,value,flag,netid,remark,idandname,sort,jcxlb " +
            " from %s where %s order by flag,sort;", TABLE_NAME, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<ItemsInfo> infoArray = new ArrayList<ItemsInfo>();
        // 执行记录查询动作，该语句返回结果集的游标
        Cursor cursor = mDB.rawQuery(sql, null);
        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()) {
            ItemsInfo info = new ItemsInfo();
            info.id = cursor.getString(0); // 取出长整型数
            info.value = cursor.getString(1); // 取出整型数
            info.flag = cursor.getString(2); // 取出字符串
            info.netid = cursor.getString(3);
            info.remark = cursor.getString(4);
            info.idandname = cursor.getString(5);
            info.sort = cursor.getInt(6);
            info.jcxlb = cursor.getString(7);
            infoArray.add(info);
        }
        cursor.close(); // 查询完毕，关闭游标
        return infoArray;
    }

    // 根据手机号码查询指定记录
    public ItemsInfo queryByidAndFlag(String id,String flag) {
        ItemsInfo info = null;
        ArrayList<ItemsInfo> infoArray = query(String.format("id='%s' and flag='%s'", id,flag));
        if (infoArray.size() > 0) {
            info = infoArray.get(0);
        }
        return info;
    }

}
