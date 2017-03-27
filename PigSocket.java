package piggy2;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class PigSocket {

	protected SocketChannel sc = null;
	private Character name = null;

	private boolean display = true;
	private boolean forward = true; // Forward message to all sockets on other
									// end of pig
	private boolean echo = false;
	private boolean log = false;
	private boolean filter = false;

	private int bufsize = 1024;
	protected boolean connected = false;

	private PigLogger logger = null;

	public List<Filter> filters = new ArrayList<Filter>();

	public PigSocket(Character name, int port, SelectionKey sKey) throws IOException {
		this.name = name;
		try {

			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
			serverSocket.configureBlocking(false);
			serverSocket.bind(hostAddress);
			// pass THIS in order to associate this socket with selection key so
			// PigBody can access it
			serverSocket.register(PigBody.selector, SelectionKey.OP_ACCEPT, this);

		} catch (IOException e) {
			System.out.println("Exception trying to listen on port " + String.valueOf(port));
			System.out.println(e.getMessage());
		}
	}

	public PigSocket(Character name, int port, String destHost, SelectionKey sKey) throws IOException {
		this.name = name;
		try {

			InetSocketAddress hostAddress = new InetSocketAddress(destHost, port);
			SocketChannel clientSocket = SocketChannel.open(hostAddress);
			clientSocket.register(PigBody.selector, SelectionKey.OP_ACCEPT, this);

		} catch (IOException e) {
			System.out.println("Exception trying to connect to " + destHost + " " + destHost);
			System.out.println(e.getMessage());
		}
	}

	public void pigAccept(SelectionKey sKey) throws IOException {

		ServerSocketChannel ssChannel = (ServerSocketChannel) sKey.channel();
		sc = ssChannel.accept();
		sc.configureBlocking(false);
		sc.register(PigBody.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, this);
		connected = true;
	}

	public void pigClose(SelectionKey sKey) throws IOException {
		sc.close();
	}

	public void addFilter(Filter newFilter) {
		filters.add(newFilter);
		filter = true;
	}

	// Read data from socket and decide what to do with it
	public void pigRead() throws IOException {

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		try{
			sc.read(bb);
		}
		catch (ClosedChannelException e){
			System.out.println("EXCEPTION " + e.getMessage());
		}
		bb.flip();

		if (filter)
			bb = pigFilter(bb);
		if (display)
			pigDisplay(bb);
		if (forward)
			pigForward(bb);
		if (echo)
			pigEcho(bb);
		if (log)
			pigLog(bb);
	}

	private void pigLog(ByteBuffer bb) throws UnsupportedEncodingException {
		String msg = new String(bb.array(), "UTF-8");
		logger.log(msg);
	}

	public void enableLogger() {
		logger = new PigLogger();
		log = true;
	}

	private ByteBuffer pigFilter(ByteBuffer bb) throws UnsupportedEncodingException {

		String msg = new String(bb.array(), "UTF-8");
		ByteBuffer blam = ByteBuffer.allocate(10000);
		for (int i = 0; i < filters.size(); i++) {
			msg = filters.get(i).applyFilter(msg);
		}

		blam.put(msg.getBytes());
		blam.flip();
		return blam;
	}

	private void pigForward(ByteBuffer bb) throws IOException {

		List<PigSocket> temp = null;
		if (this.name == 'h') {
			temp = PigBody.tails;
		} else if (this.name == 't') {
			temp = PigBody.heads;
		}

		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).connected) {
				//System.out.println("sending to " + i);
				SocketChannel otherEnd = temp.get(i).getSocket();
				otherEnd.write(bb);
				bb.flip();
			}

		}

	}

	// Repeats message back to sender
	private void pigEcho(ByteBuffer bb) throws IOException {
		sc.write(bb);
	}

	// Prints messages to stdout
	private void pigDisplay(ByteBuffer bb) throws UnsupportedEncodingException {

		String msg = new String(bb.array(), "UTF-8");
		System.out.println(msg);
	}

	public Character getName() {
		return name;
	}

	public boolean isConnected() {
		return connected;
	}

	public SocketChannel getSocket() {
		return sc;
	}

}