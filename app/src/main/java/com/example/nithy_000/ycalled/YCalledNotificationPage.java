package com.example.nithy_000.ycalled;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class YCalledNotificationPage extends Activity {
    Context context =this;
    String phNumber="";
    protected static final int RESULT_SPEECH = 1;

    private TextView txtSpeak;
    private EditText txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ycalled_notification_page);

            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(findViewById(R.id.txtSpeech), iconFont);

            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                phNumber = extras.getString("phNumber");
            }

            txtSpeak = (TextView) findViewById(R.id.txtSpeech);

            txtText = (EditText) findViewById(R.id.editNotificationScreen);

            txtSpeak.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                    try {
                        startActivityForResult(intent, RESULT_SPEECH);
                        txtText.setText("");
                    } catch (ActivityNotFoundException a) {
                        Toast t = Toast.makeText(getApplicationContext(),
                                "Ops! Your device doesn't support Speech to Text",
                                Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
            });

            phNumber = phNumber.substring(phNumber.length() - 10);
            String ns = context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
            nMgr.cancel(9999);

            Button btnOk = (Button) findViewById(R.id.btnOk);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);

            final EditText editNotificationScreen = (EditText) findViewById(R.id.editNotificationScreen);
            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("Loading....Please Wait");
                    dialog.setCancelable(false);
                    dialog.setInverseBackgroundForced(false);
                    dialog.show();

                    Intent intent = new Intent(context, YCalledActivity.class);
                    startActivity(intent);
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("Loading....Please Wait");
                    dialog.setCancelable(false);
                    dialog.setInverseBackgroundForced(false);
                    dialog.show();

                    DataBaseoperations dbOperations = new DataBaseoperations(context);
                    Boolean check = dbOperations.dataAlreadyInDBorNot(dbOperations, DbYCalled.TableInfo.TABLE_NAME, DbYCalled.TableInfo.PHONENUMBER, phNumber);
                    if (check == true) {
                        if (editNotificationScreen.getText().toString().trim().length() == 0) {
                            dbOperations.deleteContact(dbOperations, phNumber);
                        } else {
                            dbOperations.updateData(dbOperations, phNumber, editNotificationScreen.getText().toString());
                        }
                    } else {
                        if (editNotificationScreen.getText().toString().trim().length() == 0) {
                            dbOperations.deleteContact(dbOperations, phNumber);
                        } else {
                            dbOperations.deleteContact(dbOperations, phNumber);
                            dbOperations.insertInfo(dbOperations, phNumber, editNotificationScreen.getText().toString());
                        }
                    }

                    Intent intent = new Intent(context, YCalledActivity.class);
                    startActivity(intent);
                }
            });
        } catch (Exception ex) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ycalled_notification_page, menu);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));
                }
                break;
            }

        }
    }

}
