package cmps121.phonote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("name");
        Log.d("GOTEM", "Create"+name);

        Intent i = getIntent();
        String title =       i.getStringExtra("title");
        String author =      i.getStringExtra("author");
        String publisher =   i.getStringExtra("publisher");
        String city =        i.getStringExtra("city");
        String year =        i.getStringExtra("year");

        EditText titleText = findViewById(R.id.editText_Title);
        EditText authorText = findViewById(R.id.editText_Author);
        EditText publisherText = findViewById(R.id.editText_Publisher);
        EditText cityText = findViewById(R.id.editText_City);
        EditText yearText = findViewById(R.id.editText_Year);

        titleText.setText(title);
        authorText.setText(author);
        publisherText.setText(publisher);
        cityText.setText(city);
        yearText.setText(year);

        Button saveBtn = findViewById(R.id.btn_Save);
        Button cancelBtn = findViewById(R.id.btn_Cancel);

        final String rootPath = getFilesDir().getAbsolutePath() + "/projects/" + name + "/sources/";
        try {
            File f = new File(rootPath + "sources.ser");
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
                    File f = new File(rootPath + "sources.ser");
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
                sourceListIntent.putExtra("name", name);
                startActivity(sourceListIntent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView viewTitle = findViewById(R.id.textView_CreateSourceTitle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(viewTitle.getText());
    }

    public JSONObject createSourceManual() {
        JSONObject source = new JSONObject();
        EditText titleText = findViewById(R.id.editText_Title);
        EditText authorText = findViewById(R.id.editText_Author);
        EditText publisherText = findViewById(R.id.editText_Publisher);
        EditText cityText = findViewById(R.id.editText_City);
        EditText yearText = findViewById(R.id.editText_Year);

        SpannableStringBuilder citation = new SpannableStringBuilder();
        if (!authorText.getText().toString().equals("")) {
            citation.append(authorText.getText().toString());
            citation.append(". ");
        }
        SpannableStringBuilder sb = new SpannableStringBuilder(titleText.getText().toString());
        StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC);
        sb.setSpan(iss, 0, titleText.getText().toString().length()-1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        citation.append(sb);
        citation.append(". ");
        citation.append(cityText.getText().toString());
        citation.append(": ");
        citation.append(publisherText.getText().toString());
        citation.append(", ");
        citation.append(yearText.getText().toString());
        citation.append(". Print.");


        try {
            source.put("title", titleText.getText().toString());
            source.put("author", authorText.getText().toString());
            source.put("publisher", publisherText.getText().toString());
            source.put("city", cityText.getText().toString());
            source.put("year", yearText.getText().toString());
            source.put("citation", citation.toString());
        }
        catch (JSONException je) {
            je.printStackTrace();
        }

        return source;
    }
}
