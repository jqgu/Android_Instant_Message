import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

class ClientThread extends Thread {
	Socket socket;
	private DataInputStream reader;
	private DataOutputStream writer;
	private User user;
	private ArrayDeque<Message> messagePool;
	private static HashMap<String, ArrayDeque<Message>> map = new HashMap<>();

	public DataInputStream getReader() {
		return reader;
	}

	public DataOutputStream getWriter() {
		return writer;
	}

	public User getUser() {
		return user;
	}

	private int getDataLen(byte[] tmp) {
		// int value = (int) ((tmp[3] & 0xFF) | ((tmp[2] & 0xFF) << 8) |
		// ((tmp[1] & 0xFF) << 16)
		// | ((tmp[0] & 0xFF) << 24));
		int value = (tmp[0] & 0xFF);
		return value;
	}

	private String getData(byte[] tmp) {
		return new String(tmp);
	}

	public ClientThread(Socket socket) {
		try {
			this.socket = socket;
			reader = new DataInputStream(socket.getInputStream());
			writer = new DataOutputStream(socket.getOutputStream());

			byte[] tmp = new byte[1];
			reader.read(tmp, 0, 1);
			int dataLen = getDataLen(tmp);
			//System.out.println("length is " + dataLen);
			byte[] data = new byte[dataLen];
			for (int i = 0; i < dataLen; i++) {
				reader.read(data, i, 1);
			}
			String line = getData(data);

			System.out.println(line + socket.getInetAddress().getHostAddress() + " connected!");

			StringTokenizer st = new StringTokenizer(line, "@");
			String name = st.nextToken();
			user = new User(name, socket.getInetAddress().getHostAddress());

			messagePool = new ArrayDeque<>();
			synchronized (map) {
				map.put(user.getIP(), messagePool);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("Listen-Thread-"+user.getName());
				System.out.println(Thread.currentThread().getName()+" start listening pool");
				while(true){
					synchronized(messagePool){
						while(messagePool.size() > 0){
							Message message = messagePool.pollFirst();
							byte[] data = message.getData();
							String content = message.getContent();
							System.out.println(Thread.currentThread().getName()+" get message from pool: "+content);
							try {
								writer.write(data);
								writer.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		while (true) {
			Thread.currentThread().setName("Thread-"+user.getName());
			String message = null;
			try {
				System.out.println(Thread.currentThread().getName()+" waiting for the message coming...");
				byte[] tmp = new byte[1];
				reader.read(tmp, 0, 1);
				int dataLen = getDataLen(tmp);
				if(dataLen == 0){
					continue;
				}
				byte[] data = new byte[dataLen];
				for (int i = 0; i < dataLen; i++) {
					reader.read(data, i, 1);
				}
				message = getData(data);
				System.out.println(Thread.currentThread().getName()+" receive message " + message);

				byte[] content = new byte[1 + data.length];
				content[0] = tmp[0];
				for (int i = 1; i < content.length; i++) {
					content[i] = data[i - 1];
				}

				synchronized(map){
					for(String IP : map.keySet()){
						if(IP.equals(user.getIP()) == false){
							ArrayDeque<Message> messagePool = map.get(IP);
							synchronized(messagePool){
								messagePool.addLast(new Message(content, message));
							}
						}
					}
				}
//				System.out.println("ready to dispatch...");
//				for (int i = Server.clients.size() - 1; i >= 0; i--) {
//					if (Server.clients.get(i).getUser().equals(user) == false) {
//						System.out.println(user.getName()+" is sending message to "+Server.clients.get(i).getUser().getName()+" "+ message);
//						try {
//							DataOutputStream ttmp = Server.clients.get(i).getWriter();
//							synchronized (ttmp) {
//								ttmp.flush();
//								ttmp.write(content);
//								ttmp.flush();
//							}
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
