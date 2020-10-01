package uk.co.boots.server;

public interface SocketServer{
	public final static int START_FRAME = 10;
	public final static int ALTERNATIVE_START_FRAME = 32;
	public final static int END_FRAME = 13;
	
	public final static char START_FRAME_CHAR = '\n';
	public final static char ALTERNATIVE_START_FRAME_CHAR = ' ';
	public final static char END_FRAME_CHAR = '\r';
	
	public void startServer();
}
