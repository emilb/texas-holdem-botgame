import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import se.cygni.texasholdem.webclient.StaticFileServlet;
import se.cygni.texasholdem.webclient.WebPlayerClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;

public final class Launcher {

    final static OptionParser parser = new OptionParser();

    final static OptionSpec<Integer> localPort = parser.accepts("p", "The port for the local webserver to listen to")
            .withRequiredArg()
            .ofType(Integer.class)
            .describedAs("local-port").defaultsTo(8080);

    final static OptionSpec<Integer> remotePort = parser.accepts("r", "Remote port for the Poker server")
            .withRequiredArg()
            .ofType(Integer.class)
            .describedAs("remote-port").defaultsTo(4711);

    final static OptionSpec<String> remoteHost = parser
            .accepts("h", "Remote host for the Poker server").withRequiredArg()
            .ofType(String.class)
            .describedAs("remote-host").defaultsTo("localhost");

    final static OptionSpec<String> directory = parser
            .accepts("d",
                    "Full path to the directory containing static files, are served under the context /player/")
            .withRequiredArg().ofType(String.class)
            .describedAs("directory").required();

    final static OptionSpec<Void> help = parser.accepts("?", "show help");

    public static void main(String[] args) throws Exception {

        OptionSet options = null;

        try {
            options = parser.parse(args);
        } catch (Exception e) {
            System.out.println("Missing required parameter");
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
            System.out.println("\n\nWarning, " + directory + " doesn't seem to be a directory that I can read.\n");
        }

        // Set directory as system variable
        System.setProperty("static.dir", directory);

        // Set remote host and port as system variables
        // ToDo: validate remote host and port
        System.setProperty("remote.host", remoteHost);
        System.setProperty("remote.port", remotePort+"");

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
}