package org.kenigma.imageutil.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.kenigma.imageutil.ImageUtil;

public class CopyFilesMain {

	public static void showUsage() {
		System.out
				.println("java CopyFilesMain [cp|mv] <propertiesFile> <srcDir> <destDir> [<thumbnailDir>]");
	}

	public static void main(String[] args) {
		try {
			boolean toMove = false;
			if (args[0].equals("mv")) {
				toMove = true;
			}

			String propertiesFileStr = args[1];
			Properties prop = readProperties(propertiesFileStr);

			String srcDirStr = args[2];
			String destDirStr = args[3];

			File srcDir = new File(srcDirStr);
			if (!srcDir.exists()) {
				System.out.println(srcDirStr + " not found!.");
				showUsage();
				System.exit(-1);
			}
			File destDir = new File(destDirStr);
			File thumbnailDir = null;
			if (args.length > 4) {
				thumbnailDir = new File(args[4]);
			}

			ImageUtil imgUtil = new ImageUtil();
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				imgUtil.setProps(key, value);
				System.out.printf("Event desc: %s: %s\n", key, value);
			}
			imgUtil.renameImages(srcDir, destDir, toMove, true, thumbnailDir);
		} catch (Exception e) {
			e.printStackTrace();
			showUsage();
		}

	}

	public static Properties readProperties(String propFile) throws Exception {
		Properties prop = new Properties();
		FileInputStream input = new FileInputStream(propFile);

		// load a properties file
		prop.load(input);

		return prop;

	}

}
