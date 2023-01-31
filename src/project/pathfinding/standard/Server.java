package project.pathfinding.standard;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;

import com.sun.net.httpserver.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {
	volatile static RoutePlanner planner = new RoutePlanner();
	HttpServer server;
	static File f;

	public Server(String path) {
		planner.readFile(path);
		planner.createGrid();
		f = new File(".\\web\\site.html");
		try {
			server = HttpServer.create(new InetSocketAddress("localhost", 8080), 8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ready");
		server.createContext("/", new RedirectHandler());
		server.createContext("/test", new QueryHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class RedirectHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			FileInputStream fi = new FileInputStream(f);
			String response = new String(fi.readAllBytes());
			Headers h = t.getResponseHeaders();
			System.out.println(f.getAbsolutePath());
			h.set("Content-Type", "text/html");
			// h.set("Location",f.getAbsolutePath());
			// t.sendResponseHeaders(200, 0);
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();

			System.out.println("test");
			// Desktop.getDesktop().browse(f.toURI());
		}
	}

	static class QueryHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {

			Headers h = t.getRequestHeaders();
			String type = h.getFirst("type");

			if (type.equals("find")) {
				handleFind(t);
			} else if (type.equals("calc")) {
				handleCalc(t);
			}

		}

		private void handleFind(HttpExchange t) throws IOException {
			String response = "";
			String test = "";
			HashMap<String, Object> mapping = new HashMap();
			ObjectMapper mapper = new ObjectMapper();

			BufferedReader r = new BufferedReader(new InputStreamReader(t.getRequestBody()));
			while (r.ready()) {
				test += r.readLine();
			}
			// Bekommt lat long

			String[] temp = test.split(" ");
			double[] coords = new double[2];
			coords[0] = Double.valueOf(temp[0]);
			coords[1] = Double.valueOf(temp[1]);
			int node = planner.nodeFromCoordinate(coords[0], coords[1]);
			coords = planner.coordsFromNode(node);
			mapping.put("type", "Point");
			double[] coordsSwitched = { coords[1], coords[0] };
			mapping.put("coordinates", coordsSwitched);
			mapping.put("id", node);

			response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapping);
			System.out.println(response);
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.flush();
			os.close();
		}

		private void handleCalc(HttpExchange t) throws IOException {
			synchronized (planner) {
				System.out.println("Start of Calc");
				String response = "";
				String test = "";
				HashMap<String, Object> mapping = new HashMap();
				ObjectMapper mapper = new ObjectMapper();

				BufferedReader r = new BufferedReader(new InputStreamReader(t.getRequestBody()));
				while (r.ready()) {
					test += r.readLine();
				}
				// Bekommt lat long
				String[] temp = test.split(" ");
				int[] nodes = new int[2];
				nodes[0] = Integer.valueOf(temp[0]);
				nodes[1] = Integer.valueOf(temp[1]);
				planner.setOrigin(nodes[0]);
				planner.setDestination(nodes[1]);
				planner.dijkstraOneToOne();
				List<Integer> nodeList = planner.nodesOriginToDest();
				double[][] coordsArraySwapped = new double[nodeList.size()][2];
				for (int i = 0; i < nodeList.size(); i++) {
					double[] cSwaped = new double[2];
					cSwaped[0] = planner.coordsFromNode(nodeList.get(i))[1];
					cSwaped[1] = planner.coordsFromNode(nodeList.get(i))[0];
					coordsArraySwapped[i] = cSwaped;
				}
				mapping.put("type", "LineString");
				mapping.put("coordinates", coordsArraySwapped);
				response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapping);
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.flush();
				os.close();
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("Server Started");
		String graphPath = null;
		if (args.length > 0) {
			graphPath = args[1];
		}
		new Server(graphPath);
	}
}
