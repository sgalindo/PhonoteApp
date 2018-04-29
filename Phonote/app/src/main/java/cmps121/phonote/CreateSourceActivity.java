package cmps121.phonote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class CreateSourceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_source);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayList<SourceData> sourceList = new ArrayList<>();

        Button saveBtn = findViewById(R.id.btn_Save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceData newSource = createSourceManual();
                sourceList.add(newSource);
                Intent viewSourceDataIntent = new Intent(CreateSourceActivity.this,
                        viewSourceActivity.class);
                viewSourceDataIntent.putExtra("source", newSource);
                /*Bundle sourceListBundle = new Bundle();
                sourceListBundle.putSerializable("source_list", sourceList);
                viewSourceListIntent.putExtras(sourceListBundle);*/
                startActivity(viewSourceDataIntent);
            }
        });
    }

    public SourceData createSourceManual() {
        EditText titleText = findViewById(R.id.editText_Title);
        EditText authorText = findViewById(R.id.editText_Author);
        EditText publisherText = findViewById(R.id.editText_Publisher);
        EditText cityText = findViewById(R.id.editText_City);
        EditText yearText = findViewById(R.id.editText_Year);

        return new SourceData(
                titleText.getText().toString(),
                authorText.getText().toString(),
                publisherText.getText().toString(),
                cityText.getText().toString(),
                yearText.getText().toString()
        );
    }
}
