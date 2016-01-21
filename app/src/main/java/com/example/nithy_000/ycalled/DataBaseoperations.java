package com.example.nithy_000.ycalled;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nithy_000 on 22-07-2015.
 */
public class DataBaseoperations extends SQLiteOpenHelper {
    public static final int database_version=5;
    Context context;
    String CREATE_QUERY="CREATE TABLE "+ DbYCalled.TableInfo.TABLE_NAME + "(" + DbYCalled.TableInfo.PHONENUMBER + " TEXT," + DbYCalled.TableInfo.CALLNOTE + " TEXT " + ");";

    public DataBaseoperations(Context context) {
        super(context, DbYCalled.TableInfo.DATABASE_NAME, null, database_version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sdb)
    {
        sdb.execSQL(CREATE_QUERY);
    }

    //To insert the call note details into the table with phone number as the key
    public void insertInfo(DataBaseoperations dop, String phNumber, String callNote) {
        try {
            SQLiteDatabase sqlDb = dop.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DbYCalled.TableInfo.PHONENUMBER, phNumber.trim());
            cv.put(DbYCalled.TableInfo.CALLNOTE, callNote.trim());
            sqlDb.insert(DbYCalled.TableInfo.TABLE_NAME, null, cv);
        } catch (Exception ex) {

        }
    }

    // To retrieve the callnote details using the phone number
    public String getCallNoteDetails(String phoneNumber) {
        try {
            String callNote = "";
            SQLiteDatabase sqlDb = this.getReadableDatabase();
            Cursor cursor = sqlDb.query(DbYCalled.TableInfo.TABLE_NAME, new String[]{DbYCalled.TableInfo.PHONENUMBER, DbYCalled.TableInfo.CALLNOTE}, DbYCalled.TableInfo.PHONENUMBER + "=?", new String[]{String.valueOf(phoneNumber)}, null, null, null, null);
            if (cursor.moveToFirst()) {
                callNote = cursor.getString(1).toString();

            } else {
                callNote = "";
            }
            cursor.close();
            return callNote;
        } catch (Exception ex) {
        return "";
        }
    }

    //Method to check if the callnote details is available in the database. if yes->update, else insert
    public boolean dataAlreadyInDBorNot(DataBaseoperations dop,String tableName,
                                               String dbfield, String fieldValue) {
        try {
            SQLiteDatabase db = dop.getReadableDatabase();
            String Query = "Select * from " + tableName + " where " + dbfield + " = ?";
            Cursor cursor = db.rawQuery(Query, new String[]{fieldValue});
            if (cursor.getCount() <= 0) {

                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        } catch (Exception ex) {
        return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //Method to update the call note using the phonenumber
    public void updateData(DataBaseoperations dop,String phNumber, String callNote) {
        try {
            SQLiteDatabase db = dop.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put("Phone_Number", phNumber);
            args.put("CallNote", callNote);
            db.update(DbYCalled.TableInfo.TABLE_NAME, args, "Phone_Number" + "='" + phNumber.trim() + "'", null);
        } catch (Exception ex) {

        }
    }

    //Method to delete the contact details
    public void deleteContact(DataBaseoperations dop,String phNumber) {
        try {
            SQLiteDatabase db = dop.getWritableDatabase();
            db.execSQL("DELETE FROM " + DbYCalled.TableInfo.TABLE_NAME + " WHERE " + DbYCalled.TableInfo.PHONENUMBER + "='" + phNumber.trim() + "'");
        } catch (Exception ex) {

        }
    }
}

