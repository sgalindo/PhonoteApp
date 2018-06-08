package cmps121.phonote;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

import static android.support.constraint.Constraints.TAG;
import static com.google.android.gms.drive.Drive.getDriveResourceClient;

public class DriveFolderManager {


    public void copyFilesFromLocalToDriveFolder(Activity activity, GoogleSignInAccount account,DriveFolder destination, File source){

        File read_file;

        Log.d("write-up", "copy function has been called for a drive folder:" + source.getAbsolutePath() + "    to    "+ destination);
        // delete all contentes of destination

        Query query = new Query.Builder().build();

        Task<MetadataBuffer> queryTask = getDriveResourceClient(activity, account).queryChildren(destination, query);
        // END query_children]
        queryTask
                .addOnSuccessListener(activity,
                        metadataBuffer -> {
                            if (metadataBuffer.getCount() > 0) {
                                for (Metadata metadata: metadataBuffer){
                                if (!metadata.isTrashable()) {
                                    Log.d("write-up", "some file is not trashable");
                                    break;
                                }

                                DriveResource driveResource = metadata.getDriveId().asDriveResource();
                                Task<Void> toggleTrashTask;

                                    toggleTrashTask = getDriveResourceClient(activity, account).trash(driveResource);
                                }

                            }
                        })
                .addOnFailureListener(activity, e -> {
                    Log.d("write-up", "Error retrieving files " );
                });




        //copy each file from contained int source to destination

        File[] files = source.listFiles();

        Log.d("write-up", "files : " + files.length);
        if (files != null){
        for(File file:files) {
            read_file = file;
            Log.d("file writing", file.getName());

            Log.d("write-up", "files exist to be copied");
            //https://developers.google.com/drive/android/create-file

            final Task<DriveFolder> rootFolderTask = getDriveResourceClient(activity, account).getRootFolder();
            final Task<DriveContents> createContentsTask = getDriveResourceClient(activity, account).createContents();
            File finalRead_file = read_file;
            Tasks.whenAll(rootFolderTask, createContentsTask)
                    .continueWithTask(task -> {
                        DriveFolder parent = rootFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();

                        FileInputStream fi = new FileInputStream(finalRead_file);

                        byte[] data = {0};
                        OutputStream outputStream = contents.getOutputStream();
                        try (Writer writer = new OutputStreamWriter(outputStream)) {
                            while (fi.read(data) != -1) {
                                writer.write(data[0]);
                                }

                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(file.getName())
                                .setMimeType("text/plain")
                                .build();

                        return getDriveResourceClient(activity, account).createFile(destination, changeSet, contents);
                    })
                    .addOnSuccessListener(activity,
                            driveFile -> {
                                Log.d("write-up", "Success adding file");
                            })
                    .addOnFailureListener(activity, e -> {
                        Log.d("write-up", e.getMessage());
                    });
        }
        }else {Log.d("write-up", "director list failed               files == null"); };
    }

    public void sync(Activity activity, GoogleSignInAccount account){


        CustomPropertyKey phonotePropertyKey =
            new CustomPropertyKey("phonote", CustomPropertyKey.PUBLIC);
        /*MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                      .setCustomProperty(approvalPropertyKey, "yes")
                                      .setCustomProperty(submitPropertyKey, "no")
                                      .build();
        */

        // create the PHONOTE folder if it doesn't exist in drive
        final DriveFolder[] phonote_dir = new DriveFolder[1];
        final DriveFolder[] project_dir = new DriveFolder[1];
        final DriveFolder[] citations_dir = new DriveFolder[1];
        final DriveFolder[] notes_dir = new DriveFolder[1];
        final DriveFolder[] sources_dir = new DriveFolder[1];

        final String project_name = activity.getIntent().getExtras().getString("name");

        Query q = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, "PHONOTE"))
                .build();
        Task<MetadataBuffer> queryTask= getDriveResourceClient(activity, account).query(q);
        Task<MetadataBuffer> metadataBufferTask = queryTask.addOnSuccessListener(activity, metadataBuffer -> {
            MetadataBuffer found = metadataBuffer;
            if (metadataBuffer.getCount() > 0) {
                Log.d("Syn called:", found.get(0).getTitle());
                phonote_dir[0] = found.get(0).getDriveId().asDriveFolder();
            } else {
                Log.d("Syn called:", "not results for PHONOTE");


                getDriveResourceClient(activity, account).getRootFolder().continueWithTask(task -> {
                    DriveFolder parent_folder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("PHONOTE").setMimeType(DriveFolder.MIME_TYPE).build();
                    return getDriveResourceClient(activity, account).createFolder(parent_folder, changeSet);
                })
                        .addOnSuccessListener(activity,
                                (DriveFolder driveFolder) -> {
                                    Log.d("create PHONOTE FOLDER", "SUCCESS");
                                    phonote_dir[0] =driveFolder.getDriveId().asDriveFolder();
                                })
                        .addOnFailureListener(activity, e -> {
                            Log.d("create PHONOTE FOLDER", "Unable to create file");
                        })

                ;

                metadataBuffer.release();
            }
        }).addOnCompleteListener(activity, task -> {
            // need to create a folder for the current project if it doesn't already have one

            Query pq = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, activity.getIntent().getExtras().getString("name")))
                    .build();
            Task<MetadataBuffer> queryTaskProject = getDriveResourceClient(activity, account).query(pq);
            Task<MetadataBuffer> projectMetadataBufferTask = queryTaskProject.addOnSuccessListener(activity, (MetadataBuffer metadataBuffer) -> {
                MetadataBuffer foundProjects = metadataBuffer;
                if (foundProjects.getCount() > 0){
                    Log.d("Project Folder exists:", foundProjects.get(0).getTitle());
                    project_dir[0] = foundProjects.get(0).getDriveId().asDriveFolder();

                    // if the project folder exists we need to find it's dub folders
                    AtomicInteger i = new AtomicInteger();
                    DriveFolder[][] subfolders = new DriveFolder[][] {citations_dir, sources_dir, notes_dir};
                    for (String t: new String[]{project_name+"citations", project_name+"sources", project_name+"notes"}){
                        Query sub_q = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, t))

                                .build();
                        Task<MetadataBuffer> q_subfolder = getDriveResourceClient(activity, account).query(sub_q);
                        Task<MetadataBuffer> qSubfolderTask = q_subfolder.addOnSuccessListener(activity, (MetadataBuffer subBuffer) -> {
                            MetadataBuffer subFolders = subBuffer;
                            if (subFolders.getCount() > 0) {

                                if (i.get() == 0) {
                                    Log.d("Subfolders:", subBuffer.get(0).getTitle() + " " + i.get());
                                    citations_dir[0] = subBuffer.get(0).getDriveId().asDriveFolder();
                                } else if (i.get() == 1) {
                                    Log.d("Subfolders:", subBuffer.get(0).getTitle() + " " + i.get());
                                    sources_dir[0] = subBuffer.get(0).getDriveId().asDriveFolder();
                                } else if (i.get() == 2) {
                                    Log.d("Subfolders:", subBuffer.get(0).getTitle() + " " + i.get());
                                    notes_dir[0] = subBuffer.get(0).getDriveId().asDriveFolder();
                                }
                                }
                            i.getAndIncrement();
                        });
                    }


                    Log.d("write-up", String.valueOf(project_dir == null) );

                }
                else {
                    Log.d("Syn called:", "no results for project");

                    getDriveResourceClient(activity, account).getRootFolder().continueWithTask(task2 -> {
                        MetadataChangeSet projectMetadataChangeSet = new MetadataChangeSet.Builder().setTitle(activity.getIntent().getExtras().getString("name"))
                                .setMimeType(DriveFolder.MIME_TYPE).build();

                        return getDriveResourceClient(activity, account).createFolder(phonote_dir[0], projectMetadataChangeSet);

                    })
                            .addOnSuccessListener(activity,
                                    driveFolder -> {
                                        Log.d("create ProjectFOLDER", "SUCCESS");

                                        project_dir[0] = driveFolder;
                                        // create the project subfolders
                                        getDriveResourceClient(activity, account).getRootFolder().continueWithTask(task4 -> {
                                                    MetadataChangeSet projectMetadataChangeSet = new MetadataChangeSet.Builder().setTitle(project_name+"citations")
                                                            .setMimeType(DriveFolder.MIME_TYPE).build();
                                                    return getDriveResourceClient(activity, account).createFolder(project_dir[0], projectMetadataChangeSet);
                                                }).addOnSuccessListener(activity, driveFolder1 -> {citations_dir[0] = driveFolder1;});

                                        getDriveResourceClient(activity, account).getRootFolder().continueWithTask(task4 -> {
                                                    MetadataChangeSet projectMetadataChangeSet = new MetadataChangeSet.Builder().setTitle(project_name+"sources")
                                                            .setMimeType(DriveFolder.MIME_TYPE).build();
                                                    return getDriveResourceClient(activity, account).createFolder(project_dir[0], projectMetadataChangeSet);
                                                }).addOnSuccessListener(activity, driveFolder1 -> {sources_dir[0] = driveFolder1;});

                                        getDriveResourceClient(activity, account).getRootFolder().continueWithTask(task4 -> {
                                            MetadataChangeSet projectMetadataChangeSet = new MetadataChangeSet.Builder().setTitle(project_name+"notes")
                                                    .setMimeType(DriveFolder.MIME_TYPE).build();
                                            return getDriveResourceClient(activity, account).createFolder(project_dir[0], projectMetadataChangeSet);
                                        }).addOnSuccessListener(activity, driveFolder1 -> {notes_dir[0] = driveFolder1;});
                                    })
                            .addOnFailureListener(activity, e -> {
                                Log.d("create PHONOTE FOLDER", "Unable to create file");
                            });
                }

