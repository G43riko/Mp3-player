package com.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileManager {
	public static void saveToFile(String file, String content){
		try{
		    PrintWriter writer = new PrintWriter(file, "UTF-8");
		    writer.println(content);
		    writer.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	public static void saveToFile(File file, String content){
		saveToFile(file.getAbsolutePath(), content);
	}
	public static String loadFromFile(String file){
		StringBuilder result = new StringBuilder();
		String line;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			while((line = in.readLine()) != null){
				result.append(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	public static String loadFromFile(File file){
		return loadFromFile(file.getAbsolutePath());
	}
}
