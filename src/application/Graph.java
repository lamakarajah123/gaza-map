package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private Map<String, Cities> cities;
    private List<Edges> edges;

    
    public Graph() {
        cities = new HashMap<>();
        edges = new ArrayList<>();
    }



    public void addCities(String name, double latitude, double longitude, boolean isCity) {
        cities.put(name, new Cities(name, latitude, longitude, isCity));
    }

    public void addEdge(String CitiesName1, String CitiesName2) {
        Cities Cities1 = cities.get(CitiesName1);
        Cities Cities2 = cities.get(CitiesName2);
        if (Cities1 != null && Cities2 != null) {
            edges.add(new Edges(Cities1, Cities2));
            edges.add(new Edges(Cities2, Cities1)); 
        }
    }

    public Map<String, Cities> getCities() {
        return cities;
    }

    public void setCities(Map<String, Cities> cities) {
        this.cities = cities;
    }

    public List<Edges> getEdges() {
        return edges;
    }

    public void setEdges(List<Edges> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "cities=" + cities +
                ", edges=" + edges +
                '}';
    }
}