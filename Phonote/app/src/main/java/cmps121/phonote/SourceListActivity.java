package cmps121.phonote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class SourceListActivity extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray jsonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);
    }

    protected void onResume() {
        super.onResume();
        ListView listView = findViewById(R.id.source_list_view);
        TextView text = findViewById(R.id.textView_empty);
        text.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        final String rootPath = getFilesDir().getAbsolutePath() + "/projects/" + name + "/sources/";

        jo = null;
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

            final ArrayList<SourceData> sourceArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                SourceData data = new SourceData();
                try {
                    data.title = jsonArray.getJSONObject(i).getString("title");
                    data.author = jsonArray.getJSONObject(i).getString("author");
                    data.publisher = jsonArray.getJSONObject(i).getString("publisher");
                    data.city = jsonArray.getJSONObject(i).getString("city");
                    data.year = jsonArray.getJSONObject(i).getString("year");
                }
                catch (JSONException je) {
                    je.printStackTrace();
                }
                sourceArrayList.add(data);
            }

            String[] listTitles = new String[sourceArrayList.size()];

            for (int i = 0; i < sourceArrayList.size(); i++) {
                SourceData source = sourceArrayList.get(i);
                listTitles[i] = source.title;
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listTitles);
            listView.setAdapter(adapter);

            final Context context = this;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SourceData selected = sourceArrayList.get(position);

                    Intent viewSourceIntent = new Intent(context, ViewSourceActivity.class);

                    viewSourceIntent.putExtra("title",     selected.title);
                    viewSourceIntent.putExtra("author",    selected.author);
                    viewSourceIntent.putExtra("publisher", selected.publisher);
                    viewSourceIntent.putExtra("city",      selected.city);
                    viewSourceIntent.putExtra("year",      selected.year);
                    viewSourceIntent.putExtra("pos",       position);

                    startActivity(viewSourceIntent);
                }
            });
        }
        catch (IOException e) {
            listView.setEnabled(false);
            listView.setVisibility(View.INVISIBLE);

            text.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_source_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add_source:
                Intent createSourceIntent = new Intent(this, CreateSourceActivity.class);
                Bundle bundle = getIntent().getExtras();
                String name = bundle.getString("name");
                createSourceIntent.putExtra("name", name);
                startActivity(createSourceIntent);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

}
