package piggy2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

/*
 * This is a special socket for configuring the pig interactively 
 * 
 */

public class PigAdmin extends PigSocket {

	private boolean authenticated = false;
	private int loginAttempts = 0;

	SelectionKey sKey = null;
	CommandParser cparser;

	public PigAdmin(Character name, int port, SelectionKey sKey) throws IOException {
		super(name, port, sKey);
		this.sKey = sKey;
		cparser = new CommandParser();
	}

	public void pigParse(ByteBuffer bb) throws NumberFormatException, IOException {
		String msg = new String(bb.array(), "UTF-8");
		System.out.println("admin input: " + msg);

		cparser.parse(msg.split("\\s+"));
	}

	public void pigAccept(SelectionKey sKey) throws IOException {

		ServerSocketChannel ssChannel = (ServerSocketChannel) sKey.channel();
		sc = ssChannel.accept();
		sc.configureBlocking(false);
		sc.register(PigBody.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, this);
		connected = true;

		String msg = "password: ";
		ByteBuffer blam = ByteBuffer.allocate(1024);
		blam.put(msg.getBytes());
		blam.flip();
		sc.write(blam);
	}

	// Read data from socket and decide what to do with it
	public void pigRead() throws IOException {

		ByteBuffer bb = ByteBuffer.allocate(1024);
		sc.read(bb);
		bb.flip();

		if (!authenticated) {
			String password = new String(bb.array(), "UTF-8");
			password = password.trim();
			if (password.equals("password")) {
				authenticated = true;
				System.out.println("remote admin has logged in");
			}

			else
				loginAttempts++;

			if (!authenticated) {

				if (loginAttempts > 4) {
					pigClose(sKey);
					// Timer to reset login attempts? blacklisting?
					loginAttempts = 0;
				} else {
					String msg = "password: ";
					//
					ByteBuffer blam = ByteBuffer.allocate(1024);
					blam.put(msg.getBytes());
					blam.flip();
					sc.write(blam);
				}

			}

		} else
			pigParse(bb);

	}
}
