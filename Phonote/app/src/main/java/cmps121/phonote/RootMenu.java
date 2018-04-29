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
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;
import android.text.InputType;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.Task;

public class RootMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();

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
                        Intent projectMenu = new Intent(RootMenu.this, projectMenu.class);
                        projectMenu.putExtra("name_of_project", new_name);
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



        SignInButton signIn = findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int RC_SIGN_IN = 100;
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }




    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        super.onStart();

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        if (task){
            SignInButton signIn = findViewById(R.id.sign_in_button);
            signIn.setVisibility(0);
        }
        else {
            SignInButton signIn = findViewById(R.id.sign_in_button);
            signIn.setVisibility(1);


        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
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

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }
}
