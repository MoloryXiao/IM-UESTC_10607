package Server;

import Network.Server.Envelope;

public class Message {
	
	private char msgType;
	private Envelope envelope = null;
	
	public Message( char msgType, Envelope envelope){
		this.msgType = msgType;
		this.envelope = envelope;
	}
	
	
	public char getMsgType() {
		
		return msgType;
	}
	
	public Envelope getEnvelope() {
		
		return envelope;
	}
}
