package org.apache.tomcat;

public final class ConnectorConfiguration {

    private String connectorClazz;
    private int connectorPort;
    private String connectorAdress;
    private int maxIdleTime;
    private boolean secure;
    private String keystoreFile;
    private String keystorePass;
    private String clientAuth;
    private String sslProtocol;
    private String keyAlias;
    private String truststoreFile;
    private String truststorePass;

    ConnectorConfiguration() {
        connectorClazz = null;
        connectorPort = 8080;
        connectorAdress = "*";
        maxIdleTime = 60000;
        secure = false;
        keystoreFile = null;
        keystorePass = null;
        clientAuth = null;
        sslProtocol = null;
    }

    public String getConnectorClazz() {
        return connectorClazz;
    }

    public int getConnectorPort() {
        return connectorPort;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    void setConnectorPort(int connectorPort) {
        this.connectorPort = connectorPort;
    }

    void setConnectorClazz(String connectorClazz) {
        this.connectorClazz = connectorClazz;
    }

    void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public boolean isSecure() {
        return secure;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public String getClientAuth() {
        return clientAuth;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    void setSecure(boolean secure) {
        this.secure = secure;
    }

    void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    void setClientAuth(String clientAuth) {
        this.clientAuth = clientAuth;
    }

    void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getTruststoreFile() {
        return truststoreFile;
    }

    public String getTruststorePass() {
        return truststorePass;
    }

    void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    void setTruststoreFile(String truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    void setTruststorePass(String truststorePass) {
        this.truststorePass = truststorePass;
    }

    public String getConnectorAdress() {
        return connectorAdress;
    }

    public void setConnectorAdress(String connectorAdress) {
        this.connectorAdress = connectorAdress;
    }
}