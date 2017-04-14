package de.florianschlag.javamelodyprometheusexporter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyConfig;
import de.florianschlag.javamelodyprometheusexporter.config.JavaMelodyLastValueGraphs;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;

public class PrometheusGaugesCollection {
	
	private CollectorRegistry registry;
	private Map<String, Gauge> gauges;
	
	public PrometheusGaugesCollection() {
		this(CollectorRegistry.defaultRegistry);
	}

	public PrometheusGaugesCollection(CollectorRegistry registry) {
		this.registry = registry;
		this.gauges = new HashMap<String, Gauge>();
	}
	
	public void createOrSet(JavaMelodyLastValueGraphs graph, double value) {
		createOrSet(null, graph, value);
	}
	
	public void createOrSet(String application, JavaMelodyLastValueGraphs graph, double value) {
		String gaugeName = getGaugeName(application, graph);
		Gauge g = gauges.get(gaugeName);
		if (g == null) {
			g = buildGauge(gaugeName);
			gauges.put(gaugeName, g);
		}
		g.set(value);
	}
	
	public Enumeration<MetricFamilySamples> getSamples() {
		return registry.metricFamilySamples();
	}
	
	private String getGaugeName(String application, JavaMelodyLastValueGraphs graph) {
		return application != null ? application + "_" + graph.getParameterName() : graph.getParameterName();
	}

	private Gauge buildGauge(String gaugeName) {
		return Gauge.build()
				.namespace(JavaMelodyConfig.NAMESPACE)
				.name(gaugeName)
				.help("Gauge for " + gaugeName)
				.register(registry);
	}
	
}
