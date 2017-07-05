package data.image;

import java.io.File;
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
		
		
		// 画像用に指示された場所がフォルダーかどうかを確認する
		if( !folder.isDirectory() ){
			log_mes.log_println("Image Folder " + img_folder + "is not a directory.");
			System.exit(1);
		}
	}
	
	
	public void update_list(){
		img_list.clear();
		File[] list = this.folder.listFiles();
		for(File f : list){
			img_list.add(new Image(f, log_mes));
		}
	}

}
