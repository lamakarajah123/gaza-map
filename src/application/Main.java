package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {
	private Graph graph;
	private Cities startCity = null;
	private Cities endCity = null;
	Pane pane;
	private Map<Cities, Rectangle> cityMarkers = new HashMap<>();
	private Label selectionMessageLabel;
	private List<Line> pathLines = new ArrayList<>();
	private List<Polygon> pathLine = new ArrayList<>();
	private ComboBox<String> startCityBox;
	private ComboBox<String> endCityBox;
	private Button showPathButton;
	private TextArea pathTextArea;
	private TextField distanceTextArea;
	private Button res;
	List<Cities> cities;

	@Override
	public void start(Stage stage) throws Exception {
		pane = new Pane();
		selectionMessageLabel = new Label();
		selectionMessageLabel.setLayoutX(10);
		selectionMessageLabel.setLayoutY(10);
		pane.getChildren().add(selectionMessageLabel);
		ImageView mapView = new ImageView("C:\\Users\\user\\Desktop\\secpro\\algog\\src\\application\\gaza.jpg");
		pane.getChildren().add(mapView);
		initializeGraph();

		cities = readCitiesFromFile("C:\\Users\\user\\Desktop\\secpro\\algog\\src\\application\\cities.txt");
		for (Cities city : cities) {
			if (!city.getIssCity())
				continue;

			double[] pixelCoordinates = convert(city.getLatitude(), city.getLongitude());
			Rectangle cityMarker = new Rectangle(pixelCoordinates[0], pixelCoordinates[1], 10, 10);
			cityMarker.setFill(Color.YELLOW);

			Text cityLabel = new Text(city.getCity());
			cityLabel.setX(pixelCoordinates[0]);
			cityLabel.setY(pixelCoordinates[1]);

			cityLabel.setOnMouseClicked(event -> handleMouseClick(event, city));
			cityMarkers.put(city, cityMarker);
			pane.getChildren().addAll(cityMarker, cityLabel);
		}
		startCityBox = new ComboBox<>();
		endCityBox = new ComboBox<>();
		showPathButton = new Button("RUN");
		pathTextArea = new TextArea();
		pathTextArea.setMaxWidth(200);
		distanceTextArea = new TextField();

		res = new Button("Reset");

		HBox hBox = new HBox(20);
		hBox.getChildren().addAll(showPathButton, res);

		HBox hBox1 = new HBox(20);
		hBox1.getChildren().addAll(new Label("Source:"), startCityBox);

		HBox hBox2 = new HBox(20);
		hBox2.getChildren().addAll(new Label("Target:"), endCityBox);

		for (Cities city : cities) {
			if (!city.getIssCity())
				continue;
			String cityName = city.getCity();
			startCityBox.getItems().add(cityName);
			endCityBox.getItems().add(cityName);
		}

		VBox controls = new VBox(10, hBox1, hBox2, hBox, new Label("Path:"), pathTextArea, new Label("Distance:"),
				distanceTextArea);
		controls.setLayoutX(650); 
		controls.setLayoutY(10);
		pane.getChildren().add(controls);

		showPathButton.setOnAction(event -> {
			String startCityName = startCityBox.getValue();
			String endCityName = endCityBox.getValue();
			pathTextArea.setText("");
			if (startCityName != null && endCityName != null) {
				startCity = graph.getCities().get(startCityName);
				endCity = graph.getCities().get(endCityName);
				displayShortestPath();
			} else {
				pathTextArea.setText("Select Source and Target first!!\n");
			}
		});

		res.setOnAction(e -> {
			startCityBox.setValue("");
			endCityBox.setValue("");
			pathTextArea.clear();
			distanceTextArea.clear();
			clearPreviousPaths();
			clearArrows();
		});

		Scene scene = new Scene(pane, 900, 695);
		stage.setTitle("Map of Gaza Strip");
		stage.setScene(scene);
		stage.show();
	}

	private void initializeGraph() {
		graph = new Graph();

		
		List<Cities> citiesList = readCitiesFromFile(
				"C:\\Users\\user\\Desktop\\secpro\\algog\\src\\application\\cities.txt");
		for (Cities city : citiesList) {
			graph.addCities(city.getCity(), city.getLatitude(), city.getLongitude(), city.getIssCity());
		}

	
		readEdgesFromFile("C:\\Users\\user\\Desktop\\secpro\\algog\\src\\application\\edges.txt");
	}

	private void handleMouseClick(MouseEvent event, Cities city) {

		if (startCity == null) {
			startCity = city;
			startCityBox.setValue(city.getCity());
		} else if (endCity == null) {
			endCity = city;
			endCityBox.setValue(city.getCity());
		} else {
			startCity = city;
			endCity = null;
			endCityBox.setValue(null);
			startCityBox.setValue(city.getCity());
		}
	}
	private void drawPath(List<Cities> path) {
	    for (int i = 0; i < path.size() - 1; i++) {
	        Cities city1 = path.get(i);
	        Cities city2 = path.get(i + 1);

	        double[] pixel1 = convert(city1.getLatitude(), city1.getLongitude());
	        double[] pixel2 = convert(city2.getLatitude(), city2.getLongitude());

	        Line line = new Line(pixel1[0], pixel1[1], pixel2[0], pixel2[1]);
	        line.setStroke(Color.GREEN);
	        line.setStrokeWidth(2);

	        // Add arrow at the end of the line
	        addArrow(pane, pixel2[0], pixel2[1], pixel1[0], pixel1[1]);

	        pane.getChildren().add(line);
	        pathLines.add(line);
	    }
	} 

	
	private void clearPreviousPaths() {
		for (Line line : pathLines) {
			pane.getChildren().remove(line);
		}
		pathLines.clear();
	}
	
	private void clearArrows() {
	    for (Polygon arrow : pathLine) {
	        pane.getChildren().remove(arrow);
	    }
	    pathLine.clear();
	}


	
	private void addArrow(Pane pane, double x1, double y1, double x2, double y2) {
	    double arrowSize = 10;

	    // Calculate arrow angles
	    double angle = Math.atan2((y2 - y1), (x2 - x1));
	    double arrowX = x1 + arrowSize * Math.cos(angle);
	    double arrowY = y1 + arrowSize * Math.sin(angle);

	    // Create arrow shape
	    Polygon arrow = new Polygon();
	    arrow.getPoints().addAll(new Double[]{
	            x1, y1,
	            arrowX + 5 * Math.cos(angle + Math.PI / 2), arrowY + 5 * Math.sin(angle + Math.PI / 2),
	            arrowX + 5 * Math.cos(angle - Math.PI / 2), arrowY + 5 * Math.sin(angle - Math.PI / 2)
	    });
	    arrow.setFill(Color.GREEN);

	    pane.getChildren().add(arrow);
	}
	
	private double[] convert(double latitude, double longitude) {
		double mapWidth = 589;
		double mapHeight = 695;
		double mapMinLat = 31.208163033163977;
		double mapMaxLat = 31.614521165206845;
		double mapMinLon = 34.1707489947603;
		double mapMaxLon = 34.575060834817954;

		double xPercent = (longitude - mapMinLon) / (mapMaxLon - mapMinLon);
		double yPercent = (latitude - mapMinLat) / (mapMaxLat - mapMinLat);

		int xPixel = (int) (xPercent * mapWidth);

		int yPixel = (int) ((1 - yPercent) * mapHeight);

		return new double[] { xPixel, yPixel };
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Map<Cities, Double> distances;
	private Map<Cities, Cities> predecessors;
	private Set<Cities> settledNodes;
	private PriorityQueue<Cities> unsettledNodes;

	private void dijkstraAlgorithm(Cities source) {
		predecessors = new HashMap<>();
		settledNodes = new HashSet<>();
		distances = new HashMap<>();
		unsettledNodes = new PriorityQueue<>(Comparator.comparing(distances::get));

		for (Cities city : graph.getCities().values()) {
			distances.put(city, Double.MAX_VALUE);
		}
		distances.put(source, 0.0);

		unsettledNodes.add(source);

		while (!unsettledNodes.isEmpty()) {
			Cities currentCity = unsettledNodes.poll();
			settledNodes.add(currentCity);
			examineNeighbors(currentCity);
		}
	}
	

	private void examineNeighbors(Cities city) {
		List<Cities> adjacentCities = getNeighbors(city);
		for (Cities target : adjacentCities) {
			if (settledNodes.contains(target)) {
				continue;
			}
			double edgeWeight = getDistance(city, target);
			double sourceDistance = distances.get(city);

			if (sourceDistance + edgeWeight < distances.get(target)) {
				distances.put(target, sourceDistance + edgeWeight);
				predecessors.put(target, city);
				unsettledNodes.add(target);
			}
		}
	}

	private double getDistance(Cities city1, Cities city2) {
		for (Edges edge : graph.getEdges()) {
			if ((edge.getCity1().equals(city1) && edge.getCity2().equals(city2))
					|| (edge.getCity1().equals(city2) && edge.getCity2().equals(city1))) {
				return edge.getWeight();
			}
		}
		return Double.MAX_VALUE;
	}

	private List<Cities> getNeighbors(Cities city) {
		List<Cities> neighbors = new ArrayList<>();
		for (Edges edge : graph.getEdges()) {
			if (edge.getCity1().equals(city)) {
				neighbors.add(edge.getCity2());
			} else if (edge.getCity2().equals(city)) {
				neighbors.add(edge.getCity1());
			}
		}
		return neighbors;
	}

	private List<Cities> getPath(Cities target) {
		List<Cities> path = new ArrayList<>();
		for (Cities city = target; city != null; city = predecessors.get(city)) {
			path.add(city);
		}
		Collections.reverse(path);
		return path;
	}

	private void displayShortestPath() {
		for (Cities city : cities) {
			if (!Objects.equals(city.getCity(), startCity.getCity())
					&& !Objects.equals(city.getCity(), endCity.getCity())) {
			}
		}
		if (startCity != null && endCity != null) {
			dijkstraAlgorithm(startCity);
			List<Cities> path = getPath(endCity);

			clearPreviousPaths();
			drawPath(path);
			pathTextArea.setText(path.stream().map(Cities::getCity).collect(Collectors.joining(" -> ")));
			distanceTextArea.setText(String.valueOf(distances.getOrDefault(endCity, 0.0)));
		}
	}

	
	public static List<Cities> readCitiesFromFile(String fileName) {
		List<Cities> citiesList = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(", ");
				if (parts.length >= 2) {
					String city = parts[0];
					double latitude = Double.parseDouble(parts[1]);
					double longitude = Double.parseDouble(parts[2]);
					boolean isCity = true;
					if (parts[0].charAt(0) == 'r')
						isCity = false;
					citiesList.add(new Cities(city, latitude, longitude, isCity));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return citiesList;
	}
	

    public void readEdgesFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String city1 = parts[0].trim();
                    String city2 = parts[1].trim();
                  graph.addEdge(city1, city2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
