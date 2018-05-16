package cmps121.phonote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.Task;

public class RootMenu extends AppCompatActivity {

    private ImageButton imgToTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_root_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();


        ImageButton createSourceBtn = findViewById(R.id._createSource);
        createSourceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createSourceIntent = new Intent(RootMenu.this,
                        CreateSourceActivity.class);
                startActivity(createSourceIntent);
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





        SignInButton signIn = findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int RC_SIGN_IN = 100;
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                Log.d("GOOGLE SIGN IN BUTTON", "Does it load the sign in activity?");

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null){
            SignInButton signIn = findViewById(R.id.sign_in_button);
            signIn.setVisibility(View.INVISIBLE);
            Button signOut = findViewById(R.id.sign_out_button);
            signOut.setVisibility(View.VISIBLE);
        }
        else {
            SignInButton signIn = findViewById(R.id.sign_in_button);
            signIn.setVisibility(View.VISIBLE);
            Button signOut = findViewById(R.id.sign_out_button);
            signOut.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        super.onStart();
        final GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        final Button signOut = findViewById(R.id.sign_out_button);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                SignInButton signIn = findViewById(R.id.sign_in_button);
                signIn.setVisibility(View.VISIBLE);
                Button signOut = findViewById(R.id.sign_out_button);
                signOut.setVisibility(View.INVISIBLE);
            }
        });


        imgToTxt = (ImageButton) findViewById(R.id.button_image_to_text);
        imgToTxt.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent intent = new Intent(getApplicationContext(), ImageToText.class);
                        startActivity(intent);
                    }
                }
        );
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
