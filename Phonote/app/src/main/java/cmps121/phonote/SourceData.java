package cmps121.phonote;

import java.io.Serializable;

public class SourceData implements Serializable {
    public String title;
    public String author;
    public String publisher;
    public String city;
    public String year;

    public SourceData(String title, String author, String publisher,
                      String city, String year) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.city = city;
        this.year = year;
    }
}
