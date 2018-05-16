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
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Context;
import java.io.*;
import java.util.ArrayList;

public class projectMenu extends AppCompatActivity{
    public JSONObject boy = null;
    public JSONArray boys = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_menu);
        Bundle bundle = getIntent().getExtras();
        ListView list = findViewById(R.id.list_boy);
        TextView empty_text = (findViewById(R.id.empty_text));
        empty_text.setVisibility(View.INVISIBLE);
        try {
            File f = new File("/projects", "project_names.ser");
            FileInputStream file_in = new FileInputStream(f);
            ObjectInputStream object_in = new ObjectInputStream(file_in);
            String input = null;
            try {
                input = (String) object_in.readObject();
            } catch (ClassNotFoundException c) {
                c.printStackTrace();
            }
            try {
                boy = new JSONObject(input);
                boys = boy.getJSONArray("data");
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            final ArrayList<ListData> aList = new ArrayList<ListData>();
            for(int i = 0; i < boys.length(); i++){
                ListData dataBoy = new ListData();
                try{
                    dataBoy.name = boys.getJSONObject(i).getString("name");
                }
                catch(JSONException e1){
                    e1.printStackTrace();
                }
                aList.add(dataBoy);
            }
            String[] listItems = new String[aList.size()];
            for(int i = 0; i < aList.size(); i++){
                ListData listD = aList.get(i);
                listItems[i] = listD.name;
            }
            if(aList.size() <= 0){
                empty_text.setVisibility(View.VISIBLE);
            }
            else{
                empty_text.setVisibility(View.INVISIBLE);
            }
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
            list.setAdapter(adapter);

            final Context context = this;
        }
        catch(IOException e){
            list.setEnabled(false);
            list.setVisibility(View.INVISIBLE);
            empty_text.setVisibility(View.VISIBLE);
        }
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
            String name = name_of_project;
            JSONObject temp = new JSONObject();
            try{
                temp.put("name", name);
            }
            catch(JSONException j){
                j.printStackTrace();
            }
            boys.put(temp);
            try{
                File g = new File("/projects", "/project_names.ser");
                FileOutputStream file_out = new FileOutputStream(g);
                ObjectOutputStream object_out = new ObjectOutputStream(file_out);
                String j = boy.toString();
                object_out.writeObject(j);
                object_out.close();
                file_out.close();
            }
            catch(IOException e2){
                //do nothing
            }
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        Bundle bundle = getIntent().getExtras();

    }
}
