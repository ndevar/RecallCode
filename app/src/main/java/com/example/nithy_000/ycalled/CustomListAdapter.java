package com.example.nithy_000.ycalled;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by nithy_000 on 20-07-2015.
 */

//Class to bind the listview items
public class CustomListAdapter extends ArrayAdapter<ContactInfo>
{
    private LayoutInflater mLayoutInflater= (LayoutInflater)  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    public CustomListAdapter(Context context, ArrayList<ContactInfo> contactInfo)
    {
        super(context, 0, contactInfo);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try
        {
           final ContactInfo contactInfo = getItem(position);
            //Reuse the view
            if (convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.listitem, null);
            }

            TextView txtTodayDate = (TextView) convertView.findViewById(R.id.callDate23);

            TextView txtSpeech = (TextView) convertView.findViewById(R.id.txtSpeech);

            TextView txtName = (TextView) convertView.findViewById(R.id.Name);
            final TextView txtPhoneNumber = (TextView) convertView.findViewById(R.id.PhoneNumber);
            TextView txtcallDetails = (TextView) convertView.findViewById(R.id.callDayTime);
            TextView txtDate = (TextView) convertView.findViewById(R.id.callDate);
            TextView txtDay = (TextView) convertView.findViewById(R.id.callDay);
            ImageView imgView = (ImageView) convertView.findViewById(R.id.contactPhoto);

            final TextView callNoteTxt = (TextView) convertView.findViewById(R.id.txtCallNote);
            final TextView txtCallNote = (TextView) convertView.findViewById(R.id.CallNote);
            final EditText editText = (EditText) convertView.findViewById(R.id.edittext);
            final TextView txtLastCallInfo = (TextView) convertView.findViewById(R.id.LastCalledInfo);
            final TextView btnCallNote = (TextView) convertView.findViewById(R.id.txtImgCallNote);
            final TextView btnCallNoteAdded = (TextView) convertView.findViewById(R.id.txtImgCallNoteAdded);
            final TextView btnDelete = (TextView) convertView.findViewById(R.id.txtImgDelete);




            RelativeLayout MainLayout = (RelativeLayout) convertView.findViewById(R.id.mainRelativeLayout);

            MainLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (editText.isFocused()) {
                       // if (event.getX() <= 157 || event.getY() <= 388 || event.getX() >= 473 || event.getY() >= 569) {
                            //Will only enter this if the EditText already has focus
                            //And if a touch event happens outside of the EditText
                            //Which in my case is at the top of my layout
                            //and 72 pixels long
                            String phNumber = txtPhoneNumber.getText().toString();
                            phNumber = phNumber.substring(phNumber.length() - 10);
                            txtCallNote.setText("Call Note - " + editText.getText());
                            editText.setVisibility(View.GONE);

                            DataBaseoperations dbOperations = new DataBaseoperations(getContext());
                            Boolean check = dbOperations.dataAlreadyInDBorNot(dbOperations, DbYCalled.TableInfo.TABLE_NAME, DbYCalled.TableInfo.PHONENUMBER, phNumber);
                            if (check == true) {
                                if (editText.getText().toString().trim().length() == 0) {
                                    dbOperations.deleteContact(dbOperations, phNumber);
                                    btnCallNote.setVisibility(View.VISIBLE);
                                    btnDelete.setVisibility(View.GONE);
                                    txtCallNote.setText("Call Note - Add Call Note");
                                    ContactInfo Updateinfo= new ContactInfo(contactInfo._phoneNumber,contactInfo._contactName,contactInfo._photoUrl,"",contactInfo._dateValue,contactInfo._callDate,contactInfo._callDay,contactInfo._callTime);
                                    YCalledActivity.lstContactinfo.set(getPosition(contactInfo), Updateinfo);
                                    YCalledActivity.adapter.notifyDataSetChanged();
                                } else {
                                    dbOperations.updateData(dbOperations, phNumber, editText.getText().toString());
                                    btnCallNote.setVisibility(View.GONE);
                                    btnDelete.setVisibility(View.VISIBLE);
                                    ContactInfo Updateinfo= new ContactInfo(contactInfo._phoneNumber,contactInfo._contactName,contactInfo._photoUrl,editText.getText().toString(),contactInfo._dateValue,contactInfo._callDate,contactInfo._callDay,contactInfo._callTime);
                                    YCalledActivity.lstContactinfo.set(getPosition(contactInfo), Updateinfo);
                                    YCalledActivity.adapter.notifyDataSetChanged();
                                }
                            } else {
                                if (editText.getText().toString().trim().length() > 0) {
                                    dbOperations.insertInfo(dbOperations, phNumber, editText.getText().toString());
                                    btnCallNote.setVisibility(View.GONE);
                                    btnDelete.setVisibility(View.VISIBLE);
                                    ContactInfo Updateinfo= new ContactInfo(contactInfo._phoneNumber,contactInfo._contactName,contactInfo._photoUrl,editText.getText().toString(),contactInfo._dateValue,contactInfo._callDate,contactInfo._callDay,contactInfo._callTime);
                                    YCalledActivity.lstContactinfo.set(getPosition(contactInfo),Updateinfo);
                                    YCalledActivity.adapter.notifyDataSetChanged();
                                } else {
                                    btnCallNote.setVisibility(View.VISIBLE);
                                    btnDelete.setVisibility(View.GONE);
                                    txtCallNote.setText("Call Note - Add Call Note");
                                }
                            }
                        }

                    InputMethodManager imm = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                        return false;
                }
            });


            txtSpeech.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String phNumber = txtPhoneNumber.getText().toString();
                    phNumber = phNumber.substring(phNumber.length() - 10);
                    Intent notificationIntent = new Intent(getContext(), SpeechToText.class);
                    notificationIntent.putExtra("phNumber", phNumber);
                    notificationIntent.setData((Uri.parse("phNumber" + phNumber)));
                    getContext().startActivity(notificationIntent);
                }
            });



            txtCallNote.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (txtCallNote.getText().toString() != "Call Note - Add Call Note") {
                        editText.setText("");
                        editText.setVisibility(View.VISIBLE);
                        DataBaseoperations dbOperations = new DataBaseoperations(getContext());
                        String phNumber = txtPhoneNumber.getText().toString().trim();
                        phNumber = phNumber.substring(phNumber.length() - 10);
                        String callNote = dbOperations.getCallNoteDetails(phNumber);
                        if(callNote!="") {
                            editText.setText(callNote);
                            editText.requestFocus();
                        editText.setSelection(editText.getText().length());
                        }
                        else {
                            editText.setText("");
                            editText.requestFocus();
                            editText.setSelection(0);
                        }
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
                    }
                }
            });

            TextView txtRemainder = (TextView) convertView.findViewById(R.id.txtRemainder);
            txtRemainder.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String txtCallN=txtCallNote.getText().toString();
                    if(txtCallN!="Call Note - Add Call Note") {
                        Calendar cal = Calendar.getInstance();
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", cal.getTimeInMillis());
                        intent.putExtra("allDay", true);
                        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                        intent.putExtra("title", txtCallN);
                        getContext().startActivity(intent);
                    }
                }
            });


            btnCallNote.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    editText.setText("");
                    editText.setVisibility(View.VISIBLE);
                    DataBaseoperations dbOperations = new DataBaseoperations(getContext());
                    String phNumber = txtPhoneNumber.getText().toString().trim();
                    phNumber = phNumber.substring(phNumber.length() - 10);
                    String callNote = dbOperations.getCallNoteDetails(phNumber);
                    editText.setText(callNote);
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String phNumber = txtPhoneNumber.getText().toString().trim();
                    phNumber = phNumber.substring(phNumber.length() - 10);
                    DataBaseoperations dbOperations = new DataBaseoperations(getContext());
                    Boolean check = dbOperations.dataAlreadyInDBorNot(dbOperations, DbYCalled.TableInfo.TABLE_NAME, DbYCalled.TableInfo.PHONENUMBER, phNumber);
                    if (check == true) {
                        if (editText.getText().toString().trim() != "") {

                            dbOperations.deleteContact(dbOperations, phNumber);
                            ContactInfo Updateinfo= new ContactInfo(contactInfo._phoneNumber,contactInfo._contactName,contactInfo._photoUrl,"",contactInfo._dateValue,contactInfo._callDate,contactInfo._callDay,contactInfo._callTime);
                            YCalledActivity.lstContactinfo.set(getPosition(contactInfo), Updateinfo);
                            YCalledActivity.adapter.notifyDataSetChanged();
                        }
                    }
                    editText.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                    btnCallNote.setVisibility(View.VISIBLE);
                    txtCallNote.setText("Call Note - Add Call Note");
                }
            });
            if (contactInfo._callNote.trim() != "") {
                txtCallNote.setText("Call Note - " + contactInfo._callNote);
                callNoteTxt.setText(contactInfo._callNote);
                btnCallNote.setVisibility(View.GONE);
                btnCallNoteAdded.setVisibility(View.VISIBLE);
                //btnDelete.setVisibility(View.VISIBLE);
                //btnCallNote.setVisibility(View.GONE);

            } else {
                callNoteTxt.setText("Add Recall Note");
                txtCallNote.setText("Call Note - Add Call Note");
                btnCallNote.setVisibility(View.VISIBLE);
                btnCallNoteAdded.setVisibility(View.GONE);
                //btnDelete.setVisibility(View.GONE);
                //btnCallNote.setVisibility(View.VISIBLE);

            }

            txtName.setText(contactInfo._contactName);
            if (txtName.getText().toString().trim() == "") {
                txtName.setText("Unknown");
            }
            if (contactInfo._dateValue == "Today") {
                txtDate.setText("Today");
                txtDay.setText("");
                int randomColor = Color.rgb(255, 20, 147);
                //txtName.setTextColor(randomColor);
                txtDate.setTextColor(randomColor);
                txtLastCallInfo.setText(contactInfo._callTime);
                txtDate.setBackgroundResource(0);
            } else {
                txtDate.setText(contactInfo._callDate);
                txtDay.setText(contactInfo._callDay);
                int randomColor = Color.rgb(30, 144, 225);
                //txtName.setTextColor(randomColor);
                txtDate.setTextColor(randomColor);
                txtLastCallInfo.setText(contactInfo._callTime);
                //imgCalender.setVisibility(View.VISIBLE);
                txtDate.setBackgroundResource(R.drawable.calender);
            }

            //Log.d("test","The value is "+contactInfo._dateValue.toString());
            //Toast.makeText(getContext().getApplicationContext(),contactInfo._dateValue,Toast.LENGTH_SHORT);
            if(contactInfo._dateValue!="")
            {
                txtTodayDate.setVisibility(View.VISIBLE);
                txtTodayDate.setText(contactInfo._dateValue);
            }
            else
            {
                txtTodayDate.setVisibility(View.GONE);
            }



            txtPhoneNumber.setText(contactInfo._phoneNumber);
            txtcallDetails.setText(contactInfo._dateValue);
            if (contactInfo._photoUrl != null) {
                imgView.setImageBitmap(contactInfo._photoUrl);
            } else {
                imgView.setImageURI(Uri.parse("android.resource://com.example.nithy_000.ycalled/drawable/defaultuserphoto"));
            }

            Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(convertView.findViewById(R.id.txtRemainder), iconFont);
            FontManager.markAsIconContainer(convertView.findViewById(R.id.txtImgCallNote), iconFont);
            FontManager.markAsIconContainer(convertView.findViewById(R.id.txtImgDelete), iconFont);
            FontManager.markAsIconContainer(convertView.findViewById(R.id.txtSpeech), iconFont);
            FontManager.markAsIconContainer(convertView.findViewById(R.id.txtImgCallNoteAdded), iconFont);
            return convertView;
        }
        catch(Exception ex)
        {
        return null;
        }
    }
    @Override
    public int getViewTypeCount()
    {
        return getCount();

    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

}



