package cmps121.phonote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ViewNotes extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray jsonArray = null;
    static private String TAG = "ViewNotes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView listView = findViewById(R.id.notes_list_view);
        TextView text = findViewById(R.id.textView_empty_Notes);
        text.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("name");
        final String rootPath = getFilesDir().getAbsolutePath() + "/projects/" + name + "/notes/";

        jo = null;
        // Try to read the file if it doesn't exist then display the empty notes text view
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
            catch (JSONException je){
                Log.e(TAG, "Couldn't read JsonString");
                je.printStackTrace();
            }


            //create a string array with the titles values
            final ArrayList<NotesData> notesArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                NotesData data = new NotesData();
                try {
                    data.title = jsonArray.getJSONObject(i).getString("title");
                    data.text = jsonArray.getJSONObject(i).getString("text");

                }
                catch (JSONException je) {
                    Log.e(TAG, "Couldn't create the NotesData object in ViewNotes.class");
                    je.printStackTrace();
                }
                notesArrayList.add(data);
            }

            String[] listTitles = new String[notesArrayList.size()];
            if(listTitles.length < 1){
                listView.setVisibility(View.INVISIBLE);

                text.setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < notesArrayList.size(); i++) {
                NotesData note = notesArrayList.get(i);
                listTitles[i] = note.title;
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listTitles);
            listView.setAdapter(adapter);
            Log.i(TAG, "Notes have been loaded into the List View");

            final Context context = this;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    NotesData selected = notesArrayList.get(position);

                    Intent viewNotesIntent = new Intent(context, ExamineNoteActivity.class);

                    viewNotesIntent.putExtra("title",     selected.title);
                    viewNotesIntent.putExtra("text",      selected.text);
                    viewNotesIntent.putExtra("project",   name);
                    viewNotesIntent.putExtra("pos",       position);

                    startActivity(viewNotesIntent);
                }
            });

        }catch (FileNotFoundException e){
            listView.setEnabled(false);
            listView.setVisibility(View.INVISIBLE);

            text.setVisibility(View.VISIBLE);
            Log.i(TAG, "FileNotFoundError in onResume");
        }catch (IOException e){
            listView.setEnabled(false);
            listView.setVisibility(View.INVISIBLE);

            text.setVisibility(View.VISIBLE);
            Log.i(TAG, "IOException in onResume");
        }
    }
}
