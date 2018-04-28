package cmps121.phonote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageToText extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonProcess;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        imageView = (ImageView) findViewById(R.id.image_view_Img2Txt);
        buttonProcess = (Button) findViewById(R.id.button_process);
    }
}
