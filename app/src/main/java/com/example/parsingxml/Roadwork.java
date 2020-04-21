// Name: Colin Campbell
// Matriculation number: S1829119

package com.example.parsingxml;

import android.util.Log;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Roadwork
{
    // Attributes
    private String title;
    private String description;
    private String link;
    private String geoPoint;
    private String startDate;
    private String endDate;
    private Date dateStartDate;
    private Date dateEndDate;
    private long duration;

    public void setDateStartDate()
    {
        try
        {
            dateStartDate = new SimpleDateFormat("dd MMMM yyyy").parse(getStartDate());
        }

        catch (Exception e)
        {
            Log.e("Error:",e.toString());
        }
    }

    public void setDateEndDate()
    {
        try
        {
            dateEndDate = new SimpleDateFormat("dd MMMM yyyy").parse(getEndDate());
        }

        catch (Exception e)
        {
            Log.e("Error:",e.toString());
        }
    }

    public Date getDateStartDate()
    {
        return dateStartDate;
    }

    public Date getDateEndDate()
    {
        return dateEndDate;
    }

    public void setDuration()
    {
        // following code taken from: https://mkyong.com/java/how-to-calculate-date-time-difference-in-java/
        long diff = getDateEndDate().getTime() - getDateStartDate().getTime();

        long diffDays = diff / (24 * 60 * 60 * 1000);

        duration = diffDays;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setStartAndEndDate()
    {
        // trim the first part of the description
        String str = getDescription().substring(12);
        String[] arrOfStr = str.split("<br />", 2);

        int pass = 0;

        for (String a : arrOfStr)
        {
            if (pass == 0)
            {
                setStartDate(a.substring(a.indexOf(',')+2, a.indexOf('-')-1));
            }

            else if (pass == 1)
            {
                setEndDate(a.substring(a.indexOf(',')+2, a.indexOf('-')-1));
            }

            pass++;
        }
    }

    public void setStartDate(String startDateIn)
    {
        this.startDate = startDateIn;
    }

    public void setEndDate(String endDateIn)
    {
        this.endDate = endDateIn;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }


    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getGeoPoint()
    {
        return geoPoint;
    }

    public void setGeoPoint(String geoPoint)
    {
        this.geoPoint = geoPoint;
    }

    public String toString()
    {
        String resultString = MessageFormat.format("Title: {0}, Link: {1}, Description: {2}, Geopoint: {3}, Start Date: {4}, End date: {5}", getTitle(), getLink(), getDescription(), getGeoPoint(), getStartDate(), getEndDate());

        return resultString;
    }
}