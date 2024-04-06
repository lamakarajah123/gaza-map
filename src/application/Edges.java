package application;

public class Edges {
    Cities city1;
    Cities city2;
    double weight;

    public Edges(Cities city1, Cities city2) {
        this.city1 = city1;
        this.city2 = city2;
        this.weight = calculateWeight(city1, city2);
    }

    public Cities getCity1() {
        return city1;
    }

    public void setCity1(Cities city1) {
        this.city1 = city1;
    }

    public Cities getCity2() {
        return city2;
    }

    public void setCity2(Cities city2) {
        this.city2 = city2;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edges{" +
                "city1=" + city1 +
                ", city2=" + city2 +
                ", weight=" + weight +
                '}';
    }

    private double calculateWeight(Cities city1, Cities city2) {
        final int EARTH_RADIUS = 6371;

        double latDistance = Math.toRadians(city2.latitude - city1.latitude);
        double lonDistance = Math.toRadians(city2.longitude - city1.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(city1.latitude)) * Math.cos(Math.toRadians(city2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

}