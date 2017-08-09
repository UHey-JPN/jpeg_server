package main;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import console.ConsoleSocket;
import data.image.ImageList;
import window.main.LogToSystemIO;

public class TestServerMain {

	public static void main(String[] args) {
		Executor ex = Executors.newCachedThreadPool();
		LogToSystemIO log = new LogToSystemIO();
		new ConsoleSocket(ex, log, new ImageList("DB/img/", log));
	}

}
