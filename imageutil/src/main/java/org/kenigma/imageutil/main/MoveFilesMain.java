package org.kenigma.imageutil.main;

import java.io.File;

import org.kenigma.imageutil.ImageUtil;

public class MoveFilesMain {

	public static void showUsage() {
		System.out.println("java MoveFilesMain <srcDir> <destDir>");
	}
	
	public static void main(String[] args) {
		String srcDirStr = args[0];
		String destDirStr = args[1];
				
		File srcDir = new File(srcDirStr);
		if(!srcDir.exists()) {
			System.out.println(srcDirStr + " not found!.");
			showUsage();
			System.exit(-1);
		}
		File destDir = new File(destDirStr);
		File thumbnailDir = null;
		if(args.length > 2) {
			thumbnailDir = new File(args[2]);
		}
		
		ImageUtil imgUtil = new ImageUtil();
		imgUtil.renameImages(srcDir, destDir, true, true, thumbnailDir);

	}
}
