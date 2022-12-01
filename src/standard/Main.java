package standard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Main {
	/*
	 * for each node: nodeID nodeID2 latitude longitude elevation for each edge:
	 * srcIDX trgIDX cost type maxspeed nodeID2, elevation, type and maxspeed can be
	 * ignored for now
	 */
	private double[][] cords; // [0][x] Lat, [1][x] Long
	private int[][] edges; // [0][x] scr [1][x] trgt [2][x] cost
	private int[] nodeToEdge;// [x] offset to find edges of x. NumEdges = [x+1]-[x]
	private long[] distanceFromOrigin;
	private int[] previous;
	private boolean[] checked;
	// new List[7798][9220];
	private List<Integer>[][] grid = new List[780][923]; // Lat/long
	private int gridFactor = 100;
	private final static double MINLATITUDE = 47.284;
	private final static double MINLONGITUDE = 5.8630;
	private int origin = 0;
	private int dest = 0;
	private File input = new File("./germany.fmi");
	private File que = new File("./germany.fmi");
	private File sol = new File("./germany.fmi");
	private PriorityQueue<Integer> queue = new PriorityQueue<>(new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return (int)Math.signum( ((distanceFromOrigin[o1] - distanceFromOrigin[o2])/128));
		}

	});

	public static void main(String[] args) {
		new Main(0);
		
	}
	public Main() {
		
	}
	
	private Main(int a) {
		buildArrays();
		long time = System.currentTimeMillis();
		createGrid();
		System.out.println("Time Grid for Closest Node: " + (System.currentTimeMillis() - time));
		// Test input
		//origin = 95020;
		//dest = 95021;
		
		time = System.currentTimeMillis();
		origin=nodeFromCoordinate( 48.746,9.098);
		dest=nodeFromCoordinate(48.665,9.118 );
		System.out.println("Time Closest Node: " + (System.currentTimeMillis() - time));

		time = System.currentTimeMillis();
		dijkstraOneToAll();
		System.out.println("Time Dijkstra: " + (System.currentTimeMillis() - time));
		System.out.println("Dijkstra Done");
		System.out.println(distanceFromOrigin[dest]);
		System.out.println(calcDistanceNodeNode(origin, dest));
		
	}
	
	private void reset() {
		int nodeNr=distanceFromOrigin.length;
		distanceFromOrigin = new long[nodeNr];
		previous = new int[nodeNr];
		checked = new boolean[nodeNr];
		Arrays.fill(distanceFromOrigin, Long.MAX_VALUE/16);
		distanceFromOrigin[origin] = 0;
	}
	
	
	public void setOrigin(int origin) {
		this.origin=origin;
	}
	public void setDestination(int dest) {
		this.dest=dest;
	}

	public void createGrid() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = new ArrayList<>();
			}
		}
