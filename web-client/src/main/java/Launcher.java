import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.security.ProtectionDomain;

public final class Launcher {

    private static final OptionParser parser = new OptionParser();

    private static final OptionSpec<Integer> localPort = parser.accepts("p", "The port for the local webserver to listen to")
            .withRequiredArg()
            .ofType(Integer.class)
            .describedAs("local-port").defaultsTo(8080);

    private static final OptionSpec<Integer> remotePort = parser.accepts("r", "Remote port for the Poker server")
            .withRequiredArg()
            .ofType(Integer.class)
            .describedAs("remote-port").defaultsTo(4711);

    private static final OptionSpec<String> remoteHost = parser
            .accepts("h", "Remote host for the Poker server").withRequiredArg()
            .ofType(String.class)
            .describedAs("remote-host").defaultsTo("localhost");

    private static final OptionSpec<String> directory = parser
            .accepts("d",
                    "Full path to the directory containing static files, are served under the context /player/")
            .withRequiredArg().ofType(String.class)
            .describedAs("directory").required();

    private static final OptionSpec<Void> help = parser.accepts("?", "show help");

    public static void main(String[] args) throws Exception {

        OptionSet options = null;

        try {
            options = parser.parse(args);
        } catch (Exception e) {
            System.out.println("Missing required parameter: " + e.getMessage());
            printHelpAndExit();
        }

        if (options.has(help)) {
            printHelpAndExit();
        }

        startServer(
                options.valueOf(localPort),
                options.valueOf(remoteHost),
                options.valueOf(remotePort),
                options.valueOf(directory));
    }

    private static void printHelpAndExit() throws IOException {
        parser.printHelpOn(System.out);
        System.exit(0);
    }

    private static void startServer(
            int localPort,
            String remoteHost,
            int remotePort,
            String directory) throws Exception {

        System.out.println("Initializing local web server");
        System.out.println("*****************************");
        System.out.println("Local port: " + localPort);
        System.out.println("Poker server host: " + remoteHost);
        System.out.println("Poker server port: " + remotePort);
        System.out.println("Local directory for static files: " + directory);
        System.out.println("*****************************");

        // Check directory
        if (!isDirectoryValid(directory)) {
            System.out.println("Warning, " + directory + " doesn't seem to be a directory that I can read.");
        }

        // Check host
        if (!isHostReachable(remoteHost)) {
            System.out.println("Warning, " + remoteHost + " is unreachable.");
        }

        // Check Poker service is up
        if (!isPokerServiceAlive(remoteHost, remotePort)) {
            System.out.println("Warning, the Poker server is not responding. " + remoteHost + ":" + remotePort);
        }

        // Set directory as system variable
        System.setProperty("static.dir", directory);

        // Set remote host and port as system variables
        // ToDo: validate remote host and port
        System.setProperty("remote.host", remoteHost);
        System.setProperty("remote.port", remotePort + "");

        System.out.println("Starting local web server...");

        Server server = new Server(localPort);

        ProtectionDomain domain = Launcher.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(location.toExternalForm());
        server.setHandler(webapp);

        server.start();
        server.join();
    }

    private static boolean isDirectoryValid(String directory) {
        File dir = new File(directory);

        return dir.isDirectory() && dir.canRead();
    }

    private static boolean isHostReachable(String host) {
        try {
            return InetAddress.getByName(host).isReachable(500);
        } catch (IOException e) {
        }

        return false;
    }

    private static boolean isPokerServiceAlive(String host, int port) {
        Socket s = null;
        try {
            s = new Socket();
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(host, port);
            s.connect(sa, 3000);

            return true;

        } catch (IOException e) {

        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                }
            }
        }

        return false;
    }
}