/**
 * Actual Version
 * ==============
 * @version $Revision: 1.15 $
 * @author $Author: aknuth $
 * For a detailed history of this file see bottom !
 */
package org.apache.tomcat;

import java.io.File;
import java.util.Iterator;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;


/**
 *
 */
public class TomcatStarter extends TomcatRunner {
    private Tomcat tomcat;

    public TomcatStarter(Configuration configuration) {
        super(configuration);
        if (configuration==null){
            System.err.println("-DtomcatConfig not set");
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    private void init() throws Exception {
        tomcat = new Tomcat();

        configuration.updateSystemProperties();

        Iterator<String> contextPathIterator = configuration.getContextPathIterator();
        while (contextPathIterator.hasNext()) {
            String contextPath = contextPathIterator.next();
            createContext(contextPath,configuration.getWebAppSourceDirectory(contextPath));
        }
        //tomcat.addWebapp("/frame", new File("webApplication").getAbsolutePath());

        // Erstellen der Konnectoren
        for (int i = 0; i < configuration.getCountConnectorConfigurations(); i++) {
            ConnectorConfiguration connConf = configuration.getConnectorConfiguration(i);
            Connector connector = tomcat.getConnector();
            connector.setAttribute("address", connConf.getConnectorAdress());
            connector.setPort(connConf.getConnectorPort());
            connector.setSecure(connConf.isSecure());
            connector.setURIEncoding("UTF-8");

            if (connConf.isSecure()) {
                if (connConf.getClientAuth() != null) {
                    connector.setProperty("clientAuth", connConf.getClientAuth());
                }
                if (connConf.getKeystoreFile() != null) {
                    connector.setProperty("keystoreFile", connConf.getKeystoreFile());
                }
                if (connConf.getKeystorePass() != null) {
                    connector.setProperty("keystorePass", connConf.getKeystorePass());
                }
                if (connConf.getSslProtocol() != null) {
                    connector.setProperty("sslProtocol", connConf.getSslProtocol());
                }
                if (connConf.getKeyAlias() != null) {
                    connector.setProperty("keyAlias", connConf.getKeyAlias());
                }
                if (connConf.getTruststoreFile() != null) {
                    connector.setProperty("truststoreFile", connConf.getTruststoreFile());
                }
                if (connConf.getTruststorePass() != null) {
                    connector.setProperty("truststorePass", connConf.getTruststorePass());
                }
            }
        }


    }

    private Context createContext(String path, String docBase) throws Exception{
        // Erstelle einen Context
        StandardContext context = (StandardContext)tomcat.addWebapp(path, new File(docBase).getAbsolutePath());

        //schaltet die Session Persistenz aus ...
        StandardManager manager =  new StandardManager();
        manager.setPathname(null);
        context.setManager(manager);

        context.setParentClassLoader(this.getClass().getClassLoader());
        //*** Setzt die classloader Prioritaet auf parent first
        context.setDelegate(true);
        //*** Setzt das Flag, mit dem bei Aenderung ein Neustart angefordert wird
        //context.setReloadable(true);

        // Erstellen eines handlers fuer jsps
        Wrapper jspServlet = context.createWrapper();
        jspServlet.setName("jsp");
        jspServlet.setServletClass("org.apache.jasper.servlet.JspServlet");
        jspServlet.addInitParameter("fork", "false");
        jspServlet.addInitParameter("xpoweredBy", "false");
        jspServlet.addInitParameter("enablePooling", "false");
        jspServlet.addInitParameter("compilerSourceVM", "1.6");
        jspServlet.addInitParameter("compilerTargetVM", "1.6");
        jspServlet.addInitParameter("javaEncoding", "UTF8");
        jspServlet.setLoadOnStartup(2);
        context.addChild(jspServlet);
        context.addServletMapping("*.jsp", "jsp");
        context.addServletMapping("*.jspx", "jsp");

        //welcome files
        context.addWelcomeFile("indexs");
        context.setSessionTimeout(30);

        //Einige mime mappings
        context.addMimeMapping("html", "text/html");
        context.addMimeMapping("htm", "text/html");
        context.addMimeMapping("gif", "image/gif");
        context.addMimeMapping("jpg", "image/jpeg");
        context.addMimeMapping("png", "image/png");
        context.addMimeMapping("js", "text/javascript");
        context.addMimeMapping("css", "text/css");
        context.addMimeMapping("pdf", "application/pdf");

        return context;
    }

    public void startServer() throws Exception {
        init();
        tomcat.start();
    }

    public void stopServer() {
        if (tomcat != null) {
            try {
                System.out.println("Shutting down MyServer...");
                tomcat.stop();
                System.out.println("MyServer shutdown.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String args[]) throws Exception {
        Configuration configuration = null;
        String config = System.getProperty("tomcatConfig");
        if (config == null) {
            System.err.println("-DtomcatConfig not set");
            String basedir = System.getProperty("basedir")==null?"webApplication":System.getProperty("basedir");
            configuration = new Configuration(basedir);
            String contextPath = System.getProperty("contextPath")==null?"/":System.getProperty("contextPath");
            configuration.addWebApp(contextPath, basedir);
            int port = System.getProperty("port")==null?8080:Integer.parseInt(System.getProperty("port"));
            configuration.setListenerPort(port);
        } else {
            configuration = ConfigurationReader.readConfiguration(config);
        }

        TomcatStarter server = new TomcatStarter(configuration);
        server.startServer();
        Thread thread = new Thread(server);
        if (listenerMode){
            thread.start();
            System.out.println("Stop Port activated --> "+SERVER_LISTENER_PORT);
        } else {
            server.tomcat.getServer().await();
        }
        //
    }

}