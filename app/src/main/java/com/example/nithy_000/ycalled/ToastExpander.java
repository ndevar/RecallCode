package com.example.nithy_000.ycalled;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

/**
 * Created by nithy_000 on 12-08-2015.
 */
public class ToastExpander {
    public static Toast toast=null;


    public static void makeLongToast(Context context,String text) {
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();

        new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(toast!=null) {
                    toast.show();
                }
            }

            public void onFinish() {
                if (toast != null) {
                    toast.cancel();
                    toast=null;
                }
            }

        }.start();
    }
    public static void DestroyToast(){
        if(toast!=null) {
            toast.cancel();
            toast=null;
        }
    }
}
