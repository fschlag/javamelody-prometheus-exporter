package de.florianschlag.javamelodyprometheusexporter;

public class ScrapExeption extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScrapExeption(String message, Throwable cause) {
		super(message, cause);
	}

	public ScrapExeption(String message) {
		super(message);
	}

}
