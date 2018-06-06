package cmps121.phonote;

import android.app.AlertDialog;
import android.os.Environment;
import android.util.Log;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class projectMenu extends AppCompatActivity{
    public JSONObject boy = null;
    public JSONArray boys = null;
    int listSize;
    String listItems[];
    private static final String TAG = "projectMenu";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_menu);
        //Bundle bundle = getIntent().getExtras();

        //ListView lv;

    }
    protected void onResume(){
        super.onResume();

        ArrayAdapter<String> arrayAdapter;
        final Intent goToRootMenuForProject = new Intent(this, RootMenu.class);
        File projects = new File(getFilesDir(), "projects");
        boolean success = true;
        if(!projects.exists()) {
            success = projects.mkdirs();
        }
/*
        List<File> files = getListFiles(new File(getCacheDir(), "/projects"));
        //https://stackoverflow.com/questions/5070830/populating-a-listview-using-an-arraylist
        lv = (ListView) findViewById(R.id.projects_list);
        List<String> fileNameList = new ArrayList<String>();
        for (File file: files){
            fileNameList.add(file.getName());
        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNameList);
        lv.setAdapter(arrayAdapter);
*/
        ListView list = findViewById(R.id.list_boy);
        TextView empty_text = (findViewById(R.id.empty_text));
        empty_text.setVisibility(View.INVISIBLE);

        try {
            Log.d(TAG, "in first try");
            File f = new File(getFilesDir(), "project_names.ser");
            FileInputStream file_in = new FileInputStream(f);
            ObjectInputStream object_in = new ObjectInputStream(file_in);
            String input = null;
            try {
                Log.d(TAG, "in second try");
                input = (String) object_in.readObject();
            } catch (ClassNotFoundException c) {
                c.printStackTrace();
                Log.d(TAG, "in second catch");
            }
            try {
                Log.d(TAG, "in third try");
                boy = new JSONObject(input);
                boys = boy.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "in third try");
            }
            final ArrayList<ListData> aList = new ArrayList<ListData>();
            Log.d(TAG, "created ArrayList");
            for (int i = 0; i < boys.length(); i++) {
                Log.d(TAG, "inside for loop");
                ListData dataBoy = new ListData();
                try {
                    dataBoy.name = boys.getJSONObject(i).getString("name");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                aList.add(dataBoy);
                Log.d(TAG, "Showing list");
            }
            listItems = new String[aList.size()];
            listSize = aList.size();
            for (int i = 0; i < aList.size(); i++) {
                ListData listD = aList.get(i);
                listItems[i] = listD.name;
            }
            if (aList.size() <= 0) {
                empty_text.setVisibility(View.VISIBLE);
            } else {
                empty_text.setVisibility(View.INVISIBLE);
            }
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
            list.setAdapter(adapter);

            final Context context = this;
            list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListData selected = aList.get(position);
                    Intent RootMenu = new Intent(context, RootMenu.class);
                    RootMenu.putExtra("name", selected.name);
                    RootMenu.putExtra("position", position);
                    startActivity(RootMenu);
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "in first catch");
            boy = new JSONObject();
            boys = new JSONArray();
            try{
                boy.put("data", boys);
            }
            catch(JSONException input){
                input.printStackTrace();
            }
            list.setEnabled(false);
            list.setVisibility(View.INVISIBLE);
            empty_text.setVisibility(View.VISIBLE);
        }


        Button new_project = (Button) findViewById(R.id.new_project);
        new_project.setOnClickListener(new Button.OnClickListener() {
            private String new_name = "";

            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(projectMenu.this);
                builder.setTitle("New Project");

                final EditText name_input = new EditText(projectMenu.this);
                name_input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(name_input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new_name = name_input.getText().toString();
                        boolean duplicate = false;
                        Log.d(TAG, "listSize= "+listSize);
                        for(int i = 0; i < listSize; i++){
                            Log.d(TAG, "listItems[i]= "+ listItems[i]);
                            Log.d(TAG, "new_name: "+ new_name);
                            if(listItems[i].equals(new_name)){
                                Log.d(TAG, "duplicate is true");
                                duplicate = true;
                            }
                        }
                        if(duplicate == true){
                            Context context = getApplicationContext();
                            CharSequence text = "Project named: \""+new_name +"\" already exists!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            return;
                        }
                        //boolean projectCreated = true;
                        //Intent projectMenu = new Intent(projectMenu.this, projectMenu.class);
                        //projectMenu.putExtra("name_of_project", new_name);
                        //projectMenu.putExtra("projectCreated", projectCreated);
                        //projectMenu.this.startActivity(projectMenu);
                        JSONObject temp = new JSONObject();
                        try{
                            temp.put("name", new_name);
                            Log.d(TAG, "putting in name to temp: " + new_name);
                        }
                        catch(JSONException j){
                            j.printStackTrace();
                        }
                        boys.put(temp);
                        Log.d(TAG, "Putting name into boys");
                        try{
                            File f = new File(getFilesDir(), "project_names.ser");
                            FileOutputStream file_out = new FileOutputStream(f);
                            ObjectOutputStream object_out = new ObjectOutputStream(file_out);
                            String j = boy.toString();
                            object_out.writeObject(j);
                            Log.d(TAG, "writing to object");
                            object_out.close();
                            file_out.close();
                            String rootPath = getFilesDir().getAbsolutePath() + "/projects/";
                            File new_file = new File(rootPath + new_name);
                            boolean success = true;
                            if(!new_file.exists()){
                                success = new_file.mkdirs();
                                File citations = new File(rootPath + new_name + "/citations");
                                citations.mkdirs();
                                File notes = new File (rootPath + new_name + "/notes");
                                notes.mkdirs();
                                File sources = new File (rootPath + new_name + "/sources");
                                sources.mkdirs();
                            }

                        }
                        catch(IOException e) {
                            //do nothing
                        }
                        Intent RootMenu = new Intent(projectMenu.this, RootMenu.class);
                        RootMenu.putExtra("name", new_name);
                        startActivity(RootMenu);

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
