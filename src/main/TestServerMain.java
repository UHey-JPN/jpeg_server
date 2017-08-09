package main;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import console.ConsoleSocket;
import data.communication.DatabaseTCP;
import data.image.ImageList;
import data.main.Database;
import window.main.LogToSystemIO;

public class TestServerMain {

	public static void main(String[] args) {
		Executor ex = Executors.newCachedThreadPool();
		LogToSystemIO log = new LogToSystemIO();
		ImageList img_list = new ImageList("DB/img/", log);
		new ConsoleSocket(ex, log, img_list);
		new DatabaseTCP(ex, new Database(24), img_list);
	}

}
