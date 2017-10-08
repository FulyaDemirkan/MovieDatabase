////////////////////////////////////////////////////////////////////////////////
//
// Movie.java
// ==================
// Movie object contains title, year, running time and genre information.
//
// AUTHOR:  F. Fulya Demirkan (demirkaf@sheridancollege.ca)
// CREATED: 30/Sep/2017
// UPDATED: 06/Oct/2017
//
////////////////////////////////////////////////////////////////////////////////
package fd;

import java.util.ArrayList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Movie
{

    private StringProperty title = new SimpleStringProperty();
    private IntegerProperty year = new SimpleIntegerProperty();
    private IntegerProperty runningTime = new SimpleIntegerProperty();
    private ArrayList<String> genres = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////
    // Default constructor for a movie object
    ////////////////////////////////////////////////////////////////////////////
    public Movie()
    {

    }

    ////////////////////////////////////////////////////////////////////////////
    // 4 param constructor for a movie object
    ////////////////////////////////////////////////////////////////////////////
    public Movie(String title, int year, int runningTime, ArrayList genres)
    {
        setTitle(title);
        setYear(year);
        setRunningTime(runningTime);
        setGenres(genres);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Getter and setter methods for each parameter
    ////////////////////////////////////////////////////////////////////////////
    public String getTitle()
    {
        return title.getValue();
    }

    public StringProperty titleProperty()
    {
        return title;
    }

    public void setTitle(String title)
    {
        if (title != null || !title.isEmpty())
        {
            this.title.set(title);
        } else
        {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
    }

    public int getYear()
    {
        return year.getValue();
    }

    public IntegerProperty yearProperty()
    {
        return year;
    }

    public void setYear(int year)
    {
        if (year > 0)
        {
            this.year.set(year);
        } else
        {
            throw new IllegalArgumentException("Year cannot be equal or less "
                    + "than 0.");
        }
    }

    public int getRunningTime()
    {
        return runningTime.getValue();
    }

    public IntegerProperty runningTimeProperty()
    {
        return runningTime;
    }

    public void setRunningTime(int runningTime)
    {
        if (runningTime >= 0)
        {
            this.runningTime.set(runningTime);
        } else
        {
            throw new IllegalArgumentException("Running Time cannot be less "
                    + "than 0.");
        }
    }

    public ArrayList getGenres()
    {
        return genres;
    }

    public void setGenres(ArrayList genres)
    {
        this.genres = genres;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Converts genre arraylist to formatted String
    ////////////////////////////////////////////////////////////////////////////
    public String printGenres()
    {
        String genreList = "";

        for (int i = 0; i < genres.size(); i++)
        {
            if (genres.get(i) != null)
            {
                genreList += genres.get(i);
                if (i < genres.size() - 1)
                {
                    genreList += ", ";
                }
            }
        }
        return genreList;
    }
}
