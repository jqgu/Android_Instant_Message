import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
	private ServerSocket server = null;
	static ArrayList<ClientThread> clients = new ArrayList<>();
	public static HashSet<User> userList = new HashSet<>();

	public Server() {
		try {
			server = new ServerSocket(8888);
		} catch (IOException e) {
			System.out.println("create server failed!");
			e.printStackTrace();
		}
		if (server != null) {
			new startService(server).start();
		}
	}
	public static void main(String[] args) {
		new Server();
	}
}

class startService extends Thread {
	ServerSocket server = null;

	public startService(ServerSocket server) {
		this.server = server;
	}

	public void run() {
		System.out.println("waiting for the connection...");
		while (true) {
			try {
				Socket socket = server.accept();
				ClientThread client = new ClientThread(socket);
				Server.clients.add(client);
				client.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
