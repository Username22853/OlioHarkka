package com.example.harkkatyo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalendarFragment extends Fragment {
    private View view;
    private CalendarView calendarView;
    private String chosenDate;
    private String movieFile = "movies.csv";
    private ArrayList<CalendarMovie> movieArray =new ArrayList<CalendarMovie>();
    private ArrayAdapter<CalendarMovie> movieArrayAdapter;
    private ListView listViewCal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.hideKeyboard(this.getActivity()); //close soft keyboard
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        listViewCal = view.findViewById(R.id.listViewCalendar);
        calendarView = view.findViewById(R.id.calendarView);
        movieArrayAdapter = new ArrayAdapter<CalendarMovie>(this.getActivity(), android.R.layout.simple_list_item_1, movieArray);
        listViewCal.setAdapter(movieArrayAdapter);
        if(!Info.getInstance().getLoggedIn()){
            Toast toast = Toast.makeText(getContext(),"Please log in to use calendar!",Toast.LENGTH_SHORT);
            toast.show();
        }
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                if(!Info.getInstance().getLoggedIn()){
                    Toast toast = Toast.makeText(getContext(),"Please log in to use calendar!",Toast.LENGTH_SHORT);
                    toast.show();
                }
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = format.parse(String.valueOf(day)+"."+String.valueOf(month+1)+"."+String.valueOf(year));
                    chosenDate = format.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                checkForMovie(chosenDate); //check if there are movies on this date in movies -file
            }
        });
        return view;
    }
    private void checkForMovie(String checkDate){ //check if movie was seen/rated on chosen date
        movieArray.clear();
        try {
            InputStream inputs = this.getActivity().openFileInput(movieFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
            String row = "";
            String[] rowContents ={};
            while ((row = br.readLine()) != null) { //row = user;name;date;stars;comment(optional)
                rowContents = row.split(";");
                if(Info.getInstance().getLoggedInAs().equals(rowContents[0]) && rowContents.length>3){
                    if(rowContents[2].equals(checkDate) && rowContents.length>4){
                        CalendarMovie temp = new CalendarMovie(rowContents[1],rowContents[2],rowContents[3],rowContents[4]);
                        movieArray.add(temp);
                    }else if (rowContents[2].equals(checkDate)){
                        CalendarMovie temp = new CalendarMovie(rowContents[1],rowContents[2],rowContents[3]);
                        movieArray.add(temp);
                    }
                }
            }
            inputs.close();
        } catch (IOException error) {
            Log.e("IOException", "Error with input");
        } finally {
            System.out.println("Read complete");
        }
        listViewCal.invalidateViews();
    }
}


