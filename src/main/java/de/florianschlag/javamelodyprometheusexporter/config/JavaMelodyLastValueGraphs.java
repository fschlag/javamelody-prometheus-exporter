package de.florianschlag.javamelodyprometheusexporter.config;


/**
 * usedPhysicalMemorySize, usedSwapSpaceSize, httpSessionsMeanAge

 * @author Florian Schlag <dev@florianschlag.de>
 *
 */
public enum JavaMelodyLastValueGraphs {
	
	HTTP_HITS_RATE("httpHitsRate"),
	HTTP_MEAN_TIMES("httpMeanTimes"),
	HTTP_SYSTEM_ERRORS("httpSystemErrors"),
	TOMCAT_BUSY_THREADS("tomcatBusyThreads"),
	TOMCAT_BYTES_RECEIVED("tomcatBytesReceived"),
	TOMCAT_BYTES_SENT("tomcatBytesSent"),
	USED_MEMORY("usedMemory"),
	CPU("cpu"),
	HTTP_SESSIONS("httpSessions"),
	ACTIVE_THREADS("activeThreads"),
	ACTIVE_CONNECTIONS("activeConnections"),
	USED_CONNECTIONS("usedConnections"),
	GARBAGE_COLLECTOR("gc"),
	THREAD_COUNT("threadCount"),
	LOADED_CLASSES_COUNT("loadedClassesCount"),
	USED_NON_HEAP_MEMORY("usedNonHeapMemory"),
	USED_PHYSICAL_MEMORY_SIZE("usedPhysicalMemorySize"),
	USED_SWAP_SPACE_SIZE("usedSwapSpaceSize"),
	HTTP_SESSIONS_MEAN_AGE("httpSessionsMeanAge");

	String parameterName;

	private JavaMelodyLastValueGraphs(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
	
}
