package de.florianschlag.javamelodyprometheusexporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyLastValueGraphs;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

public class JavaMelodyPrometheusCollector extends Collector {
	
	private static final Logger logger = Logger.getLogger(JavaMelodyPrometheusCollector.class);
	
	public static final String NAMESPACE = "javamelody";

	private JavaMelodyScraper scraper;
	private boolean isJavaMelodyCollectorServer;
	private String[] applications;

	public JavaMelodyPrometheusCollector(String url, String username, String password, boolean isJavaMelodyCollectorServer, String... applications) {
		super();
		this.isJavaMelodyCollectorServer = isJavaMelodyCollectorServer;
		this.scraper = new JavaMelodyScraper(url, username, password, isJavaMelodyCollectorServer);
		this.applications = applications;
	}

	@Override
	public List<MetricFamilySamples> collect() {
		try {
			return isJavaMelodyCollectorServer ? buildCollectorServerMetricFamilySamples() : buildSingleServerMetricFamilySamples();
		} catch (Exception e) {
			logger.error("Error while collecting data.", e);
			throw new IllegalStateException(e);
		}
	}
	
	private List<MetricFamilySamples> buildSingleServerMetricFamilySamples() throws ScrapExeption {
		List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
		Map<JavaMelodyLastValueGraphs, Double> scrapResults = scrapSingleServer();
		for (JavaMelodyLastValueGraphs graph : scrapResults.keySet()) {
			mfs.add(new GaugeMetricFamily(NAMESPACE + "_" + graph.getParameterName(), "Help for " + graph.getParameterName(), scrapResults.get(graph)));
		}
		return mfs;
	}
	
	private List<MetricFamilySamples> buildCollectorServerMetricFamilySamples() throws ScrapExeption {
		List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
		Map<String, Map<JavaMelodyLastValueGraphs, Double>> scrapResults = scrapCollectorServer(applications);
		Set<JavaMelodyLastValueGraphs> keySet = scrapResults.values().iterator().next().keySet();
		for (JavaMelodyLastValueGraphs graph : keySet) {
		    GaugeMetricFamily gauge = new GaugeMetricFamily(NAMESPACE + "_" + graph.getParameterName(), "Help for " + graph.getParameterName(), Arrays.asList("application"));
		    for (String application : applications) {
		    	gauge.addMetric(Arrays.asList(application), scrapResults.get(application).get(graph));
		    }
		    mfs.add(gauge);
		}
		return mfs;
	}

	private Map<JavaMelodyLastValueGraphs, Double> scrapSingleServer() throws ScrapExeption {
		logger.debug("Scrapping single server");
		return scraper.scrap();
	}

	private Map<String, Map<JavaMelodyLastValueGraphs, Double>> scrapCollectorServer(String[] applications) throws ScrapExeption {
		logger.debug("Scrapping collector server for application: " + Arrays.toString(applications));
		Map<String, Map<JavaMelodyLastValueGraphs, Double>> scrapResults = new HashMap<String, Map<JavaMelodyLastValueGraphs,Double>>(applications.length);
		for (String application : applications) {
			scrapResults.put(application, scraper.scrap(application));
		}
		return scrapResults;
	}
}
