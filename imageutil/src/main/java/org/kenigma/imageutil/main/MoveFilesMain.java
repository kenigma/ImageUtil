package org.kenigma.imageutil.main;

import java.io.File;

import org.kenigma.imageutil.ImageUtil;

public class MoveFilesMain {

	public static void showUsage() {
		System.out.println("java MoveFilesMain cp|mv <srcDir> <destDir>");
	}
	
	public static void main(String[] args) {
		String cmd = args[0];
		boolean toMove = false;
		if(cmd.equals("mv")) {
			toMove = true;
		}
		
		String srcDirStr = args[1];
		String destDirStr = args[2];
				
		File srcDir = new File(srcDirStr);
		if(!srcDir.exists()) {
			System.out.println(srcDirStr + " not found!.");
			showUsage();
			System.exit(-1);
		}
		File destDir = new File(destDirStr);
		File thumbnailDir = null;
		if(args.length > 3) {
			thumbnailDir = new File(args[3]);
		}
		
		ImageUtil imgUtil = new ImageUtil();
		imgUtil.renameImages(srcDir, destDir, toMove, true, thumbnailDir);

	}
}
