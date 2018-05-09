package cmps121.phonote;

import java.io.Serializable;

public class SourceData implements Serializable {
    private String title;
    private String author;
    private String publisher;
    private String city;
    private String year;

    public SourceData(String title, String author, String publisher,
                      String city, String year) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.city = city;
        this.year = year;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public String getCity() {
        return this.city;
    }

    public String getYear() {
        return this.year;
    }
}
