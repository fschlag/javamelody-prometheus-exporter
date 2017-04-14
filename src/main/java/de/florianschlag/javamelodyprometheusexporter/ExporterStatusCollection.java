package de.florianschlag.javamelodyprometheusexporter;

import java.util.Enumeration;

import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyConfig;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Timer;

public class ExporterStatusCollection {

	private static final double STATUS_UP = 1;
	private static final double STATUS_DOWN = 0;
	
	private CollectorRegistry exporterStatusRegistry;
	private Counter scrapFailures;
	private Gauge exporterStatus;
	private Gauge scrapDuration;
	
	private Timer scrapDurationTimer;
	
	public ExporterStatusCollection() {
		this.exporterStatusRegistry = new CollectorRegistry(true);
		this.scrapFailures = buildScrapeFailuresCounter(exporterStatusRegistry);
		this.scrapDuration = buildScrapDurationGauge(exporterStatusRegistry);
		this.exporterStatus = buildStatusGauge(exporterStatusRegistry);
	}

	public void statusUp() {
		exporterStatus.set(STATUS_UP);
	}
	
	public void statusDown() {
		exporterStatus.set(STATUS_DOWN);
	}
	
	public void incScrapFailure() {
		scrapFailures.inc();
	}
	
	public void markScrapStart() {
		scrapDurationTimer = scrapDuration.startTimer();
	}
	
	public void markScrapDone() {
		if (scrapDurationTimer != null) {
			scrapDurationTimer.setDuration();
			scrapDurationTimer = null;
		}
	}
	
	public Enumeration<MetricFamilySamples> getSamples() {
		return exporterStatusRegistry.metricFamilySamples();
	}

	private Counter buildScrapeFailuresCounter(CollectorRegistry registry) {
		return Counter
			.build()
			.name(JavaMelodyConfig.NAMESPACE + "_scrape_failures_total")
			.help("Number of scrape errors")
			.register(registry);
	}
	
	private Gauge buildScrapDurationGauge(CollectorRegistry registry) {
		return Gauge
			.build()
			.name(JavaMelodyConfig.NAMESPACE + "_scrap_duration")
			.help("Scrap duration")
			.register(registry);
	}
	
	private Gauge buildStatusGauge(CollectorRegistry registry) {
		return Gauge
			.build()
			.name(JavaMelodyConfig.NAMESPACE + "_up")
			.help("Exporter Status")
			.register(registry);
	}
	

}
