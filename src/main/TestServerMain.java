package main;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import console.ConsoleSocket;
import window.main.LogToSystemIO;

public class TestServerMain {

	public static void main(String[] args) {
		Executor ex = Executors.newCachedThreadPool();
		new ConsoleSocket(ex, new LogToSystemIO());
	}

}
