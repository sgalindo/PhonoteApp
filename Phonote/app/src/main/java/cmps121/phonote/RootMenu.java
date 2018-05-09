package cmps121.phonote;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RootMenu extends AppCompatActivity {

    private Button imgToTxt;

    private int REQ_CODE_CAMERA = 1;
    private int REQ_CODE_CROP = 2;
    private Uri HQimageUri = null; //needed to get high quality image instead of thumbnail

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();


        Button createSourceBtn = findViewById(R.id.btn_createSource);
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

                HQimageUri = Uri.fromFile(getOutputMediaFile());
                Intent goToCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); //
                //goToCamera.putExtra(MediaStore.EXTRA_OUTPUT, HQimageUri);
                startActivityForResult(goToCamera,REQ_CODE_CAMERA);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQ_CODE_CAMERA){ //If returning from camera
                //Uri pic = data.getData(); //gets the picture that was returned to pass to the cropper
                //cropImage(pic);  BROKEN FOR NOW. FIX THIS TO ALLOW CROPPING

                File pictureFile = getOutputMediaFile(); //gets file location and name that the photo will be saved as
                if (pictureFile == null) return;
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                }  catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if(requestCode == REQ_CODE_CROP) {// If returning from crop

            }
        }
    }

    public void cropImage(Uri pic){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(pic, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQ_CODE_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        super.onStart();

//        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//        if (task){
//           SignInButton signIn = findViewById(R.id.sign_in_button);
//            signIn.setVisibility(0);
//        }
//        else {
//            SignInButton signIn = findViewById(R.id.sign_in_button);
//            signIn.setVisibility(1);}

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);

        imgToTxt = (Button) findViewById(R.id.button_image_to_text);
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

    //gets the media location to save photos and returns the media file that the photo will be saved ass
    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Phonote");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Phonote", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = Long.toString (System.currentTimeMillis()/1000);

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".png");

        return mediaFile;
    }

}


