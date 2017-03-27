package piggy2;

import java.io.IOException;
import java.nio.channels.SelectionKey;

//Parses and runs input commands

public class CommandParser {
	
	CommandParser(){
		
	}
	CommandParser(String[] args) throws NumberFormatException, IOException{
		parse(args);
	}
	
	public void parse(String[] args) throws NumberFormatException, IOException{
		SelectionKey sKey = null;
		if(args.length > 1){
			for(int i = 0;i<args.length;i++){
				switch(args[i]){
				
				case "-lhead": 
					i += 1;
					PigBody.heads.add(new PigSocket('h',Integer.parseInt(args[i]),sKey));
					System.out.println("raddr is " + args[i]);					
					break;
					
				case "-ltail": 
					i += 1;
					PigBody.tails.add(new PigSocket('t',Integer.parseInt(args[i]),sKey));
					System.out.println("laddr "+ args[i]);
					break;
					
				case "-chead":
					PigBody.heads.add(new PigSocket('h',Integer.parseInt(args[i+1]),args[i+2],sKey));
					i += 2;
					System.out.println("rport "+ args[i]);
					break;
					
				case "-ctail": 
					PigBody.tails.add(new PigSocket('h',Integer.parseInt(args[i+1]),args[i+2],sKey));
					i += 2;
					System.out.println("lport "+ args[i]);
					break;
				}
			}
		}
	}
}
