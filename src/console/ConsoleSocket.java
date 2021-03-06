package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

import data.image.ImageList;
import window.main.LogMessageAdapter;


public class ConsoleSocket implements Runnable{
	private ServerSocket listen = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private ImageList img_list = null;

	public ConsoleSocket(Executor ex, LogMessageAdapter log_mes, ImageList img_list) {
		try {
			listen = new ServerSocket(50000, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Console socket is opened(port number = " + get_local_port() + ").");
		
		this.img_list = img_list;
		
		// スレッドの起動
		ex.execute(this);
	}
	
	public int get_local_port(){
		return listen.getLocalPort();
	}

	@Override
	public void run() {
		Socket soc = null;
		
		while(true){
			try {
				boolean login = true;
				
				// クライアントからの接続要求を待つ。
				System.out.println("CONSOLE : wait for console connection...");
				soc = listen.accept(); 
				String addr_remote = soc.getInetAddress().getHostAddress() + "(" + soc.getPort() + ")";
				System.out.println("connect from " + addr_remote);
				
				// 入出力ストリームの生成
				in  = new BufferedReader(new InputStreamReader(soc.getInputStream()) );
				out = new PrintWriter(soc.getOutputStream(), true);
				
				// コンソールからの命令を処理
				while(login){
					// コマンドライン命令の受け取り
					String str_cmd = in.readLine();
					if( str_cmd == null ) break;
					String[] cmd = str_cmd.split(" ");
					
					// 構文の解析
					if( cmd[0].equals("set") ){
						// syntax of setter ------------------------------
						
					}else if( cmd[0].equals("get") ){
						// syntax of getter ------------------------------
						
					}else if( cmd[0].equals("add") ){
						// syntax of add ------------------------------
						
					}else if( cmd[0].equals("image") ){
						// syntax of image ------------------------------
						if( cmd[1].equals("add") ){
							img_list.receive_img(cmd[2], out);
							System.out.println("uploading image finished(name = " + cmd[2] + ").");
						}else if( cmd[1].equals("list") ){
							img_list.update_list();
							out.println(img_list.get_md5_list());
							System.out.println("return the hash list.");
						}
						
					}else if( cmd[0].equals("clear") ){
						// syntax of add ------------------------------
						
					}else if( cmd[0].equals("exit") ){
						// syntax of exit ------------------------------
						out.println("finish to operation.Logout.");
						System.out.println("Logout.");
						break;
						
					}else{
						out.println("err:0:there is no such a command:" + cmd[0]);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					soc.close();
					System.out.println("ConsoleSocket is closed.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}



}
