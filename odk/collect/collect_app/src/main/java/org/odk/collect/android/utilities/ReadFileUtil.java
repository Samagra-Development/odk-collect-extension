package org.odk.collect.android.utilities;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A Utility class used to read the contents of a file and get files from the parent directory
 *
 *  @author Prabhav Chopra
 */

public class ReadFileUtil {

    /** Takes an Instance file to read from. Reading of the file occurs on a seperate thread
     Returns the file as a String
     */
    public static String FileRead(File instanceFile) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> result = executor.submit(new Callable<String>() {
            public String call() throws Exception {
                String str;
                try {
                    Reader fileReader = new FileReader(instanceFile);
                    BufferedReader bufReader = new BufferedReader(fileReader);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    line = bufReader.readLine();
                    while( line != null){
                        sb.append(line).append("\n");
                        line = bufReader.readLine();
                    }
                    str = sb.toString();

                }catch (IOException e){
                    throw new RuntimeException(e);
                }
                return str;
            }
        });

        try {
            String string = result.get();
            return string;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /** Takes an Instance file and gives all the files inside the parent directory.
     Returns a List of files inside the parent directory of the instance file.
     */
    public static List<File> getFilesInParentDirectory(File instanceFile){

        List<File> files=new ArrayList<File>();
        File[] allFiles;

        if(instanceFile.getParentFile().listFiles().length > 0){
            allFiles = instanceFile.getParentFile().listFiles();
        }else{
            allFiles = null;
        }

        String instanceName = instanceFile.getName();

        for (File f : allFiles) {
            String fileName = f.getName();

            if (fileName.startsWith(".")) {
                continue;  // ignore invisible files
            } else if (instanceName.equals(fileName)) {
                Log.d("XML:", "file found");
                continue;  // the xml file has already been added
            }
            files.add(f);
        }
        return files;
    }
}
