package data.communication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import data.image.Image;
import data.image.ImageList;
import data.main.Database;

class ThreadDatabase implements Runnable {
	public static final String CRLF = "\r\n";
	
	private Socket soc;
	private Database database;
	private ImageList img_list;

	public ThreadDatabase(Socket soc, Database database, ImageList img_list) {
		this.soc = soc;
		this.database = database;
		this.img_list = img_list;
	}

	@Override
	public void run() {
		BufferedReader in;
		PrintWriter out;
        String line;
        String[] cmd;
        
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream(), true);

			line = in.readLine();

			if( line == null ) return;
			cmd = line.split(",");
			
			if( cmd[0].equals("robot") ){
				// get robot list ----------------------

			}else if( cmd[0].equals("team") ){
				// get team list ----------------------

			}else if( cmd[0].equals("tournament") ){
				// get tournament ----------------------

			}else if( cmd[0].equals("image") ){
				// get image ----------------------
				if(cmd.length != 2){
					out.println("NAK:ileagal command.".getBytes());
				}else{
					try {
						Image img = img_list.get(cmd[1]);
						soc.getOutputStream().write( ("ACK"+CRLF).getBytes() );
						img.upload_data(soc.getOutputStream());
						
					} catch (FileNotFoundException e){
						out.println("NAK:cannot find such a file(" + cmd[1] + ").");
					}
				}
				
			}else{
				// cancel the operation
				out.println("NAK");
				System.out.println("Requested data is failed");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				soc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
	}

}
