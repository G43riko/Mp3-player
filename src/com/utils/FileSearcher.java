package com.utils;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileSearcher {
	
	public static List<String> searchDir(String path){
		List<String> result = new LinkedList<String>();
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if(child.isDirectory()){
//					Thread t = new Thread(new Runnable() {
//						public void run() {
							result.addAll(searchDir(child.getAbsolutePath()));
//						}
//					});
//					t.start();
				}
				else{
					//result.add(child.getName());
					result.add(child.getAbsolutePath());
				}
			}
		}
		else {
			System.out.println("error");
		}
		return result;
	}
}
