package cmps121.phonote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.File;

public class projectMenu extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_menu);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getBoolean("projectCreated")) {
            String name_of_project = bundle.getString("name_of_project");
            File new_file = new File("/projects", "/" + name_of_project);
            boolean success = true;
            if (!new_file.exists()) {
                success = new_file.mkdirs();
                File citations = new File("/"+name_of_project, "/citations");
                citations.mkdirs();
                File notes = new File("/"+name_of_project,"/notes");
                notes.mkdirs();
            }
            TextView project1 = (TextView) findViewById(R.id.project1);
            project1.setText("-"+name_of_project);
        }
    }
}
