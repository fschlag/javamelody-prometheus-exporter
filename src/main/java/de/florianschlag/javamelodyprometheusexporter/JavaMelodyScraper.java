package de.florianschlag.javamelodyprometheusexporter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyLastValueGraphs;

public class JavaMelodyScraper {
	
	private static final int TIMEOUT = 5000;
	
	private static final String LAST_VALUE_BASE_URL = "?part=lastValue";
	private static final String GRAPH_PARAMETER = "&graph=";
	private static final String APPLICATION_PARAMETER = "&application=";
	
	private static final String BASIC_AUTH_HEADER_NAME = "Authorization";
	private static final String BASIC_AUTH_HEADER_VALUE = "Basic ";

	private Header authHeader = null;
	private final String baseUrl;
	private boolean collector;

	public JavaMelodyScraper(String baseUrl, String basicAuthUser, String basicAuthPassword, boolean isCollector) {
		if (basicAuthUser != null && basicAuthPassword != null) {
			this.authHeader = new BasicHeader(BASIC_AUTH_HEADER_NAME, BASIC_AUTH_HEADER_VALUE +
					buildBasicAuthHeaderValue(basicAuthUser, basicAuthPassword));
		}
		this.baseUrl = baseUrl;
		this.collector = isCollector;
	}
	
	public Map<JavaMelodyLastValueGraphs, Double> scrap() throws ScrapExeption {
		return scrap(null, JavaMelodyLastValueGraphs.values());
	}
	
	public Map<JavaMelodyLastValueGraphs, Double> scrap(String application) throws ScrapExeption {
		return scrap(application, JavaMelodyLastValueGraphs.values());
	}

	public Map<JavaMelodyLastValueGraphs, Double> scrap(JavaMelodyLastValueGraphs... graphs) throws ScrapExeption {
		return scrap(null, graphs);
	}
	
	public Map<JavaMelodyLastValueGraphs, Double> scrap(String application, JavaMelodyLastValueGraphs... graphs) throws ScrapExeption {
		if (collector && (application == null || application.length() <= 0) ) {
			throw new ScrapExeption("Collector is configured, but no applications provided.");
		}
		Map<JavaMelodyLastValueGraphs, Double> result = new LinkedHashMap<JavaMelodyLastValueGraphs, Double>(graphs.length);
		for (JavaMelodyLastValueGraphs graph : graphs) {
			result.put(graph, -1.0);
		}
		String downloadLastValueData = downloadLastValueData(buildLastValueUrl(baseUrl, application, result.keySet()));
		StringTokenizer rawResultTokens = new StringTokenizer(downloadLastValueData, ",");
		for (JavaMelodyLastValueGraphs graph : result.keySet()) {
			String token = rawResultTokens.nextToken();
			double value = Double.parseDouble(token);
			result.put(graph, value > 0 ? value : 0);
		}
		return result;
	}
	
	private String downloadLastValueData(String url) throws ScrapExeption {
		try {
			Request request = Request
									.Get(url)
									.connectTimeout(TIMEOUT)
									.socketTimeout(TIMEOUT);
			if (authHeader != null) {
				request = request.addHeader(authHeader);
			}
			HttpResponse response = request
								.execute()
								.returnResponse();
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				return EntityUtils.toString(response.getEntity());
			}
			throw new ScrapExeption("HTTP-Response code was " + responseCode);
		} catch (IOException e) {
			throw new ScrapExeption("Exception while downloading: " + url, e);
		}
	}
	
	private String buildBasicAuthHeaderValue(String username, String password) {
		String userPassword = username + ":" + password;
		return Base64.encodeBase64String(userPassword.getBytes());
	}

	private String buildLastValueUrl(String baseUrl, String application, Set<JavaMelodyLastValueGraphs> graphs) {
		StringBuilder sBuilder = new StringBuilder(baseUrl);
		if (!baseUrl.endsWith("/")) {
			sBuilder.append("/");
		}
		sBuilder.append(LAST_VALUE_BASE_URL);
		sBuilder.append(GRAPH_PARAMETER);
		int size = graphs.size();
		for (JavaMelodyLastValueGraphs graph : graphs) {
			sBuilder.append(graph.getParameterName());
			if (--size > 0) {
				sBuilder.append(",");
			}
		}
		if (application != null) {
			sBuilder.append(APPLICATION_PARAMETER);
			sBuilder.append(application);
		}
		return sBuilder.toString(); 
	}

}
