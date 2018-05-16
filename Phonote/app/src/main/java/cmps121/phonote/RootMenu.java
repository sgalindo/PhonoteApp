package cmps121.phonote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;
import android.text.InputType;
import java.io.File;


public class RootMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File projects = new File(getFilesDir(), "/projects");
        boolean success = true;
        if(!projects.exists()) {
            success = projects.mkdirs();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        //Click activity for camera button
        ImageButton btnCamera =  findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCamera = new Intent(RootMenu.this, takePicture.class); //MediaStore.ACTION_IMAGE_CAPTURE
                startActivity(goToCamera);
            }
        });
        Button new_project = (Button) findViewById(R.id.new_project);
        new_project.setOnClickListener(new Button.OnClickListener() {
            private String new_name = "";
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RootMenu.this);
                builder.setTitle("New Project");

                final EditText name_input = new EditText(RootMenu.this);
                name_input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(name_input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        new_name = name_input.getText().toString();
                        boolean projectCreated = true;
                        Intent projectMenu = new Intent(RootMenu.this, projectMenu.class);
                        projectMenu.putExtra("name_of_project", new_name);
                        projectMenu.putExtra("projectCreated", projectCreated);
                        RootMenu.this.startActivity(projectMenu);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which){
                       dialog.cancel();
                   }
                });
                builder.show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_root_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
