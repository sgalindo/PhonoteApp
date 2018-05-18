package cmps121.phonote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class projectMenu extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_menu);
        Bundle bundle = getIntent().getExtras();

        ListView lv;
        ArrayAdapter<String> arrayAdapter;
        final Intent goToRootMenuForProject = new Intent(this, RootMenu.class);
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
                        boolean projectCreated = true;
                        Intent projectMenu = new Intent(projectMenu.this, projectMenu.class);
                        projectMenu.putExtra("name_of_project", new_name);
                        projectMenu.putExtra("projectCreated", projectCreated);
                        projectMenu.this.startActivity(projectMenu);

                        // ideally this should got to the Root Menu of the New Project

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

        if (bundle != null)
        {
            if (bundle.getBoolean("projectCreated")) {
                final String name_of_project = bundle.getString("name_of_project");
                File new_file = new File("/projects", "/" + name_of_project);
                boolean success = true;
                if (!new_file.exists()) {
                    success = new_file.mkdirs();
                    File citations = new File("/" + name_of_project, "/citations");
                    citations.mkdirs();
                    File notes = new File("/" + name_of_project, "/notes");
                    notes.mkdirs();

                }

                TextView project1 = (TextView) findViewById(R.id.project1);
                project1.setText("-" + name_of_project);
                project1.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //goToRootMenuForProject.putExtra("ProjectName", name_of_project);
                        goToRootMenuForProject.putExtra("name_of_project", name_of_project);
                        startActivity(goToRootMenuForProject);
                    }


                });
            }
        }
    }
//    //https://stackoverflow.com/questions/9530921/list-all-the-files-from-all-the-folder-in-a-single-list
//    private List<File> getListFiles(File parentDir){
//        ArrayList<File> inFiles= new ArrayList<File>();
//        File[] files = parentDir.listFiles();
//        for (File file : files){
//            if (file.isDirectory()){
//                inFiles.add(file);
//            }
//        }
//        return inFiles;
//    }
}
