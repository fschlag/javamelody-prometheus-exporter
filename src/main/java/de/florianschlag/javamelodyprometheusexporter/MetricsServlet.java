package de.florianschlag.javamelodyprometheusexporter;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyConfig;
import io.prometheus.client.exporter.common.TextFormat;

/**
 * Servlet implementation class MetricsServlet
 */
public class MetricsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(MetricsServlet.class);
	private static JavaMelodyConfig config = new JavaMelodyConfig();
	
	private static final JavaMelodyPrometheusCollector collector; 

	static {
		collector = new JavaMelodyPrometheusCollector(config.getUrl(), config.getBasicAuthUsername(), config.getBasicAuthPassword(), config.isCollectorConfiguration(), config.getCollectorAppilcations()).register();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Writer writer = response.getWriter();
		try {
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(TextFormat.CONTENT_TYPE_004);

			TextFormat.write004(writer, Collections.enumeration(collector.collect()));
			writer.flush();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error("Failure during scrap", e);
		} finally {
			writer.close();
		}
	}

}
