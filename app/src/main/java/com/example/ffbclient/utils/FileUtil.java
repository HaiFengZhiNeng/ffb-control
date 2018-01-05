package com.example.ffbclient.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dell on 2017/11/9.
 */

public class FileUtil {

    public static String read(Context context, String filePath) {
        StringBuffer sb = new StringBuffer("");

        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");

                BufferedReader reader = new BufferedReader(inputStreamReader);

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean saveFile(String toSaveString, String filePath) {
        try {
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                File dir = new File(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(saveFile);
            outStream.write(toSaveString.getBytes());
            outStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
