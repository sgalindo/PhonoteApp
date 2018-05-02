package cmps121.phonote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SourceListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);

        Bundle sourceListBundle = getIntent().getExtras();
        ArrayList<SourceData> sourceList =
                (ArrayList<SourceData>) sourceListBundle.getSerializable("source_list");

        String[] sourceArray = new String[sourceList.size()];
        for (int i = 0; i < sourceList.size(); i++) {
            SourceData data = sourceList.get(i);
            sourceArray[i] = data.title;
        }

        ArrayAdapter adapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, sourceArray);

        ListView sourceListView = findViewById(R.id.source_list_view);
        sourceListView.setAdapter(adapter);
    }
}
