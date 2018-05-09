package cmps121.phonote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;

import java.io.File;

public class BootUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boot_up_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File projects = new File(getCacheDir(), "/projects");
        boolean success = true;
        if (!projects.exists()) {
            success = projects.mkdirs();
        }


        Button new_project = (Button) findViewById(R.id.new_project);
        new_project.setOnClickListener(new Button.OnClickListener() {
            private String new_name = "";

            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BootUpActivity.this);
                builder.setTitle("New Project");

                final EditText name_input = new EditText(BootUpActivity.this);
                name_input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(name_input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new_name = name_input.getText().toString();
                        boolean projectCreated = true;
                        Intent projectMenu = new Intent(BootUpActivity.this, projectMenu.class);
                        projectMenu.putExtra("name_of_project", new_name);
                        projectMenu.putExtra("projectCreated", projectCreated);
                        BootUpActivity.this.startActivity(projectMenu);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });

    }
}
