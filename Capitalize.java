package piggy2;

public class Capitalize implements Filter {

	public String applyFilter(String message) {
		
		message = message.toUpperCase();
		return message.trim() + "\r\n";
	}

}
