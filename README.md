# Archived since 12/2021 (NO MORE UPDATES)

I recommend using the official prometheus exporter provided by the JavaMelody project itself.
Please follow the official guide at https://github.com/javamelody/javamelody/wiki/UserGuideAdvanced#exposing-metrics-to-prometheus

---

# JavaMelody Prometheus Exporter

This is a prometheus exporter (<https://prometheus.io/docs/instrumenting/exporters>) for the JavaEE monitoring tool [JavaMelody](https://github.com/javamelody/javamelody/wiki). It was created to get a deeper insight of the data JavaMelody collects, especially over time without the need to change existing application code to full prometheus monitoring.

## Why Java?

Instead of using Go or Python this exporter uses Java because you probably already own a Java infrastructure when running JavaMelody so you can deploy this exporter right next to your application.

## How it works

This exporter uses the JavaMelody [lastValue external API](https://github.com/javamelody/javamelody/wiki/ExternalAPI#png-and-lastvalue) to pull data and transforms it to Prometheus gauges. Exporting is done via a simple Java servlet. No rocket science.

## How to use it
The build is maven based so a `mvn package` will create the war file in the `target` folder.

Before doing this you have to create your own `javamelody.properties` file in `src/main/resources/`otherwise the build will fail with

	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-enforcer-plugin:1.4.1:enforce (Custom javamelody.properties was created) on 	project javamelody-prometheus-exporter: Some Enforcer rules have failed. Look above for specific messages explaining why the rule failed. 	-> [Help 1]

Afterwards just deploy the war file on an application server of your choice. (Tested with Tomcat 7 & 8, minimum Java 6)

### Example javamelody.properties

	javamelody.url=https://<server running javamelody instance>

	# Basic authentication credentials if necessary
	javamelody.basicauth.username=
	javamelody.basicauth.password=

	javamelody.collector.server=false

	# Comma seperated list of applications (<application1>,<application2>)
	javamelody.collector.applications=
	
## Alternative

As an alternative to this exporter, JavaMelody also includes itself an [integration for Prometheus](https://github.com/javamelody/javamelody/wiki/UserGuideAdvanced#exposing-metrics-to-prometheus).
