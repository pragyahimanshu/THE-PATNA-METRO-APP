import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Scanner;

class Station {
    String name;
    List<Edge> edges;

    Station(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }
}

class Edge {
    Station destination;
    double distance;
    double time;

    Edge(Station destination, double distance, double time) {
        this.destination = destination;
        this.distance = distance;
        this.time = time;
    }
}

class PathResult {
    List<String> path;
    double total;

    PathResult(List<String> path, double total) {
        this.path = path;
        this.total = total;
    }
}

class MapViewer extends JFrame {
    public MapViewer(String imagePath) {
        setTitle("Patna Metro Map - Developed by Pragya Himanshu, B.Tech CSE");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        ImageIcon mapIcon = new ImageIcon(imagePath);
        JLabel mapLabel = new JLabel(mapIcon);

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new GridLayout(2, 1));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legend"));
        legendPanel.add(new JLabel("Blue Line: Danapur Cantonment – Khemni Chak"));
        legendPanel.add(new JLabel("Orange Line: Patna Junction – New ISBT"));

        JPanel container = new JPanel(new BorderLayout());
        container.add(mapLabel, BorderLayout.CENTER);
        container.add(legendPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(container);
        add(scrollPane);
    }

    public static void showMap() {
        SwingUtilities.invokeLater(() -> {
            String imagePath = System.getProperty("user.dir") + "/Patna-metro-map_0_1200.png";
            MapViewer viewer = new MapViewer(imagePath);
            viewer.setVisible(true);
        });
    }
}

public class PatnaMetro {
    private Map<String, Station> stations;
    List<String> stationList;

    public PatnaMetro() {
        stations = new HashMap<>();
        addStations();
        addConnections();
    }

    private void addStations() {
        stationList = new ArrayList<>(Arrays.asList(
            "Danapur Cantonment", "Saguna More", "RPS More", "Patliputra", "Rukanpura", "Raja Bazar",
            "Patna Zoo", "Vikas Bhawan", "Vidyut Bhawan", "Akashvani", "Patna Junction", "Mithapur",
            "Ramkrishna Nagar", "Jaganpur", "Khemni Chak",
            "Gandhi Maidan", "PMCH", "Patna University", "Moin Ul Haq Stadium", "Rajendra Nagar",
            "Malahi Pakri", "Bhoothnath", "Zero Mile", "New ISBT"
        ));

        for (String name : stationList) {
            stations.putIfAbsent(name, new Station(name));
        }
    }

    private void addEdge(String from, String to, double distance, double time) {
        Station src = stations.get(from);
        Station dest = stations.get(to);
        src.edges.add(new Edge(dest, distance, time));
        dest.edges.add(new Edge(src, distance, time));
    }

    private void addConnections() {
        double approxDistance = 1.3;
        double avgSpeed = 30.0;
        double approxTime = (approxDistance / avgSpeed) * 60;

        String[][] line1 = {
            {"Danapur Cantonment", "Saguna More"}, {"Saguna More", "RPS More"}, {"RPS More", "Patliputra"},
            {"Patliputra", "Rukanpura"}, {"Rukanpura", "Raja Bazar"}, {"Raja Bazar", "Patna Zoo"},
            {"Patna Zoo", "Vikas Bhawan"}, {"Vikas Bhawan", "Vidyut Bhawan"}, {"Vidyut Bhawan", "Akashvani"},
            {"Akashvani", "Patna Junction"}, {"Patna Junction", "Mithapur"}, {"Mithapur", "Ramkrishna Nagar"},
            {"Ramkrishna Nagar", "Jaganpur"}, {"Jaganpur", "Khemni Chak"}
        };

        String[][] line2 = {
            {"Patna Junction", "Gandhi Maidan"}, {"Gandhi Maidan", "PMCH"}, {"PMCH", "Patna University"},
            {"Patna University", "Moin Ul Haq Stadium"}, {"Moin Ul Haq Stadium", "Rajendra Nagar"},
            {"Rajendra Nagar", "Malahi Pakri"}, {"Malahi Pakri", "Khemni Chak"},
            {"Khemni Chak", "Bhoothnath"}, {"Bhoothnath", "Zero Mile"}, {"Zero Mile", "New ISBT"}
        };

        for (String[] pair : line1) addEdge(pair[0], pair[1], approxDistance, approxTime);
        for (String[] pair : line2) addEdge(pair[0], pair[1], approxDistance, approxTime);
    }

