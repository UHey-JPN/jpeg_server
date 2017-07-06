package data.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import window.main.LogMessageAdapter;

public class Image {
	public static final String CRLF = "\r\n";
	
	private File file;
	
	private LogMessageAdapter log_mes;
	
	public Image(File file, LogMessageAdapter log_mes){
		this.log_mes = log_mes;
		this.file = file;
		
		if( !file.exists() ){
			try {
				file.createNewFile();
			} catch (IOException e) {
				log_mes.log_print(e);
			}
		}
	}
	
	
	/**
	 * プロトコルに従ってファイルを送信。InetSocketAddressを受け取る。
	 * このメソッドではソケットを開いて、管理するのみ。
	 * 実際の送信はupload(OutputStream)メソッドが実行。
	 * 
	 * @param addr - InetSocketAddress ファイルを送信する先を指定。
	 * @return
	 * {@code true} : 送信処理が正常に終了。<br>
	 * {@code false} : 送信処理が正常に終了。
	 * 
	 * @throws FileNotFoundException
	 */
	public boolean upload(InetSocketAddress addr) throws FileNotFoundException{
		// データ送信用のソケットを準備
		Socket soc = null;
		try {
			soc = new Socket();
			soc.connect(addr, 200);
			soc.setSoTimeout(2000);
			OutputStream out = soc.getOutputStream();
			log_mes.log_println("connected to server("+ soc.getRemoteSocketAddress() +")");
			
			boolean result = this.upload(out);
			soc.close();
			return result;
			
		} catch (IOException e) {
			log_mes.log_print(e);
			try {
				if(soc != null)
					soc.close();
			} catch (IOException e1) {
				log_mes.log_print(e1);
			}
			return false;
		}
		
	}
	
	
	/**
	 * プロトコルに従ってファイルを送信。OutputStreamを受け取る。
	 * 送信するデータは次のような感じ
	 * <p style="padding-left:2em">
	 *   name={@literal <name><CR><LF>}<br>
	 *   size=123456{@literal <CR><LF>}<br>
	 *   DATA本体<br>
	 * </p>
	 * 
	 * @param addr - OutputStream ファイルを送信する先を指定。
	 * @return
	 * {@code true} : 送信処理が正常に終了。<br>
	 * {@code false} : 送信処理が正常に終了。
	 * 
	 * @throws FileNotFoundException
	 */
	public boolean upload(OutputStream out) throws FileNotFoundException{
		// データ送信用のソケットを準備

		// コマンドとファイルサイズの出力
		try {
			String size_info = "";
			size_info += "name=" + file.getName() + CRLF;
			size_info += "size=" + Long.toString(file.length()) + CRLF;
			out.write(size_info.getBytes());
		} catch (IOException e) {
			log_mes.log_print(e);
			return false;
		}
		
		// ファイルデータの出力
		try {
			int len;
			byte[] buffer = new byte[512];
			
			FileInputStream file_in = new FileInputStream(file);
			
			while ( (len = file_in.read(buffer)) > 0 ) {
				out.write(buffer, 0, len);
			}
			file_in.close();
			return true;
			
		} catch (IOException e) {
			log_mes.log_print(e);
			return false;
		}
	}
	
	
	/**
	 * データ用のストリームを受け取って、ファイルに出力するメソッド。
	 * 
	 * @param in - データ用のInputStream
	 * @return 保存に成功したら{@code true}、失敗したら{@code false}
	 */
	public boolean receive(InputStream in){
		String header;
		
		// 改行が2つ来るまで待つ
		try {
			byte[] buf = new byte[512];
			
			for(int i = 0; ; i++){
				if( i >= buf.length ) return false;					// バッファーのオーバーラン
				if( in.available() > 0 ) buf[i] = (byte)in.read();	// Readから読み出せるなら読み出す
				header = new String(buf, 0, i+1);					// ヘッダー情報を保存
				if( (header+"line").split(CRLF).length == 3 ) break;// 改行で区切って、3つ以上
			}
		} catch (IOException e) {
			log_mes.log_print(e);
			return false;
		}
		
		// ヘッダー情報を抽出
		String[] header_line = header.split(CRLF);
		String[] header_info = new String[header_line.length];
		for(int i = 0; i < header_line.length ; i++){
			header_info[i] = header_line[i].split("=")[1];
		}
		
		// ヘッダー情報の確認（ファイル名）
		if( header_info[0].matches(file.getName()) ){
			log_mes.log_println("File which name is different is uploaded.");
			return false;
		}
		
		// データの読み込みと書き込み
		{
			byte[] buffer = new byte[512];
			
			// データの書き込み
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(file);
				int len = 0;
				while( (len = in.read(buffer)) > 0 ){
					fout.write(buffer, 0, len);
				}
			} catch (IOException e) {
				log_mes.log_print(e);
				return false;
			} finally {
				try {
					if( fout != null )
					fout.close();
				} catch (IOException e) {
					log_mes.log_print(e);
					return false;
				}
			}
		}
		
		// ファイルサイズの確認
		if( file.length() != Integer.parseInt(header_info[1]) ){
			return false;
		}
		
		return true;
		
	}

	/**
	 * 管理下にあるファイルを削除する。オブジェクトを破棄する前に呼ばれるべき。
	 * @return ファイルがなくなれば{@code true}を返す。
	 */
	public boolean delete(){
		if( file.exists() ){
			return file.delete();
		}
		return true;
	}
	
	/**
	 * ファイルの名前を取得する関数。
	 * 
	 * @return ファイル名をStringで返す。
	 */
	public String get_name(){
		return file.getName();
	}
	
	/**
	 * MD5を文字列形式で取得
	 * @return
	 * @throws FileNotFoundException
	 */
	public String get_md5_str() throws FileNotFoundException{
		byte[] md = get_md5();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < md.length; i++){
			int b = md[i] & 0xFF;
			if( b < 15 ) sb.append("0");
			sb.append(Integer.toHexString(b));
		}
		return sb.toString();
	}
	
	
	/**
	 * ハッシュ値をMD5で計算する。
	 *
	 * @return 基本はハッシュ値を返す。何かしらのエラーで{@code null}を返す。
	 * @throws FileNotFoundException
	 */
	public byte[] get_md5() throws FileNotFoundException{
		MessageDigest md;
		FileInputStream file_in = new FileInputStream(file);
		
		try {
			md = MessageDigest.getInstance("MD5");
			
			int len;
			byte[] buffer = new byte[512];
			while ( (len = file_in.read(buffer)) > 0 ) {
				md.update(buffer, 0, len);
			}
			
			file_in.close();
			
			return md.digest();
		} catch (NoSuchAlgorithmException|IOException e) {
			log_mes.log_print(e);
			return null;
		}
	}

}
