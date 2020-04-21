package com.example.parsingxml;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener
{
    private String chosenRoad;
    private EditText editText;
    private ListView listView;
    private String currentRoadworksXML;
    private String plannedRoadworksXML;
    private String incidentsXML;
    private Button currentRoadworksButton;
    private Button plannedRoadworksButton;
    private Button currentIncidentsButton;
    private TextView textView;
    LinkedList<Roadwork> currentRoadworks = null;
    LinkedList <Roadwork> plannedRoadworks = null;
    LinkedList <Roadwork> currentIncidents = null;
    LinkedList <Roadwork> relevantRoadworks = new LinkedList<>();

    // Traffic Scotland URLs
    private String urlSourceCurrentRoadworks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlSourcePlannedRoadworks = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String urlSourceCurrentIncidents = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        editText = (EditText)findViewById(R.id.txtRoad);
        textView = (TextView)findViewById(R.id.textView2);

        currentRoadworksButton = (Button)findViewById((R.id.currentRoadworksButton));
        currentRoadworksButton.setOnClickListener(this);

        plannedRoadworksButton = (Button)findViewById((R.id.plannedRoadworksButton));
        plannedRoadworksButton.setOnClickListener(this);

        currentIncidentsButton = (Button)findViewById((R.id.btnIncidents));
        currentIncidentsButton.setOnClickListener(this);

        listView = (ListView)findViewById(R.id.listview);

        LoadCurrentRoadworks();
        LoadPlannedRoadworks();
        LoadCurrentIncidents();

        configureBackButton();
    }

    private void showtbDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void GetListOfIncidentsForChosenRoad(View v)
    {
        chosenRoad = editText.getText().toString();

        Log.e("Road", chosenRoad);

        if (chosenRoad.isEmpty())
        {
            showtbDialog("Please enter a road to search");
        }
        else
        {
            if (v.getId() == R.id.currentRoadworksButton)
            {
                if (currentRoadworks != null)
                {
                    relevantRoadworks = new LinkedList<>();

                    for (Roadwork o : currentRoadworks)
                    {
                        if (o.getTitle().substring(o.getTitle().indexOf(' ')).toLowerCase().contains(chosenRoad.toLowerCase()))
                        {
                            relevantRoadworks.add(o);
                        }
                    }

                    if (relevantRoadworks.size() == 0)
                    {
                        showtbDialog("No current roadworks to show for that road.");
                    }
                }

                else
                {
                    showtbDialog("No current roadworks to show.");
                }
            }

            // search planned roadworks
            else if (v.getId() == R.id.plannedRoadworksButton)
            {
                if (plannedRoadworks != null)
                {
                    relevantRoadworks = new LinkedList<>();

                    for (Roadwork o : plannedRoadworks)
                    {
                        if (o.getTitle().substring(o.getTitle().indexOf(' ')).toLowerCase().contains(chosenRoad.toLowerCase()))
                        {
                            relevantRoadworks.add(o);
                        }
                    }

                    if (relevantRoadworks.size() == 0)
                    {
                        showtbDialog("No planned roadworks to show for that road.");
                    }
                }

                else
                {
                    showtbDialog("No planned roadworks to show.");
                }
            }

            // search planned roadworks
            else if (v.getId() == R.id.btnIncidents)
            {
                if (currentIncidents != null)
                {
                    relevantRoadworks = new LinkedList<>();

                    for (Roadwork o : currentIncidents)
                    {
                        if (o.getTitle().substring(o.getTitle().indexOf(' ')).toLowerCase().contains(chosenRoad.toLowerCase()))
                        {
                            relevantRoadworks.add(o);
                        }
                    }

                    if (relevantRoadworks.size() == 0)
                    {
                        showtbDialog("No current incidents to show for that road.");
                    }
                }

                else
                {
                    showtbDialog("No current incidents to show.");
                }
            }
        }
    }

    public void onClick(View clickedButton)
    {
        if (clickedButton.getId() == R.id.currentRoadworksButton)
        {
            GetListOfIncidentsForChosenRoad(clickedButton);
            final ArrayList<String> roadworkArrayList = new ArrayList<>();

            try
            {
                for (Roadwork o : relevantRoadworks)
                {
                    roadworkArrayList.add(o.getTitle());
                }
            }

            catch (Exception e)
            {
                Log.e("error", e.toString() );
            }

            final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,roadworkArrayList);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String item = ((TextView)view).getText().toString();

                    for (Roadwork o : relevantRoadworks)
                    {
                        if (o.getTitle() == item)
                        {
                            textView.setText(o.getTitle().substring(o.getTitle().indexOf(' ') + 1) + "\n\n" + o.getStartDate() + " - " + o.getEndDate() + "\n\n(duration: " + o.getDuration() + " days)"+ "\n\nLink: " + o.getLink() + "\n\nCo-ordinates:" + o.getGeoPoint());
                            if (o.getDuration() < 30)
                            {
                                // short duration - green text
                                textView.setTextColor(getResources().getColor(R.color.green));
                            }
                            else if (o.getDuration() >= 30 && o.getDuration() <= 60)
                            {
                                // medium duration - orange text
                                textView.setTextColor(getResources().getColor(R.color.orange));
                            }
                            else
                            {
                                // long duration - red text
                                textView.setTextColor(getResources().getColor(R.color.red));
                            }
                        }
                    }
                }
            });
        }

        else if (clickedButton.getId() == R.id.plannedRoadworksButton)
        {
            GetListOfIncidentsForChosenRoad(clickedButton);
            final ArrayList<String> roadworkArrayList = new ArrayList<>();

            try
            {
                for (Roadwork o : relevantRoadworks)
                {
                    roadworkArrayList.add(o.getTitle());
                }
            }

            catch (Exception e)
            {
                Log.e("error", e.toString() );
            }

            final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,roadworkArrayList);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String item = ((TextView)view).getText().toString();

                    for (Roadwork o : relevantRoadworks)
                    {
                        if (o.getTitle() == item)
                        {
                            textView.setText(o.getTitle().substring(o.getTitle().indexOf(' ') + 1) + "\n\n" + o.getStartDate() + " - " + o.getEndDate() + "\n\n(duration: " + o.getDuration() + " days)"+ "\n\nLink: " + o.getLink() + "\n\nCo-ordinates:" + o.getGeoPoint());
                            if (o.getDuration() < 30)
                            {
                                // short duration - green text
                                textView.setTextColor(getResources().getColor(R.color.green));
                            }
                            else if (o.getDuration() >= 30 && o.getDuration() <= 60)
                            {
                                // medium duration - yellow text
                                textView.setTextColor(getResources().getColor(R.color.orange));
                            }
                            else
                            {
                                // long duration - red text
                                textView.setTextColor(getResources().getColor(R.color.red));
                            }
                        }
                    }
                }
            });
        }

        else if (clickedButton.getId() == R.id.btnIncidents)
        {
            GetListOfIncidentsForChosenRoad(clickedButton);
            final ArrayList<String> roadworkArrayList = new ArrayList<>();

            try
            {
                for (Roadwork o : relevantRoadworks)
                {
                    roadworkArrayList.add(o.getTitle());
                }
            }

            catch (Exception e)
            {
                Log.e("error", e.toString() );
            }

            final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,roadworkArrayList);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String item = ((TextView)view).getText().toString();

                    for (Roadwork o : relevantRoadworks)
                    {
                        if (o.getTitle() == item)
                        {
                            textView.setText(o.getTitle().substring(o.getTitle().indexOf(' ') + 1) + "\n\n" + o.getDescription() + "\n\nLink: " + o.getLink() + "\n\nCo-ordinates:" + o.getGeoPoint());
                            textView.setTextColor(getResources().getColor(R.color.colorText));
                        }
                    }
                }
            });
        }

        else
        {
            Log.e("status","not recognised");
        }
    }

    // method taken from: https://stackoverflow.com/questions/883060/how-can-i-determine-if-a-date-is-between-two-dates-in-java
    public boolean IsBetweenTwoDates(Date min, Date max, Date d)
    {
        return min.compareTo(d) * d.compareTo(max) >= 0;
    }

    public void LoadCurrentRoadworks()
    {
        new Thread(new SecondActivity.Task(urlSourceCurrentRoadworks)).start();
    }

    public void LoadPlannedRoadworks()
    {
        new Thread(new SecondActivity.Task(urlSourcePlannedRoadworks)).start();
    }

    public void LoadCurrentIncidents()
    {
        new Thread(new SecondActivity.Task(urlSourceCurrentIncidents)).start();
    }

    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }

        @Override
        public void run()
        {
            if (url == urlSourceCurrentRoadworks)
            {
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";

                try
                {
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                    while ((inputLine = in.readLine()) != null)
                    {
                        currentRoadworksXML = currentRoadworksXML + inputLine;
                    }

                    in.close();
                }
                catch (IOException ae)
                {
                    Log.e("MyTag", "ioexception");
                }

                currentRoadworksXML = currentRoadworksXML.substring(4);

                currentRoadworks = parseData(currentRoadworksXML);

                if (currentRoadworks != null)
                {
                    for (Roadwork o : currentRoadworks)
                    {
                        o.setStartAndEndDate();
                        o.setDateStartDate();
                        o.setDateEndDate();
                        o.setDuration();
                    }
                }

                else
                {
                    Log.e("MyTag", "List is null");
                }
            }

            else if (url == urlSourcePlannedRoadworks)
            {
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";

                try
                {
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                    while ((inputLine = in.readLine()) != null)
                    {
                        plannedRoadworksXML = plannedRoadworksXML + inputLine;
                    }

                    in.close();
                }
                catch (IOException ae)
                {
                    Log.e("MyTag", "ioexception");
                }

                plannedRoadworksXML = plannedRoadworksXML.substring(4);
                plannedRoadworks = parseData(plannedRoadworksXML);

                if (plannedRoadworks != null)
                {
                    for (Roadwork o : plannedRoadworks)
                    {
                        o.setStartAndEndDate();
                        o.setDateStartDate();
                        o.setDateEndDate();
                        o.setDuration();
                    }
                }

                else
                {
                    Log.e("MyTag", "List is null");
                }
            }

            else if (url == urlSourceCurrentIncidents)
            {
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";

                try
                {
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                    while ((inputLine = in.readLine()) != null)
                    {
                        incidentsXML = incidentsXML + inputLine;
                    }

                    in.close();
                }
                catch (IOException ae)
                {
                    Log.e("MyTag", "ioexception");
                }

                incidentsXML = incidentsXML.substring(4);
                currentIncidents = parseData(incidentsXML);

                if (currentIncidents != null)
                {
                    Log.e("MyTag", "Current incidents not null");
                }

                else
                {
                    Log.e("MyTag", "List is null");
                }
            }

            SecondActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                }
            });
        }

    }

    private LinkedList<Roadwork> parseData(String dataToParse)
    {
        Roadwork roadwork = null;
        LinkedList <Roadwork> roadworks = null;

        // following three variables make sure we don't store unnecessary XML
        // without these, the application crashes
        int numTimesSeenTitle = 0;
        int numTimesSeenDescription = 0;
        int numTimesSeenLink = 0;

        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if(eventType == XmlPullParser.START_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        roadworks  = new LinkedList<Roadwork>();
                    }

                    else if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        roadwork = new Roadwork();
                    }

                    else if (xpp.getName().equalsIgnoreCase("title"))
                    {
                        if (numTimesSeenTitle != 0)
                        {
                            String temp = xpp.nextText();
                            roadwork.setTitle(String.valueOf(numTimesSeenTitle) + ". " + temp);
                        }
                        numTimesSeenTitle++;
                    }

                    else if (xpp.getName().equalsIgnoreCase("description"))
                    {
                        if (numTimesSeenDescription != 0)
                        {
                            String temp = xpp.nextText();
                            roadwork.setDescription(temp);
                        }
                        numTimesSeenDescription++;
                    }

                    else if (xpp.getName().equalsIgnoreCase("link"))
                    {
                        if (numTimesSeenLink != 0)
                        {
                            String temp = xpp.nextText();
                            roadwork.setLink(temp);
                        }
                        numTimesSeenLink++;
                    }

                    else if (xpp.getName().contains("point"))
                    {
                        String temp = xpp.nextText();
                        roadwork.setGeoPoint(temp);
                    }
                }

                else if(eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        roadworks.add(roadwork);
                    }

                    else if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        int size;
                        size = roadworks.size();
                    }
                }

                eventType = xpp.next();
            }
        }

        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }

        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        return roadworks;
    }

    private void configureBackButton()
    {
        Button backButton = (Button) findViewById(R.id.btnDateSearch);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }
}
