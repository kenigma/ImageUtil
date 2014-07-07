package org.kenigma.imageutil.exception;

public class ImageUtilException extends Exception {

	private static final long serialVersionUID = -6930945602420102706L;

	public ImageUtilException(String s) {
		super(s);
	}
	
	public ImageUtilException(String s, Throwable t) {
		super(s, t);
	}
	
}
