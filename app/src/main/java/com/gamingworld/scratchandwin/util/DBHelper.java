package com.gamingworld.scratchandwin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

   public static final String DATABASE_NAME = "Transactions.db";
   public static final String TRANSACTIONS_TABLE_NAME = "transactions";
   public static final String TRANSACTIONS_COLUMN_ID = "id";
   public static final String TRANSACTIONS_COLUMN_COINS = "coins";
   public static final String TRANSACTIONS_COLUMN_MEDIUM = "medium";
   public static final String TRANSACTIONS_COLUMN_DETAILS = "details";
   public static final String TRANSACTIONS_COLUMN_STATUS = "status";
   public static final String TRANSACTIONS_COLUMN_DATE = "date";
   private HashMap hp;

   public DBHelper(Context context) {
      super(context, DATABASE_NAME , null, 1);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
      db.execSQL(
         "create table transactions " +
         "(id integer primary key, coins integer,medium text,details text, status text, date text)"
      );
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      db.execSQL("DROP TABLE IF EXISTS transactions");
      onCreate(db);
   }

   public boolean insertTransaction (Integer coins, String medium, String details, String status, String date) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("coins", coins);
      contentValues.put("medium", medium);
      contentValues.put("details", details);
      contentValues.put("status", status);
      contentValues.put("date", date);
      db.insert("transactions", null, contentValues);
      return true;
   }

   public boolean updateTransaction (String status, String date) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("status", status);
      contentValues.put("date", date);
      db.update("transactions", contentValues, "status = ? ", new String[] { "Pending" } );
      return true;
   }

   public ArrayList<TransactionDetail> getAllTransactions() {
      ArrayList<TransactionDetail> array_list = new ArrayList<TransactionDetail>();
      
      //hp = new HashMap();
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from transactions", null );
      res.moveToFirst();
      
      while(res.isAfterLast() == false){
         int coins = res.getInt(res.getColumnIndex("coins"));
         String medium = res.getString(res.getColumnIndex("medium"));
         String details = res.getString(res.getColumnIndex("details"));
         String status = res.getString(res.getColumnIndex("status"));
         String date = res.getString(res.getColumnIndex("date"));
         array_list.add(new TransactionDetail(status, coins, medium, details, date));
         res.moveToNext();
      }
      return array_list;
   }
}