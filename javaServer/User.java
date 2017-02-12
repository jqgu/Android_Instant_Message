public class User {
	private String name;
	private String IP;
	public static int UserPort = 8888;
	public User(String name, String IP){
		this.name = name;
		this.IP = IP;
	}
	
	public String getName(){return this.name;}
	public String getIP() {return this.IP;}

	@Override
	public int hashCode(){
		return this.IP.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof User == false){
			return false;
		}
		User user = (User)obj;
		return this.IP.equals(user.getIP());
	}
}
