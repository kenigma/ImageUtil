package org.kenigma.imageutil;

import java.io.File;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!");

		//File src = new File("/Users/kwyeung/Desktop/testRaw/fewSet/DSC_2205.JPG");
		//File src = new File("/Users/kwyeung/Desktop/testRaw/fewSet/P3233671.JPG");
		//File src = new File("/Users/kwyeung/Desktop/testRaw/fewSet/IMG_7693.JPG");
		//File src = new File("/Users/kwyeung/Desktop/testRaw/fewSet/DSC_20120101_201733.JPG");
		File src = new File("/Users/kwyeung/Desktop/testRaw/fewSet/20140118_163502.jpg");
		File dest = new File("D:\\temp\\001s.jpg");
		
		Metadata metadata = ImageMetadataReader.readMetadata(src);
		for (Directory directory : metadata.getDirectories()) {
		    for (Tag tag : directory.getTags()) {
		        System.out.println(tag);
		    }
		}
		ExifIFD0Directory ifd0Dir = metadata.getDirectory(ExifIFD0Directory.class);
		Date takenDate = ifd0Dir.getDate(ExifIFD0Directory.TAG_DATETIME);
		String model = ifd0Dir.getString(ExifIFD0Directory.TAG_MODEL);
		if(model != null) {
			model = model.trim().replace(' ', '_');
		}

		System.out.printf("date: <%s>\n", takenDate);
		System.out.printf("mode: <%s>\n", model);
		
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
