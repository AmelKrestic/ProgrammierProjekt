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
import java.util.PriorityQueue;

public class Main {
	/*
	 * for each node: nodeID nodeID2 latitude longitude elevation for each edge:
	 * srcIDX trgIDX cost type maxspeed nodeID2, elevation, type and maxspeed can be
	 * ignored for now
	 */
	double[][] cords; // [0][x] Lat, [1][x] Long
	int[][] edges; // [0][x] scr [1][x] trgt [2][x] cost
	int[] nodeToEdge;// [x] offset to find edges of x. NumEdges = [x+1]-[x]
	long[] distanceFromOrigin;
	int[] previous;
	boolean[] checked;
	int origin = -1;
	int dest = -1;
	File input = new File("./germany.fmi");
	PriorityQueue<Integer> queue = new PriorityQueue<>(new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return (int) (distanceFromOrigin[o1] - distanceFromOrigin[o2]);
		}

	});

	public static void main(String[] args) {
		new Main();

	}
	
	
	public Main() {
		buildArrays();
		// Test input
		origin = 5020;
		dest = createDest(origin, 20);
		long time = System.currentTimeMillis();

		/*
		 * for(int i=0;i<nodeToEdge.length;i++) { if(nodeToEdge[i+1]-nodeToEdge[i]>5) {
		 * for(int j=nodeToEdge[i];j<nodeToEdge[i+1];j++) {
		 * System.out.println(edges[0][j]+" "+edges[1][j]); } System.out.println("t"); }
		 * System.out.println("Node:"+i+" Edges:"+nodeToEdge[i]); }
		 */

		// Distances set to infinity
		Arrays.fill(distanceFromOrigin, Long.MAX_VALUE);
		distanceFromOrigin[origin] = 0;
		enqueueChildren(origin);
		while (!queue.isEmpty()) {
			//System.out.println("queued:" + queue.size() + "  done:" + loopcounter);
			int currentNode = queue.poll();
			checked[currentNode]=true;
			// if(currentNode==dest) {
			// System.out.println("Correct Exit");
			// break;
			// }
			updateChildrenAlt(currentNode);
		}
		System.out.println(queue.isEmpty());
		System.out.println("Done?");
		System.out.println(distanceFromOrigin[dest] + "  maxdist: " + Integer.MAX_VALUE);
		System.out.println("Time: " + (System.currentTimeMillis() - time));
	}

	//1. Run: Time 68557
	//2. Run: Time 69756
	//3. Run: Time 69628
	private void updateChildren(int nodeID) {
		long parentDist = distanceFromOrigin[nodeID];
		for (int currentEdge = nodeToEdge[nodeID]; currentEdge < nodeToEdge[nodeID + 1]; currentEdge++) {
			int childNode = edges[1][currentEdge];
			if (!checked[childNode]) {
				// Updated distance and position in queue
				long potentialDist = parentDist + edges[2][currentEdge];
				if(!queue.contains(childNode)) {
					distanceFromOrigin[childNode] = potentialDist;
					queue.add(childNode);
					previous[childNode] = nodeID;
				}else if (distanceFromOrigin[childNode] > potentialDist) {
					queue.remove(childNode);
					distanceFromOrigin[childNode] = potentialDist;
					queue.add(childNode);
					previous[childNode] = nodeID;
				}
			}
		}
	}
	
	// 1. Run: Time 61545
	// 2. Run: Time 63887
	private void updateChildrenAlt(int nodeID) {
		long parentDist = distanceFromOrigin[nodeID];
		for (int currentEdge = nodeToEdge[nodeID]; currentEdge < nodeToEdge[nodeID + 1]; currentEdge++) {
			int childNode = edges[1][currentEdge];
			if (!checked[childNode]) {
				// Updated distance and position in queue
				long potentialDist = parentDist + edges[2][currentEdge];
				if (distanceFromOrigin[childNode] > potentialDist) {
					queue.remove(childNode);
					distanceFromOrigin[childNode] = potentialDist;
					queue.add(childNode);
					previous[childNode] = nodeID;
				}
			}
		}
	}

	private void enqueueChildren(int nodeID) {
		long parentDist = distanceFromOrigin[nodeID];
		for (int i = 0; i < nodeToEdge[nodeID + 1] - nodeToEdge[nodeID]; i++) {
			int currentEdge = nodeToEdge[nodeID + i];
			int childNode = edges[1][currentEdge];
			int cost = edges[2][currentEdge];

			// Updated distance and position in queue
			long potentialDist = parentDist + cost;
			if (distanceFromOrigin[childNode] > potentialDist) {
				queue.remove(childNode);
				distanceFromOrigin[childNode] = potentialDist;
				queue.add(childNode);
				previous[childNode] = nodeID;
			}
		}

	}

	private void enqueueAll() {
		for (int i = 0; i < nodeToEdge.length - 1; i++) {
			queue.add(i);
		}
	}

	private int createDest(int id, int steps) {
		int currentNode = id;
		int preNode = 0;
		for (int i = 0; i < steps; i++) {
			for (int edge = nodeToEdge[currentNode]; edge < nodeToEdge[currentNode + 1]; edge++) {
				if (edges[1][edge] != preNode) {
					currentNode = edges[1][edge];
				}
			}

		}
		return currentNode;
	}

	private double calcDistance(int id1, int id2) {
		double dLat = cords[0][id1] - cords[0][id2];
		double dLong = cords[1][id1] - cords[2][id2];

		return Math.sqrt(dLat * dLat + dLong * dLong);
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
			System.out.println("done");
			System.out.println("Time: " + (System.currentTimeMillis() - l));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(edges[0][125489] + " " + edges[1][125489] + " " + edges[2][125489]);
		System.out.println(cords[0][edges[0][125489]] + " " + cords[1][edges[0][125489]]);
		System.out.println(cords[0][edges[1][125489]] + " " + cords[1][edges[1][125489]]);
	}

	private class SimpleNode implements Comparable<SimpleNode> {

		int id = 0;
		int weight = 0;

		public SimpleNode(int id, int weight) {
			this.id = id;
			this.weight = weight;
		}

		@Override
		public int compareTo(SimpleNode o) {

			return weight - o.weight;
		}

		@Override
		public String toString() {

			return "id: " + id + " weight: " + weight;
		}
	}

}
