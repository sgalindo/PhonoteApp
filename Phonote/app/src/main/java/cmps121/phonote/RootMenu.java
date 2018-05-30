package cmps121.phonote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Matrix;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class RootMenu extends AppCompatActivity {
    public JSONObject boy = null;
    public JSONArray boys = null;

    private ImageButton imgToTxt;

    private int REQ_CODE_CAMERA = 1;
    private int REQ_CODE_CROP = 2;
    private Uri HQimageUri; //needed to get high quality image instead of thumbnail
    private File imageFile; //hol

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        setContentView(R.layout.content_root_menu);

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

        setContentView(R.layout.content_root_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        TextView project_name = (TextView) findViewById(R.id.name_of_project);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            project_name.setTooltipText(bundle.getCharSequence("name_of_project"));
        }
        String name = bundle.getString("name");
        final int position = bundle.getInt("position");
        final String fileName = bundle.getString("name");
        if (name.length() > 15){
            name = name.substring(0, 13) + "...";
        }

        project_name.setText(name);

        setSupportActionBar(toolbar);
        final GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();

        final String finalName = name;
        ImageButton sourceListBtn = findViewById(R.id._createSource);
        sourceListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sourceListIntent = new Intent(RootMenu.this,
                        SourceListActivity.class);
                sourceListIntent.putExtra("name", finalName);
                startActivity(sourceListIntent);
            }
        });

        ImageButton returnToProjects = findViewById(R.id.go_to_projects);
        returnToProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Click activity for camera button
        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFile = getOutputMediaFile(); //gets image file location to save pic at
                HQimageUri = Uri.fromFile(imageFile); //gets Uri from image file to save HQ image
                Intent goToCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //
                goToCamera.putExtra(MediaStore.EXTRA_OUTPUT, HQimageUri);
                startActivityForResult(goToCamera, REQ_CODE_CAMERA);
            }
        });

        imgToTxt = (ImageButton) findViewById(R.id.button_image_to_text);
        imgToTxt.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ImageToText.class);
                        intent.putExtra("name", finalName);
                        startActivity(intent);
                    }
                }
        );


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

        Button delete = (Button) findViewById(R.id.DeleteButton);
        delete.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(RootMenu.this);
                builder.setTitle("Delete Project?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        String rootPath = getFilesDir().getAbsolutePath()+"/projects/" + fileName;
                        File dir = new File(rootPath);
                        deleteDirectory(dir);
                        try{
                            File f = new File(getFilesDir(), "project_names.ser");
                            FileInputStream file_in = new FileInputStream(f);
                            ObjectInputStream object_in = new ObjectInputStream(file_in);
                            String input = null;
                            try{
                                input = (String) object_in.readObject();
                            }
                            catch(ClassNotFoundException c){
                                c.printStackTrace();
                            }
                            try{
                                boy = new JSONObject(input);
                                boys = boy.getJSONArray("data");
                            }
                            catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                        catch(IOException e){
                            //do nothing
                        }
                        boys.remove(position);
                        try{
                            File f = new File(getFilesDir(), "project_names.ser");
                            FileOutputStream file_out = new FileOutputStream(f);
                            ObjectOutputStream object_out = new ObjectOutputStream(file_out);
                            String j = boy.toString();
                            object_out.writeObject(j);
                            object_out.close();
                            file_out.close();
                        }
                        catch(IOException e){
                            //do nothing
                        }
                        Intent projectMenu = new Intent(RootMenu.this, projectMenu.class);
                        projectMenu.putExtra("deleted", true);
                        projectMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(projectMenu);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener(){
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

                Bitmap bitmap = BitmapFactory.decodeFile(HQimageUri.getPath(), options);
                bitmap = editImage.rotateBitmap(bitmap, 90); //automatically rotates the image 90degrees as I found all images started sideways

                try {
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    Intent cropIntent = new Intent(RootMenu.this, editImage.class); //intent to move to the crop activity
                    cropIntent.putExtra("image", imageFile.getAbsolutePath()); //adds the image location to be passed to the crop
                    startActivityForResult(cropIntent, REQ_CODE_CROP);
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

    //Helper function to delete directories
    boolean deleteDirectory(File directoryToBeDeleted){
        File[] allContents = directoryToBeDeleted.listFiles();
        if(allContents != null){
            for(File file : allContents){
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}


