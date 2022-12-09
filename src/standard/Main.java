package standard;

import java.util.Scanner;

public class Main {

	private Scanner sc = new Scanner(System.in);
	private RoutePlanner rp = new RoutePlanner();

	public Main(String path) {
		if (path == null || path.isEmpty()) {
			System.out.println("Input Graph Path");
			path = sc.nextLine();
		}

		System.out.println("loading Graph");
		rp.readFile(path);
		System.out.println("creating grid sctructure");
		rp.createGrid();
		int src = 0;
		int dest = 0;
		int depth = 1;
		while (depth > 0) {
			System.out.println("One-To-(O)ne | One-To-(A)ll | E(X)IT");
			String input = sc.nextLine().toLowerCase();
			if (input.equals("x") || input.equals("exit")) {
				return;
			}
			if (input.equals("o") || input.equals("one")) {

				System.out.println("Input Source Node");
				src = inputNode();
				System.out.println("Input Destination Node");
				dest = inputNode();
				System.out.println("Starting one to one Dijkstra");
				rp.setOrigin(src);
				rp.setDestination(dest);
				rp.dijkstraOneToOne();

			}
			if (input.equals("a") || input.equals("all")) {
				depth++;
				while (depth > 1) {
					System.out.println("Input Source Node");
					src = inputNode();
					rp.setOrigin(src);
					rp.dijkstraOneToAll();
					depth++;
					while (depth > 2) {

						System.out.println("(N)ode | (R)eturn to Source | Return to (B)eginning");

						input = sc.nextLine().toLowerCase();
						if (input.equals("b") || input.equals("beginning")) {
							depth -= 2;
						}
						if (input.equals("r") || input.equals("return")) {
							depth--;
						}
						if (input.equals("n") || input.equals("node")) {
							System.out.println("Input target Node");
							int target = inputNode();
							System.out.println("Distance: " + rp.distFromNode(target));
						}
					}
				}
			}
		}
	}

	private int inputNode() {
		int src = -1;
		while (src < 0) {
			System.out.println("(I)d | (C)oord");
			String input = sc.nextLine().toLowerCase();
			if (input.equals("i") || input.equals("id")) {
				System.out.println("Input id");
				src = sc.nextInt();
			}
			if (input.equals("c") || input.equals("coord")) {
				System.out.println("Input Latitude");
				double lat = sc.nextDouble();
				System.out.println("Input Longitude");
				double lon = sc.nextDouble();
				src = rp.nodeFromCoordinate(lat, lon);
			}
		}
		return src;
	}

	public static void main(String[] args) {
		String graphPath = null;
		if (args.length > 0) {
			graphPath = args[1];
		}
		new Main(graphPath);
	}
}
