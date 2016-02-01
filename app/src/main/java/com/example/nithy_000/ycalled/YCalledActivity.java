package com.example.nithy_000.ycalled;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class YCalledActivity extends Activity {

    Context context =this;
    DataBaseoperations db;
    public static CustomListAdapter adapter;
    public static ArrayList<String> lstRemoveduplicateDate = new ArrayList<>();
    public static ArrayList<ContactInfo> lstContactinfo= new ArrayList<ContactInfo>();;
    protected static final int RESULT_SPEECH = 1;
    TextView txtcallNote;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ycalled);


            db = new DataBaseoperations(context);
            lstContactinfo.clear();
            getCallDetails();
            if (lstContactinfo.size() == 0) {
                TextView txtNoCallInfo = (TextView) findViewById(R.id.txtNoCallInfo);
                txtNoCallInfo.setText("Unanswered outgoing call log is empty.");
                txtNoCallInfo.setVisibility(View.VISIBLE);
            }
            else
            {
                ListView listView = (ListView) findViewById(R.id.yCalledList);
                adapter = new CustomListAdapter(this, lstContactinfo);
                listView.setAdapter(adapter);
            }
        }
        catch (Exception ex)
        {
            Log.d("test", ex.getMessage().toString());
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ycalled, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Method to get the unanswered outgoing call log
    private void getCallDetails() {
        try {
            lstRemoveduplicateDate.clear();
            ArrayList<String> lstremoveDuplicates = new ArrayList<String>();
            Cursor people = managedQuery(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null, null, CallLog.Calls.DATE + " DESC");

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            if (managedCursor != null) {
                while (managedCursor.moveToNext()) {
                    String phNumber = managedCursor.getString(number);
                    String dbPhNuber = phNumber.substring(phNumber.length() - 10);

                    String dspName = getContactName(phNumber);
                    String callType = managedCursor.getString(type);
                    int dircode = Integer.parseInt(callType);
                    String callDuration = managedCursor.getString(duration);
                    int callHours = Integer.parseInt(callDuration.toString());
                    if (callHours == 0) {
                        switch (dircode) {
                            case CallLog.Calls.OUTGOING_TYPE:

                                if (!lstremoveDuplicates.contains(dbPhNuber)) {
                                    lstremoveDuplicates.add(dbPhNuber);

                                    String callDate = managedCursor.getString(date);
                                    Date callDayTime = new Date(Long.valueOf(callDate));
                                    String contactId = fetchContactIdFromPhoneNumber(phNumber);
                                    Bitmap photo = null;
                                    if (contactId != "") {
                                        photo = loadContactPhoto(Long.parseLong(contactId));
                                    }
                                    String callNote = db.getCallNoteDetails(dbPhNuber);
                                    String Datevalue = "";
                                    String callday = "";
                                    String calldate = "";
                                    String calltime = "";
                                    SimpleDateFormat checkDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    Date today = new Date();
                                    String date1 = checkDateFormat.format(callDayTime);
                                    String date2 = checkDateFormat.format(today);

                                    SimpleDateFormat checkDay = new SimpleDateFormat("dd/MM/yyyy");
                                    checkDay.applyPattern("EEE");
                                    SimpleDateFormat checkDate = new SimpleDateFormat("dd");
                                    checkDate.applyPattern("dd");

                                    if (date1.compareTo(date2) == 0) {
                                        date1="Today";

                                        Datevalue = "Today";
                                        calldate = "";
                                        callday = "";
                                        SimpleDateFormat checkHour = new SimpleDateFormat("HH");
                                        SimpleDateFormat checkMin = new SimpleDateFormat("mm");
                                        int hours = Integer.parseInt(checkHour.format(today)) - Integer.parseInt(checkHour.format(callDayTime));
                                        int mins = Integer.parseInt(checkMin.format(today)) - Integer.parseInt(checkMin.format(callDayTime));
                                        calltime = "Last called " + Integer.toString(hours) + " Hour(s)" + Integer.toString(mins) + " mins ago";
                                    }

                                    else {
                                        callday = checkDay.format(callDayTime);
                                        calldate = checkDate.format(callDayTime);
                                        calltime = "Last called " + date1;
                                    }
                                    if(!lstRemoveduplicateDate.contains(date1))
                                    {
                                        lstRemoveduplicateDate.add(date1);
                                    }
                                    else
                                    {
                                        date1="";
                                    }


                                    ContactInfo contactInfo = new ContactInfo(phNumber, dspName, photo, callNote, date1, calldate, callday, calltime);//,path);
                                    lstContactinfo.add(contactInfo);
                                    break;

                                }
                        }
                    }
                }
            }
            managedCursor.close();
        }
        catch(Exception ex)
        {
            Log.d("test", ex.getMessage().toString());
        }
    }

    //Method to get the round shaped image
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
        catch (Exception ex)
        {
            return null;
        }
    }


    // Method to get the photo url
    public Bitmap loadContactPhoto(long  id) {
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
            if (input == null) {
                Bitmap myBitmap = null;
                return myBitmap;
            } else {
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                myBitmap = getCroppedBitmap(myBitmap);
                return myBitmap;
            }

        } catch (Exception ex) {
        return null;
        }
    }

    //Method to get the contact id from the phone number
    private String fetchContactIdFromPhoneNumber(String phoneNumber) {
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));
            Cursor cFetch = getContentResolver().query(uri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                    null, null, null);

            String contactId = "";


            if (cFetch.moveToFirst()) {

                cFetch.moveToFirst();

                contactId = cFetch.getString(cFetch
                        .getColumnIndex(ContactsContract.PhoneLookup._ID));

            }
            cFetch.close();
            return contactId;

        }
        catch (Exception ex)
        {
        return "";
        }
    }

    //Method to get the contact name from the phone number
    public String getContactName(final String phoneNumber) {
        try {
            Uri uri;
            String[] projection;

            if (Build.VERSION.SDK_INT >= 5) {
                uri = Uri.parse("content://com.android.contacts/phone_lookup");
                projection = new String[]{"display_name"};
            } else {
                uri = Uri.parse("content://contacts/phones/filter");
                projection = new String[]{"name"};
            }

            uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
            Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

            String contactName = "";

            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }

            cursor = null;
            return contactName;
        }
        catch (Exception ex)
        {
        return "";
        }
    }


}