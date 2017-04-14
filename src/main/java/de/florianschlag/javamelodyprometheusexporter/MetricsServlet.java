package de.florianschlag.javamelodyprometheusexporter;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyConfig;
import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyLastValueGraphs;
import io.prometheus.client.exporter.common.TextFormat;

/**
 * Servlet implementation class MetricsServlet
 */
public class MetricsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(MetricsServlet.class);

	private static JavaMelodyConfig config = new JavaMelodyConfig();
	
	private JavaMelodyScraper scraper;
	private PrometheusGaugesCollection gaugeCollection;
	private ExporterStatusCollection statusCollection;

	public MetricsServlet() {
		this.scraper = new JavaMelodyScraper(config.getUrl(), config.getBasicAuthUsername(), config.getBasicAuthPassword(), config.isCollectorConfiguration());
		this.statusCollection = new ExporterStatusCollection();
		this.gaugeCollection = new PrometheusGaugesCollection();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Writer writer = response.getWriter();
		try {
			statusCollection.markScrapStart();
			if (config.isCollectorConfiguration()) {
				scrapCollectorServer(config.getCollectorAppilcations());
			} else {
				scrapSingleServer();
			}
			statusCollection.markScrapDone();
			statusCollection.statusUp();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(TextFormat.CONTENT_TYPE_004);

			TextFormat.write004(writer, gaugeCollection.getSamples());
			writer.flush();
		} catch (Exception e) {
			statusCollection.statusDown();
			statusCollection.incScrapFailure();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error("Failure during scrap", e);
		} finally {
			TextFormat.write004(writer, statusCollection.getSamples());
			writer.close();
		}
	}
	
	private void scrapSingleServer() throws ScrapExeption {
		logger.debug("Scrapping single server");
		Map<JavaMelodyLastValueGraphs, Double> scrapResult = scraper.scrap();
		for (JavaMelodyLastValueGraphs graph : scrapResult.keySet()) {
			gaugeCollection.createOrSet(graph, scrapResult.get(graph));
		}
	}
	
	private void scrapCollectorServer(String[] applications) throws ScrapExeption {
		logger.debug("Scrapping collector server for applications: " + Arrays.toString(applications));
		for (String application : applications) {
			Map<JavaMelodyLastValueGraphs, Double> scrapResult = scraper.scrap(application);
			for (JavaMelodyLastValueGraphs graph : scrapResult.keySet()) {
				gaugeCollection.createOrSet(application, graph, scrapResult.get(graph));
			}
		}
	}

}