//		double minLat = 100;
//		double minLon = 100;
//		double maxLat = 0;
//		double maxLon = 0;
		for (int i = 0; i < cords[0].length; i++) {

//			if (minLat > cords[0][i]) {
//				minLat = cords[0][i];
//			}
//			if (maxLat < cords[0][i]) {
//				maxLat = cords[0][i];
//			}
//			if (minLon > cords[1][i]) {
//				minLon = cords[1][i];
//			}
//			if (maxLon < cords[1][i]) {
//				maxLon = cords[1][i];
//			}
//			
			int lat = (int) ((cords[0][i] - MINLATITUDE) * gridFactor);
			int lon = (int) ((cords[1][i] - MINLONGITUDE) * gridFactor);
			grid[lat][lon].add(i);
		}
		System.out.println("Building grid done");
	}
	
	public int nodeFromCoordinate(double lat,double lon) {
		int gLat = (int) ((lat - MINLATITUDE) * gridFactor);
		int gLon = (int) ((lon - MINLONGITUDE) * gridFactor);
		
		List<Integer> possibleNodes=grid[gLat][gLon];
		int minNode=-1;
		double minDist=Double.MAX_VALUE;
		for(Integer i:possibleNodes) {
			double dist=calcDistanceNodeCoord(i, lat, lon);
			if(minDist> dist) {
				minDist=dist;
				minNode=i;
			}
		}
		return minNode;
		
	}
	
	public void dijkstraOneToAll() {
		reset();
		int currentNode=-1;
		updateChildren(origin);
		while (!queue.isEmpty()) {
			currentNode = queue.poll();
			if (!checked[currentNode]) {
				checked[currentNode] = true;
				updateChildren(currentNode);
			}

		}
	}

	public void dijkstraOneToOne() {
		reset();
		
		updateChildren(origin);
		int currentNode = -1;
		while (!queue.isEmpty() && !checked[dest]) {
			currentNode = queue.poll();
			if (!checked[currentNode]) {
				checked[currentNode] = true;
				updateChildren(currentNode);
			}
		}
	}

	private void updateChildren(int nodeID) {
		long parentDist = distanceFromOrigin[nodeID];
		for (int currentEdge = nodeToEdge[nodeID]; currentEdge < nodeToEdge[nodeID + 1]; currentEdge++) {
			int childNode = edges[1][currentEdge];
			if (!checked[childNode]) {
				// Updated distance and position in queue
				long potentialDist = parentDist + edges[2][currentEdge];
				if (distanceFromOrigin[childNode] > potentialDist) {
					distanceFromOrigin[childNode] = potentialDist;
					queue.add(childNode);
					previous[childNode] = nodeID;
				}
			}
		}
	}

	/*
	 * [0]=lat , [1]=lon
	 */
	public double[] coordsFromNode(int id) {
		double[] a= {cords[0][id],cords[1][id]};
		return a;
	}
	
	public long distFromNode(int id) {
		return distanceFromOrigin[id];
	}
	
	private double calcDistanceNodeNode(int id1, int id2) {
		double dX = 71.5 * (cords[1][id1] - cords[1][id2]);
		double dY = 111.3 * (cords[0][id1] - cords[0][id2]);

		return Math.sqrt(dX*dX + dY * dY);
	}

	private double calcDistanceNodeCoord(int id1, double lat, double lon) {
		double dX = 71.5 * (cords[1][id1] - lon);
		double dY = 111.3 * (cords[0][id1] - lat);

		return Math.sqrt(dX*dX + dY * dY);
	}

	public void readFile(String path) {
		input=new File(path);
		buildArrays();
	}

	private void buildArrays() {
		long l = System.currentTimeMillis();
		try {
			FileReader r = new FileReader(input);
			BufferedReader fr = new BufferedReader(r, 4096);
			boolean done = false;
			String line;
			String[] splitLine;
			int currentNode = 0;
			// Überspringt den Anfang
			for (int i = 0; i < 5; i++) {
				fr.readLine();
			}
			// Arrays initialisieren

			line = fr.readLine();
			int nodeNr = Integer.valueOf(line);
			cords = new double[2][nodeNr];
			nodeToEdge = new int[nodeNr + 1];
			distanceFromOrigin = new long[nodeNr];
			previous = new int[nodeNr];
			checked = new boolean[nodeNr];

			line = fr.readLine();
			int edgeNr = Integer.valueOf(line);
			edges = new int[3][edgeNr];
			nodeToEdge[nodeNr] = edgeNr;

			for (int i = 0; i < nodeNr; i++) {

				line = fr.readLine();
				splitLine = line.split(" ");
				cords[0][Integer.valueOf(splitLine[0])] = Double.valueOf(splitLine[2]);
				cords[1][Integer.valueOf(splitLine[0])] = Double.valueOf(splitLine[3]);
			}
			for (int i = 0; i < edgeNr; i++) {
				line = fr.readLine();
				splitLine = line.split(" ");
				if (currentNode != Integer.valueOf(splitLine[0])) {
					for (int j = currentNode + 1; j <= Integer.valueOf(splitLine[0]); j++) {
						nodeToEdge[j] = i;
					}
					currentNode = Integer.valueOf(splitLine[0]);
				}
				edges[0][i] = currentNode;
				edges[1][i] = Integer.valueOf(splitLine[1]);
				edges[2][i] = Integer.valueOf(splitLine[2]);
			}

			fr.close();
			System.out.println("Building Arrays done");
			System.out.println("Time: " + (System.currentTimeMillis() - l));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
