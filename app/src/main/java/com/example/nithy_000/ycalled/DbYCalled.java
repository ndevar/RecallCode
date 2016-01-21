package com.example.nithy_000.ycalled;
import android.provider.BaseColumns;

public final class  DbYCalled  {
        // To Prevent someone from accidentally contract instantiating the class,
        // Give it an empty constructor.
        public  DbYCalled ()  {};
        public  static  class  TableInfo  implements BaseColumns {
            public  static  final  String PHONENUMBER =  "Phone_Number" ;
            public  static  final  String CALLNOTE =  "CallNote" ;
            public static final String DATABASE_NAME="dbCallNote";
            public static final String TABLE_NAME="tblCallNote";
        }
}
