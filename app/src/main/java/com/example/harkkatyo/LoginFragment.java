package com.example.harkkatyo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment{
    private View view;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonGoCreateUser;
    private Button buttonLogin;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.hideKeyboard(this.getActivity()); //close soft keyboard
        view = inflater.inflate(R.layout.fragment_login, container, false);
        context = this.getActivity();
        editTextUsername = (EditText) view.findViewById(R.id.editTextLoginUserName);
        editTextPassword = (EditText) view.findViewById(R.id.editTextLoginPassword);
        buttonGoCreateUser = (Button) view.findViewById(R.id.createUserButton);
        buttonGoCreateUser.setOnClickListener(view -> showCreateUserDialog());
        buttonLogin = (Button) view.findViewById(R.id.loginButton);
        buttonLogin.setOnClickListener(view -> login());
        if(Info.getInstance().getLoggedIn()){ //check to see if we are logged in or not
            buttonLogin.setText("Log out");
            editTextUsername.setText(Info.getInstance().getLoggedInAs());
        }else{
            buttonLogin.setText("Log in");
        }
        editTextPassword.setFocusable(!Info.getInstance().getLoggedIn());//use the boolean from info
        editTextUsername.setFocusable(!Info.getInstance().getLoggedIn()); //to declare if writable
        editTextUsername.setFocusableInTouchMode(!Info.getInstance().getLoggedIn());
        editTextPassword.setFocusableInTouchMode(!Info.getInstance().getLoggedIn());
        buttonGoCreateUser.setEnabled(!Info.getInstance().getLoggedIn());
        return view;
    }

    private void login(){
        if(Info.getInstance().getLoggedIn()){
            Toast toast = Toast.makeText(getContext(),"Logged out successfully.",Toast.LENGTH_SHORT);
            toast.show();
            Info.getInstance().setLoggedIn(false);
            Info.getInstance().setLoggedInAs("");
            buttonLogin.setText("Log in");
            editTextUsername.setText("");
            editTextPassword.setText("");
            editTextPassword.setFocusable(!Info.getInstance().getLoggedIn());//use the boolean from info
            editTextUsername.setFocusable(!Info.getInstance().getLoggedIn());//to declare if writable
            editTextUsername.setFocusableInTouchMode(!Info.getInstance().getLoggedIn());
            editTextPassword.setFocusableInTouchMode(!Info.getInstance().getLoggedIn());
            buttonGoCreateUser.setEnabled(!Info.getInstance().getLoggedIn());
        } else{
            String usernameEntry = editTextUsername.getText().toString().trim();
            String passwordEntry = editTextPassword.getText().toString();
            String entry = usernameEntry+";"+passwordEntry;
            readUserFromFile(entry,usernameEntry);
        }
    }

    public void readUserFromFile(String userEntry, String unEntry) {
        Toast toast;
        Boolean userFound = false;
        try {
            InputStream inputs = context.openFileInput("users.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
            String row = "";
            while ((row = br.readLine()) != null) {
                if (row.equals(userEntry)){
                    userFound = true;
                    break;
                }
            }
            if(userFound){
                toast = Toast.makeText(getContext(),"Logged in as "+unEntry,Toast.LENGTH_SHORT);
                MainActivity.hideKeyboard(this.getActivity()); //close soft keyboard
                Info.getInstance().setLoggedIn(true);
                Info.getInstance().setLoggedInAs(unEntry);
                editTextPassword.setFocusable(!Info.getInstance().getLoggedIn());//use the boolean from info
                editTextUsername.setFocusable(!Info.getInstance().getLoggedIn());//to declare if writable
                editTextUsername.setFocusableInTouchMode(!Info.getInstance().getLoggedIn());
                editTextPassword.setFocusableInTouchMode(!Info.getInstance().getLoggedIn());
                buttonLogin.setText("Log out");
                buttonGoCreateUser.setEnabled(!Info.getInstance().getLoggedIn());
            }
            else{
                toast = Toast.makeText(getContext(),"Username or password wrong!",Toast.LENGTH_SHORT);
            }
            toast.show();

            inputs.close();
        } catch (IOException error) {
            Log.e("IOException", "Error with input");
        } finally {
            System.out.println("Read complete");
        }
    }

    private void showCreateUserDialog(){
        Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.dialog_create_user);
        EditText editTextCreateUn = (EditText) dialog.findViewById(R.id.editTextCreateUsername);
        EditText editTextCreatePw = (EditText) dialog.findViewById(R.id.editTextCreatePassword);
        EditText editTextConfirmPw = (EditText) dialog.findViewById(R.id.editTextConfirmPassword);
        ImageView buttonCloseCreate = dialog.findViewById(R.id.buttonCloseCreateUser);
        Button buttonCreate = dialog.findViewById(R.id.buttonCreateUser);
        buttonCloseCreate.setOnClickListener(view -> dialog.dismiss());
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameProposal = editTextCreateUn.getText().toString().trim();
                String passwordProposal = editTextCreatePw.getText().toString();
                String passwordCheck = editTextConfirmPw.getText().toString();
                String errorText = checkPasswordAndUsernameRequirements(usernameProposal,passwordProposal,passwordCheck);
                Toast toast;
                if(errorText.length()>1){ // if the returned error is empty, then we can continue - else display error
                    toast = Toast.makeText(getContext(),errorText,Toast.LENGTH_SHORT);
                }else{
                    toast = Toast.makeText(getContext(),"User "+ usernameProposal+ " created successfully!",Toast.LENGTH_SHORT);
                    dialog.dismiss();
                    User user = new User(usernameProposal,passwordProposal, context);
                }
                toast.show();
            }
        });
        dialog.show();
    }

    private boolean checkIfUserExists(String un){
        boolean exists = false;
        try {
            InputStream inputs = context.openFileInput("users.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
            String row = "";
            while ((row = br.readLine()) != null) {
                if (row.split(";",2)[0].equals(un)){
                    exists =true;
                    break;
                }
            }
            inputs.close();
        } catch (IOException error) {
            Log.e("IOException", "Error with input");
        } finally {
            System.out.println("Read complete");
        }
        return exists;
    }

    public String checkPasswordAndUsernameRequirements( String usernameToCheck, String passwordToCheck, String passwordConfirm){
        String text = ""; // text to show to the user if something is wrong with their password

        //check for these characters
        Pattern digit = Pattern.compile("[0-9]");
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern specials = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");

        if(usernameToCheck.length() < 3){
            text = "Username too short! Please enter at least 3 characters.";
        }
        else if(!passwordToCheck.equals(passwordConfirm)){
            text = "Passwords do not match.";
        }
        else if(passwordToCheck.length() < 12){
            text = "Password invalid. Please enter at least 12 characters.";
        }
        else if(!lowercase.matcher(passwordToCheck).find()){
            text = "Password invalid. No lowercase letter (a-z) found.";
        }
        else if(!uppercase.matcher(passwordToCheck).find()){
            text = "Password invalid. No uppercase letter (A-Z) found.";
        }
        else if(!digit.matcher(passwordToCheck).find()){
            text = "Password invalid. No digit (0-9) found.";
        }
        else if(!specials.matcher(passwordToCheck).find()){
            text = "Password invalid. No special character found.";
        } else if(checkIfUserExists(usernameToCheck)){
            text = "Username exists already. Please choose another username.";
        }

        return text;
    }

}
