package cmps121.phonote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CreateSourceActivity extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray jsonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_source);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayList<SourceData> sourceList = new ArrayList<>();

        Button saveBtn = findViewById(R.id.btn_Save);

        try {
            File f = new File(getFilesDir(), "sources.ser");
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
            catch (JSONException je){
                je.printStackTrace();
            }
        }
        catch (IOException e) {
            jo = new JSONObject();
            jsonArray = new JSONArray();
            try {
                jo.put("data", jsonArray);
            }
            catch (JSONException je) {
                je.printStackTrace();
            }
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject newSource = createSourceManual();
                jsonArray.put(newSource);

                try {
                    File f = new File(getFilesDir(), "sources.ser");
                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream o = new ObjectOutputStream(fo);
                    String j = jo.toString();
                    o.writeObject(j);
                    o.close();
                    fo.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Intent sourceListIntent = new Intent(CreateSourceActivity.this, SourceListActivity.class);
                sourceListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(sourceListIntent);
            }
        });
    }

    public JSONObject createSourceManual() {
        JSONObject source = new JSONObject();
        EditText titleText = findViewById(R.id.editText_Title);
        EditText authorText = findViewById(R.id.editText_Author);
        EditText publisherText = findViewById(R.id.editText_Publisher);
        EditText cityText = findViewById(R.id.editText_City);
        EditText yearText = findViewById(R.id.editText_Year);

        try {
            source.put("title", titleText.getText().toString());
            source.put("author", authorText.getText().toString());
            source.put("publisher", publisherText.getText().toString());
            source.put("city", cityText.getText().toString());
            source.put("year", yearText.getText().toString());
        }
        catch (JSONException je) {
            je.printStackTrace();
        }

        return source;
    }
}