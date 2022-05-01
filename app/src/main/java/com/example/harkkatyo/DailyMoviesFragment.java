package com.example.harkkatyo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DailyMoviesFragment extends Fragment {
    private View view;
    private ArrayList<Theater> theaterArrayList =new ArrayList<>();
    private ArrayAdapter<Theater> theaterArrayAdapter;
    private ArrayList<DailyMovie> dailyMovieArrayList = new ArrayList<>();
    private ArrayAdapter<DailyMovie> dailyMovieArrayAdapter;
    private Spinner theaterSpinner;
    private ListView movieList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.hideKeyboard(this.getActivity()); //close soft keyboard
        view = inflater.inflate(R.layout.fragment_daily_movies, container, false);
        theaterSpinner = view.findViewById(R.id.spinnerTheaters);
        movieList = view.findViewById(R.id.listViewDailyMovies);
        getTheaters(); //get theaters from Finnkino xml to show in the spinner
        theaterArrayAdapter = new ArrayAdapter<Theater>(this.getActivity(),android.R.layout.simple_spinner_dropdown_item,theaterArrayList);
        theaterSpinner.setAdapter(theaterArrayAdapter);
        dailyMovieArrayAdapter = new ArrayAdapter<DailyMovie>(this.getActivity(), android.R.layout.simple_list_item_1,dailyMovieArrayList);
        movieList.setAdapter(dailyMovieArrayAdapter);
        theaterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Theater selectedTheater = (Theater) adapterView.getSelectedItem();
                getDailyMovies(selectedTheater);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    private void getTheaters() {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.parse("https://www.finnkino.fi/en/xml/TheatreAreas/");
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("TheatreArea");
            for (int i = 0; i < nodeList.getLength();i++){
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    Theater tempt = new Theater(element.getElementsByTagName("ID")
                            .item(0).getTextContent(),element.getElementsByTagName("Name").item(0).getTextContent());
                    theaterArrayList.add(tempt);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void getDailyMovies(Theater theater){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        String url = "https://www.finnkino.fi/xml/Schedule/?area="+theater.getTheaterID()+"&dt="+date;
        dailyMovieArrayList.clear();
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.parse(url);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("Show");
            for (int i = 0; i < nodeList.getLength();i++){
                Node n = nodeList.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE){ //get elements from the xml
                    Element element = (Element) n;
                    String[] dateAndTime = element.getElementsByTagName("dttmShowStart").item(0).getTextContent().split("T",2);
                    String[] timeList = dateAndTime[1].split(":",3); //break down time
                    //to not show the seconds
                    String time = timeList[0] +":"+timeList[1];
                    DailyMovie temp =new DailyMovie(element.getElementsByTagName("Title").item(0).getTextContent(),
                            time,
                            element.getElementsByTagName("Theatre").item(0).getTextContent());
                    dailyMovieArrayList.add(temp);
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        dailyMovieArrayAdapter.notifyDataSetChanged();
    }
}
