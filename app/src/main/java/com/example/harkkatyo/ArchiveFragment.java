package com.example.harkkatyo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ArchiveFragment extends Fragment {
    private View view;
    private ArrayList<Object> movieArray =new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<Object> movieArrayAdapter;
    private String movieFile = "movies.csv";
    private Object selectedMovie;
    private Button chooseDateButton;
    private Button saveReviewButton;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String pickedDate = "";
    private RatingBar ratingBar;
    private EditText movieComment;
    private Object readMovie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.hideKeyboard(this.getActivity()); //close soft keyboard
        view = inflater.inflate(R.layout.fragment_archive, container, false);
        listView = view.findViewById(R.id.listViewArchive);
        ratingBar = view.findViewById(R.id.ratingBar);
        movieComment = view.findViewById(R.id.editTextMovieComment);
        movieArrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, movieArray);
        listView.setAdapter(movieArrayAdapter);
        saveReviewButton = (Button) view.findViewById(R.id.buttonSaveReview);

        initDatePicker();
        chooseDateButton = (Button) view.findViewById(R.id.buttonChooseDate);
        chooseDateButton.setOnClickListener(view -> datePickerDialog.show());
        saveReviewButton.setEnabled(false);

        if(!Info.getInstance().getLoggedIn()){
            Toast toast = Toast.makeText(getContext(),"Please log in to use the archive!",Toast.LENGTH_SHORT);
            toast.show();
        }else{
            readMovieFile();
            getMoviesFromNet();
        }


        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            readMovie = listView.getItemAtPosition(i);
            saveReviewButton.setEnabled(true);
            if (readMovie instanceof ReviewedMovie){
                selectedMovie =(ReviewedMovie) readMovie;
                chooseDateButton.setText(((ReviewedMovie) selectedMovie).getReviewDate());
                pickedDate=((ReviewedMovie) selectedMovie).getReviewDate();
                ratingBar.setRating(Float.parseFloat(((ReviewedMovie) selectedMovie).getReviewStars()));
                movieComment.setText(((ReviewedMovie) selectedMovie).getReviewComment());
            }else{
                selectedMovie =(Movie) readMovie;
                clearReview();
            }
        });

        saveReviewButton.setOnClickListener(view -> saveReview());
        return view;
    }

    private void getMoviesFromNet() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse("https://www.finnkino.fi/en/xml/Schedule/");
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("Show");
            boolean matchFound;
            for (int i = 0; i < nodeList.getLength();i++){
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String tempMovieName = element.getElementsByTagName("OriginalTitle")
                            .item(0).getTextContent().trim();
                    matchFound = false;
                    for (Object movie: movieArray){
                        if(((Movie) movie).getMovieName().equals(tempMovieName)){
                            matchFound =true;
                            break;
                        }
                    }
                    if(!matchFound){
                        Movie temp = new Movie(tempMovieName);
                        movieArray.add(temp);
                        addMovieToFile(temp);
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void addMovieToFile(Movie movie){
        try {
            OutputStreamWriter outs = new OutputStreamWriter(this.getActivity().openFileOutput(movieFile, Context.MODE_APPEND));
            outs.write(Info.getInstance().getLoggedInAs()+";"+movie.getMovieName()+"\n"); // csv file contains user and movie for user
            outs.close();
        } catch (FileNotFoundException error) {
            Log.e("FileNotFoundException", "File not found");
        } catch (IOException error) {
            Log.e("IOException", "Error with input");
        } finally{
            System.out.println("Write complete");
        }
    }

    private void readMovieFile() {
        movieArray.clear();
        try {
            InputStream inputs = this.getActivity().openFileInput(movieFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
            String row = "";
            String[] rowContents ={};
            while ((row = br.readLine()) != null) {
                rowContents = row.split(";");
                if(Info.getInstance().getLoggedInAs().equals(rowContents[0])){
                    if(rowContents.length >4){
                        ReviewedMovie temp = new ReviewedMovie(rowContents[1],rowContents[2],rowContents[3],rowContents[4]);
                        movieArray.add(temp);
                    } else if(rowContents.length >3){
                        ReviewedMovie temp = new ReviewedMovie(rowContents[1],rowContents[2],rowContents[3]);
                        movieArray.add(temp);
                    } else {
                        Movie temp = new Movie(rowContents[1]);
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
        listView.invalidateViews();
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String monthStr ="";
                String dayStr = "";
                if (month<10){
                    monthStr = "0"+Integer.toString(month);
                }else{
                    monthStr = Integer.toString(month);
                } if (day<10){
                    dayStr = "0"+Integer.toString(day);
                }else{
                    dayStr = Integer.toString(day);
                }
                pickedDate = dayStr+"."+monthStr+"."+Integer.toString(year);
                chooseDateButton.setText(pickedDate);
            }
        };
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_TRADITIONAL;

        datePickerDialog = new DatePickerDialog(this.getActivity(),
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen, dateSetListener,year,month,day);
    }

    private void saveReview(){ //save movie review to movie file
        ReviewedMovie reviewedMovie;
        // movie = selectedMovie
        // date = pickedDate
        String stars = String.valueOf(ratingBar.getRating());
        String comment = movieComment.getText().toString();
        if (pickedDate.equals("")) {
            Toast toast = Toast.makeText(getContext(),"Remember to pick the date of your visit to the movies!",Toast.LENGTH_SHORT);
            toast.show();
        } else if(stars.equals("0.0")){
            Toast toast = Toast.makeText(getContext(),"Please give the movie a star-rating!",Toast.LENGTH_SHORT);
            toast.show();
        } else{
            if(comment.length()==0){
                reviewedMovie = new ReviewedMovie(((Movie) selectedMovie).getMovieName(),pickedDate,stars);
            } else {
                reviewedMovie = new ReviewedMovie(((Movie) selectedMovie).getMovieName(),pickedDate,stars,comment);
            }
            replaceMovieWithReview(reviewedMovie);
            System.out.println(Info.getInstance().getLoggedInAs()+";"+reviewedMovie.getMovieName()
                    +";"+reviewedMovie.getReviewDate()+";"+reviewedMovie.getReviewStars()+";"
                    +reviewedMovie.getReviewComment());
        }

    }

    private void replaceMovieWithReview(ReviewedMovie reviewedMovie){
        List<String> lines = new LinkedList<>();
        int index=0;
        int indexOfReviewedMovie =0;
        try {
            InputStream inputs = this.getActivity().openFileInput(movieFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
            String row = "";
            while ((row = br.readLine()) != null) {
                if(!row.contains(Info.getInstance().getLoggedInAs()+";"+reviewedMovie.getMovieName())){
                    lines.add(row); //other movies than the one reviewed
                    index++;
                } else{
                    indexOfReviewedMovie=index;
                }
            }
            inputs.close();
        } catch (IOException error) {
            Log.e("IOException", "Error with input");
        } finally {
            System.out.println("Old movies saved to list");
        }
        //write again to file with reviewed movie
        try {
            index = 0;
            OutputStreamWriter outs = new OutputStreamWriter(this.getActivity().openFileOutput(movieFile, Context.MODE_PRIVATE));
            for(String line : lines){
                outs.write(line+"\n");
                index++;
                if (indexOfReviewedMovie ==index){
                    outs.write(Info.getInstance().getLoggedInAs()+";"+reviewedMovie.getMovieName()
                            +";"+reviewedMovie.getReviewDate()+";"+reviewedMovie.getReviewStars()+";"
                            +reviewedMovie.getReviewComment()+"\n");
                }
            }
            outs.close();
        } catch (FileNotFoundException error) {
            Log.e("FileNotFoundException", "File not found");
        } catch (IOException error) {
            Log.e("IOException", "Error with input");
        } finally{
            System.out.println("Rewrite complete");
            clearReview();
        }
    readMovieFile();
    }

    private void clearReview(){
        chooseDateButton.setText("Choose date");
        ratingBar.setRating(0);
        movieComment.setText("");
    }
}


