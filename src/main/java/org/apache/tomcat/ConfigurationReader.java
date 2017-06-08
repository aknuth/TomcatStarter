package org.apache.tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ConfigurationReader {

	private ConfigurationReader() {
		// only static services
	}

	public static Configuration readConfiguration(String configName) {
		try {
			File baseDir = new File(".");
			File configFile = new File(configName);
			System.out.println("--------" + configFile.getAbsolutePath());
			if (configFile.getCanonicalFile().exists()) {
				baseDir = configFile.getCanonicalFile().getParentFile();
			} else {
				throw new RuntimeException("Kann File " + configFile.getAbsolutePath() + " nicht finden ...");
			}
			Configuration configuration = new Configuration(baseDir.getCanonicalPath());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();

			InputSource source = new InputSource(new FileInputStream(configFile.getCanonicalFile()));
			Document doc = builder.parse(source);
			readConfiguration(doc, configuration);
			return configuration;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private static void readConfiguration(Document doc, Configuration configuration) {

		Node jettyNode = findSubNode(doc, "config");
		if (jettyNode == null) {
			throw new IllegalArgumentException("Could not find configuration");
		}

		Node connectorsNode = findSubNode(jettyNode, "connectors");
		if (connectorsNode != null) {
			NodeList connectorNodes = connectorsNode.getChildNodes();
			for (int i = 0, size = connectorNodes.getLength(); i < size; i++) {
				Node connectorNode = connectorNodes.item(i);
				if ("connector".equals(connectorNode.getNodeName())) {
					ConnectorConfiguration connectorConfig = new ConnectorConfiguration();
					NamedNodeMap attrMap = connectorNode.getAttributes();
					if (attrMap != null) {
						Node implNode = attrMap.getNamedItem("implementation");
						if (implNode != null) {
							connectorConfig.setConnectorClazz(implNode.getNodeValue().trim());
						}
					}
					connectorConfig.setConnectorPort(Integer.parseInt(getProperty(connectorNode, "port")));
					if (getProperty(connectorNode, "adress") != null) {
						connectorConfig.setConnectorAdress(getProperty(connectorNode, "adress"));
					}
					connectorConfig.setMaxIdleTime(Integer.parseInt(getProperty(connectorNode, "maxIdleTime")));
					String secureStr = getProperty(connectorNode, "secure");
					if (secureStr != null) {
						connectorConfig.setSecure(Boolean.parseBoolean(secureStr));
					}
					connectorConfig.setKeystoreFile(getProperty(connectorNode, "keystoreFile"));
					connectorConfig.setKeystorePass(getProperty(connectorNode, "keystorePass"));
					connectorConfig.setKeyAlias(getProperty(connectorNode, "keyAlias"));
					connectorConfig.setTruststoreFile(getProperty(connectorNode, "truststoreFile"));
					connectorConfig.setTruststorePass(getProperty(connectorNode, "truststorePass"));
					connectorConfig.setClientAuth(getProperty(connectorNode, "clientAuth"));
					connectorConfig.setSslProtocol(getProperty(connectorNode, "sslProtocol"));
					configuration.addConnectorConfiguration(connectorConfig);
				}
			}
		} else {
			// zumindest ein Standard-Connector muss vorhanden sein
			configuration.addConnectorConfiguration(new ConnectorConfiguration());
		}

		Node tmpDir = findSubNode(jettyNode, "tmpDir");
		if (tmpDir != null) {
			File directory = new File(tmpDir.getFirstChild().getNodeValue().trim());
			if (directory.exists() && directory.isDirectory()) {
				configuration.setTmpDir(directory);
			}
		}
		String listenerPort = getProperty(jettyNode, "stoplistenerport");
		if (isNumeric(listenerPort)) {
			configuration.setListenerPort(Integer.parseInt(listenerPort));
		}

		configuration.setJoining(new Boolean(getProperty(jettyNode, "join")).booleanValue());

		configuration.setSessionIdSuffix(getProperty(jettyNode, "sessionid-suffix"));

		Node[] systemProperties = getSubNodes(jettyNode, "systemProperties", "systemProperty");
		for (int i = 0; i < systemProperties.length; i++) {
			Node valueNode = findSubNode(systemProperties[i], "value");
			Node nameNode = findSubNode(systemProperties[i], "name");
			if (valueNode != null && nameNode != null) {
				configuration.addProperty(nameNode.getFirstChild().getNodeValue().trim(), valueNode.getFirstChild().getNodeValue()
						.trim());
			}
		}

		Node[] webApplications = getSubNodes(jettyNode, "webApps", "webApp");
		if (webApplications == null || webApplications.length == 0) {
			configuration.addWebApp(getProperty(jettyNode, "contextPath"), getProperty(jettyNode, "webAppSourceDirectory"),
					getProperty(jettyNode, "jarsToSkip"), getProperty(jettyNode, "jarsToScan"));
		} else {
			for (int i = 0; i < webApplications.length; i++) {
				Node contextPathNode = findSubNode(webApplications[i], "contextPath");
				Node webAppSourceDirectoryNode = findSubNode(webApplications[i], "webAppSourceDirectory");
				if (contextPathNode != null && webAppSourceDirectoryNode != null) {
					configuration.addWebApp(getProperty(webApplications[i], "contextPath"),
							getProperty(webApplications[i], "webAppSourceDirectory"), getProperty(webApplications[i], "jarsToSkip"),
							getProperty(webApplications[i], "jarsToScan"));
				}
			}
		}
	}

	private static String getProperty(Node jettyNode, String nodeName) {
		Node node = findSubNode(jettyNode, nodeName);
		if (node != null) {
			return node.getFirstChild().getNodeValue().trim();
		} else {
			return null;
		}
	}

	private static Node[] getSubNodes(Node jettyNode, String nodeName, String subNodeName) {
		Node[] result = {};
		Node propertiesNode = findSubNode(jettyNode, nodeName);
		if (propertiesNode != null) {
			NodeList propertyNodes = propertiesNode.getChildNodes();
			for (int i = 0, size = propertyNodes.getLength(); i < size; i++) {
				Node propertyNode = propertyNodes.item(i);
				if (subNodeName.equals(propertyNode.getNodeName())) {
					int arrayLength = Array.getLength(result);
					Node[] tmp = new Node[arrayLength];
					System.arraycopy(result, 0, tmp, 0, arrayLength);
					result = new Node[arrayLength + 1];
					System.arraycopy(tmp, 0, result, 0, tmp.length);
					Array.set(result, arrayLength, propertyNode);
				}
			}
		}
		return result;
	}

	private static Node findSubNode(Node parent, String subNodeName) {
		NodeList list = parent.getChildNodes();
		for (int i = 0, size = list.getLength(); i < size; i++) {
			Node child = list.item(i);
			if (subNodeName.equals(child.getNodeName())) {
				return child;
			}
		}
		return null;
	}

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
}