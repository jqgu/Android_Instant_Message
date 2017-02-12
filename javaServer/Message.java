public class Message {
	private byte[] data;
	private String content;
	public Message(byte[] data, String content){
		this.data = data;
		this.content = content;
	}
	
	public byte[] getData(){
		return this.data;
	}
	
	public String getContent(){
		return this.content;
	}
}
