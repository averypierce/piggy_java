package piggy2;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PigBody {

	public static Selector selector;

	// DEFAULT PORTS//
	// private static int defaultPort = 36750;
	// private static int leftPort = 36751;
	// private static int rightPort = 36752;

	public static List<PigSocket> heads = new ArrayList<PigSocket>();
	public static List<PigSocket> tails = new ArrayList<PigSocket>();
	public static PigSocket master = null;

	public static void main(String args[]) throws IOException {
		SelectionKey sKey = null;
		selector = Selector.open();

		// i THINK the garbage collector will handle this properly
		new CommandParser(args);
		
		master = new PigAdmin('m', 36759, sKey);
		
		
		tails.add(new PigSocket('t',36751,sKey));
		tails.add(new PigSocket('t',36752,sKey));
		heads.add(new PigSocket('h',36753,sKey));
		
		
		while (true) {
			selector.select(500);
			Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

			while (iter.hasNext()) {
				sKey = iter.next();

				if (sKey.isAcceptable()) {

					PigSocket temp = (PigSocket) sKey.attachment();
					temp.pigAccept(sKey);

				} else if (sKey.isReadable()) {

					PigSocket temp = (PigSocket) sKey.attachment();
					temp.pigRead();

				}
				iter.remove();
			}
		}

	}
}
