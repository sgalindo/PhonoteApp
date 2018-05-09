package cmps121.phonote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class viewSourceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_source);

        TextView vTitle = findViewById(R.id.view_Title);
        TextView vAuthor = findViewById(R.id.view_Author);
        TextView vPublisher = findViewById(R.id.view_Publisher);
        TextView vCity = findViewById(R.id.view_City);
        TextView vYear = findViewById(R.id.view_Year);

        SourceData source = (SourceData) getIntent().getSerializableExtra("source");

        vTitle.setText(source.getTitle());
        vAuthor.setText(source.getAuthor());
        vPublisher.setText(source.getPublisher());
        vCity.setText(source.getCity());
        vYear.setText(source.getYear());

    }
}
