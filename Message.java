package piggy2;

public class Message {

	private String myMessage;
	// private List<filter> filters;
	// private List<desination> destinations;

	public Message(String messageContents) {
		myMessage = messageContents;
	}

	public void processFilters() {

		/*
		 * so... for each filter, myMessage = filter.run(myMessage);
		 * 
		 * Yeah that should work, and let me add generic filters and whatnot.
		 * 
		 * filter on receive, output to screen, filter before send...Yeah that
		 * should work.
		 */
	}

}