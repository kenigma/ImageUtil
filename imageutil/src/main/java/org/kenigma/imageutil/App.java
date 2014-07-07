package org.kenigma.imageutil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!");

		File src = new File("D:\\temp\\001.jpg");
		File dest = new File("D:\\temp\\001s.jpg");
		
		ImageUtil imageUtil = new ImageUtil();
		
		imageUtil.resize(src, dest);
		
		
		
		System.out.println("DONE.");

	}
	
	
	public static void listFilesForFolder(final File folder) throws Exception {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	            System.out.println(fileEntry.getAbsolutePath());
	            System.out.println(fileEntry.getCanonicalPath());
	            System.out.println(fileEntry.getCanonicalFile());
	        }
	    }
	}
	
}
