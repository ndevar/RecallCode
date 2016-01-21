package com.example.nithy_000.ycalled;
import android.graphics.Bitmap;

//ContactInfo class
public class ContactInfo {
    public String _phoneNumber;
    public String _contactName;
    public Bitmap _photoUrl;
    public String _callNote;
    public String _dateValue;
    public String _callDate;
    public String _callDay;
    public String _callTime;


    //Constructor
    public ContactInfo(String phoneNumber,String contactName, Bitmap photoUrl, String callNote, String dateValue,String callDate, String callDay, String callTime  )
    {
        _phoneNumber=phoneNumber;
        _contactName=contactName;
        _photoUrl=photoUrl;
        _callNote=callNote;
        _dateValue=dateValue;
        _callDate=callDate;
        _callDay=callDay;
        _callTime=callTime;
    }
}
