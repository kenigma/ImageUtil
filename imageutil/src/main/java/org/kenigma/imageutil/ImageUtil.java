package org.kenigma.imageutil;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.kenigma.imageutil.exception.ImageUtilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class ImageUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

	public static final String THREE_DIGIT_SEQ_FORMAT = "%03d";

	private SimpleDateFormat filenameSDF;
	private SimpleDateFormat subDirnameSDF;
	private String thumbnailSuffix = "_small";
	private float factor = 0.5f;

	public ImageUtil() {
		filenameSDF = new SimpleDateFormat("yyyyMMdd_HHmmss");
		filenameSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		subDirnameSDF = new SimpleDateFormat("yyyyMMdd");
		subDirnameSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	}

	private String getExt(String filename) {
		int i = filename.lastIndexOf(".");
		String ext = filename.substring(i + 1);
		return ext;
	}

	private boolean isSupportedImageFile(File file) {
		String filename = file.getName();
		return filename.toLowerCase().endsWith("jpg"); // TODO
	}

	private Set<File> listImageFiles(File dir, boolean includeSubDir) {
		Set<File> fileSet = new HashSet<File>();
		for (final File fileEntry : dir.listFiles()) {
			if (fileEntry.isDirectory()) {
				if (includeSubDir) {
					fileSet.addAll(listImageFiles(fileEntry, includeSubDir));
				}
			} else {
				if (isSupportedImageFile(fileEntry)) {
					fileSet.add(fileEntry);
				}
			}
		}
		return fileSet;
	}

	private String genSubDirnameByDateTaken(Date takenDate) {
		if (takenDate == null) {
			throw new IllegalArgumentException("Missing image taken date.");
		}
		String subDirname = subDirnameSDF.format(takenDate);
		return subDirname;
	}

	private String genFilenamePrefixByDateTaken(Date takenDate) {
		if (takenDate == null) {
			throw new IllegalArgumentException("Missing image taken date.");
		}
		String newFilenamePrefix = filenameSDF.format(takenDate);
		return newFilenamePrefix;
	}

	private File genDestFile(String destPath, String subDir, String filename, String seqFormat, String additionalSuffix, String ext) throws ImageUtilException {
		String basePath = destPath + File.separator + subDir + File.separator;
		File file = new File(basePath + filename + additionalSuffix + "." + ext);
		if (file.exists()) {
			int seq = 1;
			File fileSeq;
			do {
				if (seq >= 1000) {
					throw new ImageUtilException("Cannot gen a suitable file name");
				}
				fileSeq = new File(basePath + filename + "_" + String.format(seqFormat, seq) + additionalSuffix + "." + ext);
				seq++;
			} while (fileSeq.exists());
			return fileSeq;
		} else {
			return file;
		}
	}

	public void renameImages(File srcDir, File destDir, boolean toMove, boolean includeSubDir, File thumbnailDir) {
		Set<File> files = this.listImageFiles(srcDir, includeSubDir);
		for (File f : files) {
			try {
				String oriFilename = f.getName();
				String ext = this.getExt(oriFilename);
				Metadata metadata = ImageMetadataReader.readMetadata(f);
				ExifIFD0Directory ifd0Dir = metadata.getDirectory(ExifIFD0Directory.class);
				if (ifd0Dir == null) {
					LOGGER.error("No EXIF info for " + f.getAbsolutePath() + ", skipping.");
					continue;
				}
				Date takenDate = ifd0Dir.getDate(ExifIFD0Directory.TAG_DATETIME);

				String newFilenamePrefix = this.genFilenamePrefixByDateTaken(takenDate);
				String subDirname = this.genSubDirnameByDateTaken(takenDate);

				File destFile = this.genDestFile(destDir.getAbsolutePath(), subDirname, newFilenamePrefix, THREE_DIGIT_SEQ_FORMAT, "", ext);

				if (toMove) {
					FileUtils.moveFile(f, destFile);
					LOGGER.info("Move: {} ==> {} ", f.getAbsolutePath(), destFile.getAbsolutePath());
				} else {
					FileUtils.copyFile(f, destFile);
					LOGGER.info("Copy: {} ==> {} ", f.getAbsolutePath(), destFile.getAbsolutePath());
				}

				if (thumbnailDir != null) {
					String destBaseName = FilenameUtils.getBaseName(destFile.getName());
					String tnBasePath = FilenameUtils.concat(thumbnailDir.getAbsolutePath(), subDirname);
					String tnFullPath = FilenameUtils.concat(tnBasePath, destBaseName + this.thumbnailSuffix + "." + ext);
					File thumbnailDestFile = new File(tnFullPath);
					this.resize(f, thumbnailDestFile);
					LOGGER.info("Resized: {} ==> {}", f.getAbsolutePath(), thumbnailDestFile.getAbsolutePath());

				}

			} catch (Exception e) {
				LOGGER.error("Error processing: " + f.getAbsolutePath(), e);
			}

		}

	}

	/**
	 * scale image
	 * 
	 * @param sbi
	 *            image to scale
	 * @param imageType
	 *            type of image
	 * @param dWidth
	 *            width of destination image
	 * @param dHeight
	 *            height of destination image
	 * @param fWidth
	 *            x-factor for transformation / scaling
	 * @param fHeight
	 *            y-factor for transformation / scaling
	 * @return scaled image
	 */
	private BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
		BufferedImage dbi = null;
		if (sbi != null) {
			dbi = new BufferedImage(dWidth, dHeight, imageType);
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}

	public void resize(File srcFile, File destFile) throws IOException {
		BufferedImage srcImg = ImageIO.read(srcFile);
		BufferedImage resizedImg = this.scale(srcImg, srcImg.getType(), Math.round(srcImg.getWidth() * factor), Math.round(srcImg.getHeight() * factor), factor, factor);
		FileUtils.forceMkdir(destFile);
		ImageIO.write(resizedImg, "jpg", destFile);
	}

}
