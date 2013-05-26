/**
 * Actual Version
 * ==============
 * @version $Revision: 1.4 $
 * @author $Author: aknuth $
 * For a detailed history of this file see bottom !
 */
package org.apache.tomcat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 */
public class TomcatStop extends TomcatRunner{

    public TomcatStop(Configuration configuration) {
        super(configuration);
    }

    public static void main(String[] args) {
        TomcatStop tomcatStop = new TomcatStop(null);
        if (!listenerMode){
            System.err.println("no stopport defined");
            System.exit(1);
        }
        tomcatStop.stopServer();
    }

    @Override
    public void stopServer() {
        try {
            String hostAddress = InetAddress.getByName("localhost").getHostAddress();
            Socket socket = new Socket(hostAddress, SERVER_LISTENER_PORT);
            OutputStream stream = socket.getOutputStream();
            String shutdown = SERVER_SHUTDOWN_TOKEN;
            for (int i = 0; i < shutdown.length(); i++)
                stream.write(shutdown.charAt(i));
            stream.flush();
            stream.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Catalina.stop: "+e.getMessage());
            System.exit(1);
        }
    }
}
