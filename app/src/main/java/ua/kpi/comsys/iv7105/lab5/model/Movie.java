package ua.kpi.comsys.iv7105.lab5.model;

public class Movie {
    private String title;
    private String year;
    private String imdbID;
    private String type;
    private String poster;

    private String rated;
    private String released;
    private String runtime;
    private String genre;

    public Movie(String title, String year, String imdbID, String type, String poster) {
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.type = type;
        this.poster = poster;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Movie() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String shortInfo() {
        return "Title=" + title + '\n' +
                "Year=" + year + '\n' +
                "ImdbID=" + imdbID + '\n' +
                "Type=" + type + '\n' +
                "Poster=" + poster;
    }

    public String fullInfo() {
        return "Title=" + title + '\n' +
                "Year=" + year + '\n' +
                "ImdbID=" + imdbID + '\n' +
                "Type=" + type + '\n' +
                "Poster=" + poster + '\n' +
                "Rated=" + rated + '\n' +
                "Released=" + released + '\n' +
                "Runtime=" + runtime + '\n' +
                "Genre=" + genre;
    }
}
