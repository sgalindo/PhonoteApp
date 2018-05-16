package cmps121.phonote;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.graphics.Matrix;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;


public class RootMenu extends AppCompatActivity {

    private Button imgToTxt;

    private int REQ_CODE_CAMERA = 1;
    private int REQ_CODE_CROP = 2;
    private Uri HQimageUri; //needed to get high quality image instead of thumbnail
    private File imageFile; //hol

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);

        //Cheeky fix needed to avoid Uri crashes.
        //Disables the check for security risk if other apps can access the Uri. we don't care if this happens
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

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
        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFile = getOutputMediaFile();
                HQimageUri = Uri.fromFile(imageFile);
                Intent goToCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); //
                goToCamera.putExtra(MediaStore.EXTRA_OUTPUT, HQimageUri);
                startActivityForResult(goToCamera, REQ_CODE_CAMERA);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_CAMERA) { //If returning from camera
                //Uri pic = data.getData(); //gets the picture that was returned to pass to the cropper


                if (imageFile == null) {
                    Toast.makeText(this, "No Valid file path. Picture not saved", Toast.LENGTH_LONG).show();
                    return; //No picture could be saved
                }

                BitmapFactory.Options options = new BitmapFactory.Options(); //used to set bitmap options (specifically to lower image file size)
                options.inSampleSize = 2; //reduces file size to avoid out of memory issues (power of 2. 2 = 1/2 image size, 4 = 1/4 image size

                Bitmap bitmap = BitmapFactory.decodeFile(HQimageUri.getPath(),options);
                bitmap = editImage.rotateBitmap(bitmap,90); //automatically rotates the image 90degrees as I found all images started sideways

                try {
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    Intent cropIntent = new Intent(RootMenu.this, editImage.class); //intent to move to the crop activity
                    cropIntent.putExtra("image",imageFile.getAbsolutePath()); //adds the image location to be passed to the crop
                    startActivityForResult(cropIntent,REQ_CODE_CROP);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (requestCode == REQ_CODE_CROP) {// If returning from crop

            }
        }
    }

    protected void onResume() {
        super.onResume();
        SignInButton signIn = findViewById(R.id.sign_in_button);
        signIn.setVisibility(View.INVISIBLE);
        Button signOut = findViewById(R.id.sign_out_button);
        signOut.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        super.onStart();
        final GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            SignInButton signIn = findViewById(R.id.sign_in_button);
            signIn.setVisibility(View.INVISIBLE);
            Button signOut = findViewById(R.id.sign_out_button);
            signOut.setVisibility(View.VISIBLE);
        } else {
            SignInButton signIn = findViewById(R.id.sign_in_button);
            signIn.setVisibility(View.VISIBLE);
            Button signOut = findViewById(R.id.sign_out_button);
            signOut.setVisibility(View.INVISIBLE);
        }

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


        imgToTxt = (Button) findViewById(R.id.button_image_to_text);
        imgToTxt.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
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
        String timeStamp = Long.toString(System.currentTimeMillis() / 1000); //gets a timestamp for a unique photo name

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png"); // Create a media file name
        return mediaFile;
    }


}


