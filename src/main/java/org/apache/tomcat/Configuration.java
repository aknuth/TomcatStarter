/**
 * Actual Version
 * ==============
 * @version $Revision: 1.1 $
 * @author Stefan Richter, Beckmann & Partner CONSULT
 * For a detailed history of this file see bottom !
 */
package org.apache.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;


/**
 * @date: 11.07.2007
 */
public final class Configuration {

    private static final String BASEDIR_VARIABLE = "${basedir}";

    private final ArrayList connectorConfigurations;
    private final Properties properties;
    private final Properties webApps;
    private final String baseDir;
    private int poolMinThreads;
    private int poolMaxThreads;
    private int poolLowThreads;
    private String sessionIdSuffix;
    private File tmpDir;
    private boolean joining=true;
    private int listenerPort;

    Configuration(String baseDir) {
        poolMinThreads = 10;
        poolMaxThreads = 250;
        poolLowThreads = 50;
        this.baseDir = baseDir;
        properties = new Properties();
        webApps = new Properties();
        connectorConfigurations = new ArrayList();
    }
    public int getListenerPort() {
        return listenerPort;
    }
    public void setListenerPort(int listenerPort) {
        this.listenerPort = listenerPort;
    }
    public String getBaseDir() {
        return baseDir;
    }

    public int getPoolMinThreads() {
        return poolMinThreads;
    }

    public int getPoolMaxThreads() {
        return poolMaxThreads;
    }

    public int getPoolLowThreads() {
        return poolLowThreads;
    }

    public int getCountConnectorConfigurations() {
        return connectorConfigurations.size();
    }

    public ConnectorConfiguration getConnectorConfiguration(int index) {
        return (ConnectorConfiguration)connectorConfigurations.get(index);
    }

    public String getSessionIdSuffix() {
        return sessionIdSuffix;
    }

    void setSessionIdSuffix(String suffix) {
        sessionIdSuffix = suffix;
    }

    void setPoolMinThreads(int poolMinThreads) {
        this.poolMinThreads = poolMinThreads;
    }

    void setPoolMaxThreads(int poolMaxThreads) {
        this.poolMaxThreads = poolMaxThreads;
    }

    void setPoolLowThreads(int poolLowThreads) {
        this.poolLowThreads = poolLowThreads;
    }

    void addProperty(String name, String value) {
        properties.put(name, normalize(value));
    }

    void addConnectorConfiguration(ConnectorConfiguration config) {
        connectorConfigurations.add(config);
    }

    void addWebApp(String contextPath, String webAppSourceDirectory){
        webApps.put(contextPath, normalize(webAppSourceDirectory));
    }

    public Iterator getContextPathIterator(){
        return webApps.keySet().iterator();
    }

    public String getWebAppSourceDirectory(String contextPath){
        return webApps.getProperty(contextPath);
    }

    String normalize(String path) {
        int index = path.indexOf(BASEDIR_VARIABLE);
        if (index != -1) {
            path = path.substring(0, index) + baseDir + path.substring(index + BASEDIR_VARIABLE.length());
        }
        return path;
    }

    public void updateSystemProperties() {
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = properties.getProperty(key);
            System.setProperty(key, value);
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Configuration:\n");
        buffer.append("==============================================\n");
        buffer.append("Base directory    = ");
        buffer.append(baseDir);
        buffer.append('\n');
        buffer.append("ThreadPool:\n");
        buffer.append("  min Threads     = ");
        buffer.append(poolMinThreads);
        buffer.append('\n');
        buffer.append("  max Threads     = ");
        buffer.append(poolMaxThreads);
        buffer.append('\n');
        buffer.append("  low Threads     = ");
        buffer.append(poolLowThreads);
        buffer.append('\n');

        if (sessionIdSuffix != null) {
            buffer.append("SessionId suffix  = ");
            buffer.append(sessionIdSuffix);
            buffer.append('\n');
        }
        if (properties.size() > 0) {
            buffer.append("System properties:\n");
            Enumeration keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                String value = properties.getProperty(key);
                buffer.append("  ");
                buffer.append(key);
                buffer.append(" = ");
                buffer.append(value);
                buffer.append('\n');
            }
        }
        buffer.append("Connectors:\n");
        for (int i = 0, size = connectorConfigurations.size(); i < size; i++) {
            ConnectorConfiguration connectorConfiguration = (ConnectorConfiguration)connectorConfigurations.get(i);
            buffer.append(i + 1);
            buffer.append(" Connector:\n");
            buffer.append("  Clazz           = ");
            buffer.append(connectorConfiguration.getConnectorClazz());
            buffer.append('\n');
            buffer.append("  Port            = ");
            buffer.append(connectorConfiguration.getConnectorPort());
            buffer.append('\n');
            buffer.append("  Max Idle Time   = ");
            buffer.append(connectorConfiguration.getMaxIdleTime());
            buffer.append('\n');

        }
        buffer.append("SERVER PROPS: \n");
        buffer.append("  Server is joining   = ");
        buffer.append(isJoining());
        buffer.append('\n');
        if (listenerPort != 0) {
            buffer.append("  Server ist im StopListenerMode auf Port = ");
            buffer.append(listenerPort);
            buffer.append("\n");
        }
        buffer.append("==============================================\n");
        buffer.append("\n");
        buffer.append("========Folgende Webapplikationen=============\n");
        buffer.append("==============================================\n");
        if (webApps!=null){
            buffer.append("System properties:\n");
            Enumeration keys = webApps.keys();
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                String value = webApps.getProperty(key);
                buffer.append("ContextRoot: ");
                buffer.append(key);
                buffer.append(" | Basedir: ");
                buffer.append(value);
                buffer.append('\n');
            }
        }
        buffer.append("==============================================\n");
        return buffer.toString();
    }

    public boolean isJoining() {
        return joining;
    }

    public void setJoining(boolean joining) {
        this.joining = joining;
    }

    public File getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }
}
