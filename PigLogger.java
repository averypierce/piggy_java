package piggy2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PigLogger {

	public void log(String message){
		
		  PrintWriter printWriter = null;
		  try {
		     printWriter = new PrintWriter(
		    		 		new FileWriter(
		    		 		new File("piggyLog.txt"), true));
		     printWriter.println(message);
		  } catch (IOException e) {
		     e.printStackTrace();
		  } finally {
			if (printWriter != null) {
			    	printWriter.close();
			 }
		  }
	}
}
