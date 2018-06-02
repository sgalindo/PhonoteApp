package cmps121.phonote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewNotes extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray jsonArray = null;

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
        //text.setVisibility(View.INVISIBLE);
    }
}
