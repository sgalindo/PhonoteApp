package cmps121.phonote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ExamineNoteActivity extends AppCompatActivity {

    final private String TAG = "ExamineNoteActivity";
    EditText titleView;
    EditText textView;
    Button saveEdit;
    Button deleteEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_note);

        // Get information from the intent----------------------------------------------------------
        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("project");
        final String rootPath = getFilesDir().getAbsolutePath() + "/projects/" + name + "/notes/";

        Intent i = getIntent();

        String title =       i.getStringExtra("title");
        String text  =       i.getStringExtra("text");
        final int position = i.getIntExtra("pos", -1);
        if(position == -1){
            Log.e(TAG, "position value -1, couldn't get position");
        }else{
            //Toast.makeText(getApplicationContext(), "Note Number: " + position, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Good So Far: position: "+ position);
        }

        // Get the views and fill them with Required text-------------------------------------------
        titleView = (EditText)findViewById(R.id.edit_note_title);
        textView  = (EditText)findViewById(R.id.edit_note_text);

        titleView.setText(title);
        textView.setText(text);

        // Define the Save button-------------------------------------------------------------------
        saveEdit = (Button)findViewById(R.id.save_edit_note);
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = null;
                JSONObject jo = null;

                // Open imageNotes.ser
                try {
                    File f = new File(rootPath + "imageNotes.ser");
                    FileInputStream fi = new FileInputStream(f);
                    ObjectInputStream o = new ObjectInputStream(fi);
                    String jsonString = null;

                    try {
                        jsonString = (String) o.readObject();
                    }
                    catch (ClassNotFoundException c) {
                        c.printStackTrace();
                    }
                    try {
                        jo = new JSONObject(jsonString);
                        jsonArray = jo.getJSONArray("data");
                    }
                    catch (JSONException je) {
                        je.printStackTrace();
                    }

                    // This will create an updated JSONArray with the new values from the user
                    JSONArray updatedArray = new JSONArray();
                    if(jsonArray != null){
                        for(int i = 0; i < jsonArray.length(); i++){
                            if( i != position){
                                try{
                                    updatedArray.put(jsonArray.get(i));
                                }catch(JSONException ex){
                                    Log.e(TAG, "couldn't get value from jsonArray.get()");
                                    finish();
                                }
                            }else{
                                JSONObject joTemp = new JSONObject();
                                try {
                                    joTemp.put("title", titleView.getText().toString());
                                    joTemp.put("text", textView.getText().toString());
                                } catch (JSONException e) {
                                    Log.e(TAG, "couldn't create new JSONObject to be saved");
                                    e.printStackTrace();
                                    finish();
                                }
                                updatedArray.put(joTemp);
                            }
                        }
                    }

                    // Replace the data inside of the JSONObject jo
                    try{
                        jo.put("data", updatedArray);
                    }catch (JSONException ex){
                        Log.e(TAG, "Couldn't update JSONObject jo with the updatedArray");
                    }

                    // Serialize the jsonObject
                    try {
                        f = new File(rootPath + "imageNotes.ser");
                        FileOutputStream fo = new FileOutputStream(f);
                        ObjectOutputStream oo = new ObjectOutputStream(fo);
                        String j = jo.toString();
                        oo.writeObject(j);
                        oo.close();
                        fo.close();

                        Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Couldn't save to imageNotes.ser file");
                    }

                }catch (FileNotFoundException e){
                    Log.e(TAG, "FileNotFoundException: imageNotes.ser doesn't exist yet");
                    e.printStackTrace();
                    finish();
                }catch (IOException e){
                    Log.e(TAG, "IOException: imageNotes.ser doesn't exist yet");
                    e.printStackTrace();
                    finish();
                }
            }
        });

        // Define Delete Button---------------------------------------------------------------------
        deleteEdit = (Button)findViewById(R.id.delete_edit_note);
        deleteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = null;
                JSONObject jo = null;

                // Open imageNotes.ser
                try {
                    File f = new File(rootPath + "imageNotes.ser");
                    FileInputStream fi = new FileInputStream(f);
                    ObjectInputStream o = new ObjectInputStream(fi);
                    String jsonString = null;

                    try {
                        jsonString = (String) o.readObject();
                    }
                    catch (ClassNotFoundException c) {
                        c.printStackTrace();
                    }
                    try {
                        jo = new JSONObject(jsonString);
                        jsonArray = jo.getJSONArray("data");
                    }
                    catch (JSONException je) {
                        je.printStackTrace();
                    }

                    // This will create an updated JSONArray without the selected note
                    JSONArray updatedArray = new JSONArray();
                    if(jsonArray != null){
                        for(int i = 0; i < jsonArray.length(); i++){
                            if( i != position){
                                try{
                                    updatedArray.put(jsonArray.get(i));
                                }catch(JSONException ex){
                                    Log.e(TAG, "couldn't get value from jsonArray.get()");
                                    finish();
                                }
                            }
                        }
                    }

                    // Replace the data inside of the JSONObject jo
                    try{
                        jo.put("data", updatedArray);
                    }catch (JSONException ex){
                        Log.e(TAG, "Couldn't update JSONObject jo with the updatedArray");
                    }

                    // Serialize the jsonObject
                    try {
                        f = new File(rootPath + "imageNotes.ser");
                        FileOutputStream fo = new FileOutputStream(f);
                        ObjectOutputStream oo = new ObjectOutputStream(fo);
                        String j = jo.toString();
                        oo.writeObject(j);
                        oo.close();
                        fo.close();

                        Toast.makeText(getApplicationContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Deleting note: Couldn't save to imageNotes.ser file");
                    }

                }catch (FileNotFoundException e){
                    Log.e(TAG, "FileNotFoundException: imageNotes.ser doesn't exist yet");
                    e.printStackTrace();
                    finish();
                }catch (IOException e){
                    Log.e(TAG, "IOException: imageNotes.ser doesn't exist yet");
                    e.printStackTrace();
                    finish();
                }
            }
        });

    }
}
