package com.example.nithy_000.ycalled;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SpeechToText extends Activity {
    Context context =this;
    TextView txtText;
    protected static final int RESULT_SPEECH = 1;
    String phNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            phNumber = extras.getString("phNumber");
        }
        Log.d("test",phNumber);
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Ops! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("test", text.get(0));
                    DataBaseoperations dbOperations = new DataBaseoperations(context);
                    Boolean check = dbOperations.dataAlreadyInDBorNot(dbOperations, DbYCalled.TableInfo.TABLE_NAME, DbYCalled.TableInfo.PHONENUMBER, phNumber);
                    if (check == true) {
                        if (text.get(0).length() == 0) {
                            dbOperations.deleteContact(dbOperations, phNumber);
                        } else {
                            dbOperations.updateData(dbOperations, phNumber, text.get(0));
                        }
                    } else {
                        if (text.get(0).length() == 0) {
                            dbOperations.deleteContact(dbOperations, phNumber);
                        } else {
                            dbOperations.deleteContact(dbOperations, phNumber);
                            Log.d("test", "Before Insert");
                            dbOperations.insertInfo(dbOperations, phNumber, text.get(0));
                            Log.d("test", "After Insert");
                        }
                    }

                    Intent intent = new Intent(context, YCalledActivity.class);
                    startActivity(intent);
                }
                break;
            }

        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speech_to_text, menu);
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
}
