package data.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import window.main.LogMessageAdapter;

public class ImageList {
	private List<Image> img_list;
	private File folder;
	private LogMessageAdapter log_mes;

	public ImageList(String img_folder, LogMessageAdapter log_mes) {
		img_list = Collections.synchronizedList(new ArrayList<Image>());
		this.folder = new File(img_folder);
		this.log_mes = log_mes;
		
		// フォルダーが存在しない場合、フォルダを生成
		if( !folder.exists() ){
			folder.mkdir();
		}
		
		// 画像用に指示された場所がフォルダーかどうかを確認する
		if( !folder.isDirectory() ){
			log_mes.log_println("Image Folder " + img_folder + "is not a directory.");
			System.exit(1);
		}
	}
	
	public void receive_img(String name, PrintWriter out){
		Image img;
		ServerSocket listen = null;
		Socket soc = null;
		
		// 画像ファイル用のインスタンスを検索、なければ生成。
		try {
			img = this.get(name);
		} catch (FileNotFoundException e) {
			log_mes.log_println("create file " + name);
			img = new Image(new File(name), log_mes);
		}
		
		// ソケットを通知して、接続待ち
		try {
			// 待機用ソケットの生成
			listen = new ServerSocket(0, 1);
			
			// ソケットを通知して、接続待ち
			String addr = InetAddress.getLocalHost().getHostAddress();
			out.println(addr + "," + listen.getLocalPort() );
			soc = listen.accept();
			
			// データの受信
			boolean result = img.receive(soc.getInputStream());
			
			// 受信プロセス結果の送信
			if(result){
				out.println("err:error has occurred during upload");
			}else{
				out.println("OK");
			}

		} catch (IOException e) {
			log_mes.log_print(e);
			out.println("err:IOExceptino has occurred about socket");
			
		} finally {
			try {
				if( soc != null) soc.close();
				if( listen != null ) listen.close();
			} catch (IOException e) {
				log_mes.log_print(e);
			}
		}
		
	}
	
	public Image get(String name) throws FileNotFoundException{
		for(Image img : img_list){
			if( img.get_name().equals(name) ){
				return img;
			}
		}
		throw new FileNotFoundException(name + "is not found.");
	}
	
	public void update_list(){
		img_list.clear();
		File[] list = this.folder.listFiles();
		for(File f : list){
			img_list.add(new Image(f, log_mes));
		}
	}

}