    public PathResult dijkstra(String start, String end, boolean useTime) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<Map.Entry<String, Double>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());

        for (String station : stations.keySet()) dist.put(station, Double.MAX_VALUE);
        dist.put(start, 0.0);
        pq.add(new AbstractMap.SimpleEntry<>(start, 0.0));

        while (!pq.isEmpty()) {
            String current = pq.poll().getKey();
            if (current.equals(end)) break;

            for (Edge edge : stations.get(current).edges) {
                String neighbor = edge.destination.name;
                double weight = useTime ? edge.time : edge.distance;
                double newDist = dist.get(current) + weight;
                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    pq.add(new AbstractMap.SimpleEntry<>(neighbor, newDist));
                }
            }
        }

        List<String> path = new ArrayList<>();
        String at = end;
        if (!prev.containsKey(end) && !start.equals(end)) return new PathResult(path, Double.MAX_VALUE);
        while (at != null) {
            path.add(at);
            at = prev.get(at);
        }
        Collections.reverse(path);
        return new PathResult(path, dist.get(end));
    }

    private static String chooseStation(Scanner scanner, List<String> stationList, String prompt) {
        System.out.println(prompt);
        for (int i = 0; i < stationList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, stationList.get(i));
        }
        int index = scanner.nextInt() - 1;
        scanner.nextLine();
        return stationList.get(index);
    }

    private static void calculateFare(Scanner scanner, PatnaMetro metro) {
        String src = chooseStation(scanner, metro.stationList, "Select source station:");
        String dest = chooseStation(scanner, metro.stationList, "Select destination station:");
        PathResult res = metro.dijkstra(src, dest, false);
        double distance = res.total;

        double baseFare;
        if (distance <= 2) baseFare = 10;
        else if (distance <= 5) baseFare = 20;
        else if (distance <= 10) baseFare = 30;
        else baseFare = 40;

        System.out.println("Choose category:");
        System.out.println("1. General");
        System.out.println("2. Student (20% off)");
        System.out.println("3. Senior Citizen (30% off)");
        int category = scanner.nextInt();

        double discount = 0;
        if (category == 2) discount = 0.2;
        else if (category == 3) discount = 0.3;

        double finalFare = baseFare * (1 - discount);
        System.out.printf("Estimated fare from %s to %s: ₹%.2f\n", src, dest, finalFare);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PatnaMetro metro = new PatnaMetro();

        while (true) {
            System.out.println("===================================");
            System.out.println("        PATNA METRO MENU           ");
            System.out.println("===================================");
            System.out.println("1. List all stations");
            System.out.println("2. Show the metro map");
            System.out.println("3. Shortest distance between stations");
            System.out.println("4. Shortest time between stations");
            System.out.println("5. Shortest path (by distance)");
            System.out.println("6. Shortest path (by time)");
            System.out.println("7. Calculate fare with discounts");
            System.out.println("8. Exit");
            System.out.println("-----------------------------------");
            System.out.println("Developed by Pragya Himanshu, B.Tech CSE");
            System.out.println("===================================");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nStations:");
                    for (String s : metro.stationList) System.out.println("- " + s);
                    break;
                case 2:
                    MapViewer.showMap();
                    break;
                case 3: {
                    String src = chooseStation(scanner, metro.stationList, "Select source station:");
                    String dest = chooseStation(scanner, metro.stationList, "Select destination station:");
                    PathResult res = metro.dijkstra(src, dest, false);
                    System.out.printf("Shortest distance from %s to %s: %.2f km\n", src, dest, res.total);
                    break;
                }
                case 4: {
                    String src = chooseStation(scanner, metro.stationList, "Select source station:");
                    String dest = chooseStation(scanner, metro.stationList, "Select destination station:");
                    PathResult res = metro.dijkstra(src, dest, true);
                    System.out.printf("Shortest travel time from %s to %s: %.2f minutes\n", src, dest, res.total);
                    break;
                }
                case 5: {
                    String src = chooseStation(scanner, metro.stationList, "Select source station:");
                    String dest = chooseStation(scanner, metro.stationList, "Select destination station:");
                    PathResult res = metro.dijkstra(src, dest, false);
                    System.out.println("Shortest path (by distance): " + String.join(" -> ", res.path));
                    System.out.printf("Total distance: %.2f km\n", res.total);
                    break;
                }
                case 6: {
                    String src = chooseStation(scanner, metro.stationList, "Select source station:");
                    String dest = chooseStation(scanner, metro.stationList, "Select destination station:");
                    PathResult res = metro.dijkstra(src, dest, true);
                    System.out.println("Fastest route (by time):");
                    System.out.println(String.join(" -> ", res.path));
                    System.out.printf("Total travel time: %.2f minutes\n", res.total);
                    break;
                }
                case 7:
                    calculateFare(scanner, metro);
                    break;
                case 8:
                    System.out.println("Thank you for using Patna Metro App!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
