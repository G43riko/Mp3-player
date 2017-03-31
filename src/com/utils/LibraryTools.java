package com.utils;

import java.lang.reflect.Field;

import com.library.Song;

public class LibraryTools {

	
	private static void mergeAttribute(Song s1, Song s2, String attr){
		Object s1Attr = null, s2Attr = null;
		try{
			Class<?> c1 = s1.getClass();
			Field f1 = c1.getDeclaredField(attr);
			f1.setAccessible(true);
	
			s1Attr = f1.get(s1);

			Class<?> c2 = s2.getClass();
			Field f2 = c2.getDeclaredField(attr);
			f2.setAccessible(true);
	
			s2Attr = f2.get(s2);
			
			if(s1Attr == null){
				f1.set(s1, s2Attr);
			}
			else if(s2Attr != null){
				s1.setHistoricalData(attr, s2Attr.toString());
			}
		} catch(Exception e){
			System.out.println(e);
		}
	}
	
	private static void mergeHistoricalData(Song s1, Song s2){
		
	};
}
