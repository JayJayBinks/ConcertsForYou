package jayjaybinks.github.com.concertsforyou;

import android.os.Bundle;

/**
 * Created by Name on 16.12.2017.
 */

public class Concert{

    public static final String ARTIST_NAME = "ARTIST_NAME_PARAM";
    public static final String CONCERT_DATE = "CONCERT_DATE_PARAM";
    public static final String IMAGE_NAME = "IMAGE_NAME";
    public static final String VENUE_NAME = "VENUE_NAME";
    public static final String VENUE_COUNTRY = "VENUE_COUNTRY";
    public static final String VENUE_CITY = "VENUE_CITY";

    private Bundle concertBundle = new Bundle();

    private String artistName;
    private String concertDate;
    private String imageName;
    private String venueName;
    private String venueCountry;
    private String venueCity;

    public Concert(String artistName, String concertDate, String pictureURL, String venueName, String venueCountry, String venueCity) {
        this.artistName = artistName;
        this.concertDate = concertDate;
        this.imageName = pictureURL;
        this.venueName = venueName;
        this.venueCountry = venueCountry;
        this.venueCity = venueCity;
    }

    public Bundle getAsBundle() {
        concertBundle.putString(ARTIST_NAME, artistName);
        concertBundle.putString(CONCERT_DATE, concertDate);
        concertBundle.putString(IMAGE_NAME, imageName);
        concertBundle.putString(VENUE_NAME, venueName);
        concertBundle.putString(VENUE_COUNTRY, venueCountry);
        concertBundle.putString(VENUE_CITY, venueCity);

        return concertBundle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getConcertDate() {
        return concertDate;
    }

    public void setConcertDate(String concertDate) {
        this.concertDate = concertDate;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueCountry() {
        return venueCountry;
    }

    public void setVenueCountry(String venueCountry) {
        this.venueCountry = venueCountry;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }
}
