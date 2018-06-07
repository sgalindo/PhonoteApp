package cmps121.phonote;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.List;

public class ViewSourceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_source);

        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("name");
        final String rootPath = getFilesDir().getAbsolutePath() + "/projects/" + name + "/sources/";
        final Intent i = getIntent();

        final int position = i.getIntExtra("pos", -1);

        final String title =       i.getStringExtra("title");
        final String author =      i.getStringExtra("author");
        final String publisher =   i.getStringExtra("publisher");
        final String city =        i.getStringExtra("city");
        final String year =        i.getStringExtra("year");
        final String citation =    i.getStringExtra("citation");

        final TextView vTitle = findViewById(R.id.view_Title);
        final TextView vAuthor = findViewById(R.id.view_Author);
        final TextView vPublisher = findViewById(R.id.view_Publisher);
        final TextView vCity = findViewById(R.id.view_City);
        final TextView vYear = findViewById(R.id.view_Year);
        final TextView vCitation = findViewById(R.id.view_Citation);

        vTitle.setText(title);
        vAuthor.setText(author);
        vPublisher.setText(publisher);
        vCity.setText(city);
        vYear.setText(year);
        vCitation.setText(citation);

        Button deleteButton = (Button) findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = null;
                JSONObject jo = null;
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
                    catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    jsonArray = remove(position, jsonArray);
                    jo.remove("data");
                    try {
                        jo.put("data", jsonArray);
                    }
                    catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
                catch (NullPointerException n) {
                    n.printStackTrace();
                }

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

                Intent in = new Intent(ViewSourceActivity.this, SourceListActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("name", name);
                startActivity(in);
            }
        });

        Button editButton = (Button) findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ViewSourceActivity.this, CreateSourceActivity.class);
                in.putExtra("name", name);
                in.putExtra("title", title);
                in.putExtra("author", author);
                in.putExtra("publisher", publisher);
                in.putExtra("city", city);
                in.putExtra("year", year);
                in.putExtra("pos", position);
                startActivity(in);
            }
        });

    }

    // These two methods are used to remove an item from a JSONArray since we are using API 15
    // and it requires at least API 19
    // https://gist.github.com/emmgfx/0f018b5acfa3fd72b3f6
    // ---------------------------------------------------------------------------------------------
    public static JSONArray remove(final int idx, final JSONArray from) {
        final List<JSONObject> objs = asList(from);
        objs.remove(idx);

        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            ja.put(obj);
        }

        return ja;
    }

    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }
    // ---------------------------------------------------------------------------------------------
}
