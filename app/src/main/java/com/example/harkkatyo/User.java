package com.example.harkkatyo;
import android.content.Context;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class User  {
    private String username ="";
    private String password ="";
    private String filename = "users.csv";
    private Context context;

    public User(String un, String pw, Context context){
        username = un;
        password = pw;
        this.context = context; //get context from login, to pass for outputStreamWriter
        writeUserToFile();
    }

    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void writeUserToFile(){
        try {
            OutputStreamWriter outs = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND));
            outs.write(username+";"+password+"\n");
            outs.close();
        } catch (FileNotFoundException error) {
            Log.e("FileNotFoundException", "File not found");
        } catch (IOException error) {
            Log.e("IOException", "Error with input");
        } finally{
            System.out.println("Write complete");
        }
    }



}
