package de.florianschlag.javamelodyprometheusexporter.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class JavaMelodyConfig {
	
	private static final Logger logger = Logger.getLogger(JavaMelodyConfig.class);
	
	private static final String PROPERTY_FILENAME = "javamelody.properties";
	private static final String PROPERTY_URL = "javamelody.url";
	private static final String PROPERTY_BASIC_AUTH_USERNAME = "javamelody.basicauth.username";
	private static final String PROPERTY_BASIC_AUTH_PASSWORD = "javamelody.basicauth.password";
	private static final String PROPERTY_COLLECTOR_SERVER = "javamelody.collector.server";
	private static final String PROPERTY_COLLECTOR_APPLICATIONS = "javamelody.collector.applications";
	
	private String url;
	private boolean collectorConfiguration;
	private String[] collectorAppilcations;

	private String basicAuthUsername;
	private String basicAuthPassword;
	
	public JavaMelodyConfig() {
		initConfig();
	}
	
	private void initConfig() {
		InputStream propsInputStream = null;
		try {
			try {
				Properties props = new Properties();
				propsInputStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILENAME);
				props.load(propsInputStream);
				
				setUrl(props.getProperty(PROPERTY_URL));
				setBasicAuthUsername(props.getProperty(PROPERTY_BASIC_AUTH_USERNAME, null));
				setBasicAuthPassword(props.getProperty(PROPERTY_BASIC_AUTH_PASSWORD, null));
				setCollectorConfiguration(Boolean.valueOf(props.getProperty(PROPERTY_COLLECTOR_SERVER)));
				String rawApplications = props.getProperty(PROPERTY_COLLECTOR_APPLICATIONS, null);
				setCollectorAppilcations(rawApplications != null ? rawApplications.split(",") : null);
			} finally {
				if (propsInputStream != null)
					propsInputStream.close();
			}
		} catch (IOException e) {
			logger.error("Configuration failure", e);
			throw new IllegalStateException("Configuration failure", e);
		}
		logger.info("Using config: " + this.toString());
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isCollectorConfiguration() {
		return collectorConfiguration;
	}

	public void setCollectorConfiguration(boolean collectorConfiguration) {
		this.collectorConfiguration = collectorConfiguration;
	}
	
	public String[] getCollectorAppilcations() {
		return collectorAppilcations;
	}

	public void setCollectorAppilcations(String[] collectorAppilcations) {
		this.collectorAppilcations = collectorAppilcations;
	}

	public String getBasicAuthUsername() {
		return basicAuthUsername;
	}

	public void setBasicAuthUsername(String basicAuthUsername) {
		this.basicAuthUsername = basicAuthUsername;
	}

	public String getBasicAuthPassword() {
		return basicAuthPassword;
	}

	public void setBasicAuthPassword(String basicAuthPassword) {
		this.basicAuthPassword = basicAuthPassword;
	}

	@Override
	public String toString() {
		return "JavaMelodyConfig [url=" + url + ", collectorConfiguration=" + collectorConfiguration
				+ ", basicAuthUsername=" + basicAuthUsername + ", basicAuthPassword=" + "***" + "]";
	}

	
}
