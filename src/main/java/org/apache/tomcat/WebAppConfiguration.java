package org.apache.tomcat;

public final class WebAppConfiguration {
	private final String contextPath;

	private final String webAppSourceDirectory;

	private final String jarsToSkip;

	private final String jarsToScan;

	public WebAppConfiguration(String contextPath, String webAppSourceDirectory, String jarsToSkip, String jarsToScan) {
		this.contextPath = contextPath;
		this.webAppSourceDirectory = webAppSourceDirectory;
		this.jarsToSkip = jarsToSkip;
		this.jarsToScan = jarsToScan;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getWebAppSourceDirectory() {
		return webAppSourceDirectory;
	}

	public String getJarsToSkip() {
		return jarsToSkip;
	}

	public String getJarsToScan() {
		return jarsToScan;
	}
}
