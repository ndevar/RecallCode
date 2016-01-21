package com.example.nithy_000.ycalled;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.TelephonyManager;


/**
 * Created by nithy_000 on 06-08-2015.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    public static String callNote = "";
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        ctx = context;
        String state = bundle.getString(TelephonyManager.EXTRA_STATE);
        if (state != null) {

            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
             try{
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingNumber = incomingNumber.substring(incomingNumber.length() - 10);
                DataBaseoperations dbOperation = new DataBaseoperations(context);
                callNote = dbOperation.getCallNoteDetails(incomingNumber);
                // This code will execute when the phone has an incoming call
                if (callNote.trim() != "") {
                    ToastExpander.makeLongToast(context, callNote);
                }
            }
                catch(Exception ex)
                {

                }
            }
            else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                try {
                    Thread.sleep(1000L);
                    if (ToastExpander.toast != null) {
                        ToastExpander.DestroyToast();
                    }
                    Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null, null, CallLog.Calls.DATE + " DESC");
                    int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                    int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                    int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);

                    if (managedCursor.moveToFirst() == true) {
                        String phNumber = managedCursor.getString(number);
                        String callType = managedCursor.getString(type);
                        int dircode = Integer.parseInt(callType);
                        String callDuration = managedCursor.getString(duration);
                        int callHours = Integer.parseInt(callDuration.toString());
                        if (callHours == 0) {
                            if (dircode == CallLog.Calls.OUTGOING_TYPE) {
                                notify("YCalled", "Add a call Note to " + phNumber + " ?", context, phNumber);
                            }
                        }
                        managedCursor.close();
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        } else {
            try {
                if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                    String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    number = number.substring(number.length() - 10);
                    DataBaseoperations dbOperation = new DataBaseoperations(context);
                    callNote = dbOperation.getCallNoteDetails(number);
                    if (callNote.trim() != "") {
                        ToastExpander.makeLongToast(context, callNote);
                    }
                }
            }
            catch(Exception ex)
            {

            }

        }
    }


    // To get the notification
    public void notify(String notificationTitle, String notificationMessage,Context context,String phNumber) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(R.drawable.logofornotification, "Do you want to add a Call Note to " + phNumber + " ? ", System.currentTimeMillis());
            Intent notificationIntent = new Intent(context, YCalledNotificationPage.class);
            notificationIntent.putExtra("phNumber", phNumber);
            notificationIntent.setData((Uri.parse("phNumber" + phNumber)));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notification.setLatestEventInfo(context, notificationTitle, notificationMessage, pendingIntent);
            notificationManager.notify(9999, notification);
        } catch (Exception ex) {

        }
    }
}