                foundProjects.release();

            }).addOnFailureListener(activity, metadataBuffer -> {
                Log.d("Syn called:", metadataBuffer.toString());

            }).addOnSuccessListener(activity, task2 -> {


                AtomicInteger i = new AtomicInteger();
                String[] t_minus = new String[] {"citations", "sources", "notes"};
                for (String t: new String[]{project_name+"citations", project_name+"sources", project_name+"notes"}) {

                    String rootPath = activity.getFilesDir().getAbsolutePath() + "/projects/" + project_name + "/" + t_minus[i.get()] + "/";
                    Query sub_q = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, t))

                            .build();
                    Task<MetadataBuffer> q_subfolder = getDriveResourceClient(activity, account).query(sub_q);
                    Task<MetadataBuffer> qSubfolderTask = q_subfolder.addOnSuccessListener(activity, (MetadataBuffer subBuffer) -> {
                        MetadataBuffer subFolders = subBuffer;
                        if (subFolders.getCount() > 0) {

                            File directory = new File(rootPath);
                            if  (directory != null) {
                                Log.d("write-up", String.valueOf(subBuffer.get(0).getDriveId().asDriveFolder() == null) + "is drive folder invalid " +t );
                                copyFilesFromLocalToDriveFolder(activity, account, subBuffer.get(0).getDriveId().asDriveFolder(), directory);
                            }
                            }

                        i.getAndIncrement();
                    });
                }



            });


        }).addOnFailureListener(activity, metadataBuffer -> {
        Log.d("Syn called:", metadataBuffer.toString());
        });


    }

}
