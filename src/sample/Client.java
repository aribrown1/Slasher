package sample;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

public class Client extends NetworkConnectionClient {

	private String ip;
	private int port;
	private String ID;
	
	public Client(String ip, int port, Consumer<Serializable> callback) {
		super(callback);
		this.ip = ip;
		this.port = port;
		System.out.println("creatintg client. port: " + this.port);
		this.ID = UUID.randomUUID().toString(); //create unique ID for client
	}

	@Override
	protected boolean isServer() {
	
		return false;
	}

	@Override
	protected String getIP() {
		// TODO Auto-generated method stub
		return this.ip;
	}

	@Override
	protected int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}

	protected String getID(){
		return this.ID;
	}

}
