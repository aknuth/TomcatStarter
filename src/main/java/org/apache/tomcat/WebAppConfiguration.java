//
// Created on 24.05.2017
//
//*************************************
//* Copyright © 2017                  *
//*************************************
//* TOYOTA Kreditbank GmbH            *
//* TOYOTA Leasing GmbH               *
//* D-50858 Köln                      *
//* Toyota-Allee 5                    *
//* Germany                           *
//* Telefon: +49 (2234) 102-0         *
//* Website: www.toyota-fs.de         *
//*************************************
//* All rights reserved               *
//*************************************
//
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
