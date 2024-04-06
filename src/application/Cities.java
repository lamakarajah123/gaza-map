package application;

public class Cities {
    String city;
    double latitude;
    double longitude;
    boolean isCity;

    public Cities(String city, double latitude, double longitude, boolean isCity) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isCity = isCity;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public boolean getIssCity() {
        return isCity;
    }

    public void setCity(boolean city) {
        isCity = city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Cities{" +
                "city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isCity=" + isCity +
                '}';
    }
}
