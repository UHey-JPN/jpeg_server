package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;


public class ConsoleSocket implements Runnable{
	private ServerSocket listen = null;
	private static String PASSWORD = "chiba.robot.studio";
	private BufferedReader in = null;
	private PrintWriter out = null;

	public ConsoleSocket(Executor ex) {
		try {
			listen = new ServerSocket(0, 2);
			// listen = new ServerSocket(55123, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Console socket is opened(port number = " + get_local_port() + ").");
		
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
				boolean login = false;
				
				// クライアントからの接続要求を待つ。
				System.out.println("CONSOLE : wait for console connection...");
				soc = listen.accept(); 
				String addr_remote = soc.getInetAddress().getHostAddress() + "(" + soc.getPort() + ")";
				System.out.println("connect from " + addr_remote);
				
				// 入出力ストリームの生成
				in  = new BufferedReader(new InputStreamReader(soc.getInputStream()) );
				out = new PrintWriter(soc.getOutputStream(), true);
				
				// -----------------------------------------------------------
				// 認証 -------------------------------------------------------
				// 法を256としたアフィン暗号
				// password : chiba.robot.studio
				
				// 鍵の生成
				int[] i_key = new int[2];
				i_key[0] = ( (int)(Math.random()*128) ) * 2 + 1 ;
				i_key[1] = (int)( Math.random() * 256 );
				System.out.println("key = " + i_key[0] + "," + i_key[1]);
				// 鍵を送信
				out.println( "" + i_key[0] + "," + i_key[1]);
				
				// パスワード暗号文の生成と確認
				String recv_pass = in.readLine();
				char[] true_pass = new char[PASSWORD.length()];
				for(int i = 0; i < PASSWORD.length(); i++){
					true_pass[i] = (char)( (PASSWORD.charAt(i)*i_key[0] + i_key[1])%256);
				}
				if( recv_pass != null ){
					//if( Arrays.equals(recv_pass.toCharArray(), true_pass) ){
					if( recv_pass.equals(PASSWORD) ){
						System.out.println("login successed.");
						out.println("Login : ACK");
						login = true;
					}else{
						System.out.println("login failed.");
						out.println("Login : NAK");
						login = false;
					}
				}else{
					login = false;
				}
				
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
