<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript">
    $(document).ready(function () {
        prettyPrint();
    });
</script>

<div class="container">
    <div class="hero-unit">
        <h1>Getting started</h1>

        <p class="pull-right"><a href="/registration">Register for contest</a></p>

        <p>and code samples</p>
    </div>

    <div class="row">
        <div class="span6">
            <div class="well well-large">
                <h2>Getting started with a Java client</h2>

                <p>Follow the commands below and you should be okay, see links below for
                    further instructions if some <a href="#java_client_prereq">prerequisites</a> are missing.</p>

                <p>You need to have a sane Java environment, version 6 or above is okay:</p>
                <pre class="prettyprint">
> java -version
java version "1.7.0_04"
Java(TM) SE Runtime Environment (build 1.7.0_04-b20)
Java HotSpot(TM) Server VM (build 23.0-b21, mixed mode)</pre>

                <p>Maven is needed for project builds and dependency management, version 3 or above:</p>
                <pre class="prettyprint">
> mvn -version
Apache Maven 3.0.4 (r1232337; 2012-01-17 09:44:56+0100)
Maven home: /opt/java/maven-3.0.4
Java version: 1.7.0_04, vendor: Oracle Corporation
Java home: /usr/lib/jvm/jdk1.7.0/jre
Default locale: en_US, platform encoding: ANSI_X3.4-1968
OS name: "linux", version: "3.5.2-linode45", arch: "i386", family: "unix"</pre>

                <p>Download the example project:
                    <a href="/download/texas-holdem-java-client-<spring:eval expression="@applicationProperties.getProperty('application.version')" />.zip">texas-holdem-java-client-<spring:eval
                            expression="@applicationProperties.getProperty('application.version')"/></a>
                    Extract the zip.
                </p>
 <pre class="prettyprint">
> unzip texas-holdem-java-client-<spring:eval expression="@applicationProperties.getProperty('application.version')"/>.zip</pre>
                <p>
                    A simple test run with maven:
                </p>
                    <pre class="prettyprint">
> cd texas-holdem-java-client-<spring:eval expression="@applicationProperties.getProperty('application.version')"/>
> mvn compile exec:java -Dexec.mainClass="se.cygni.texasholdem.player.FullyImplementedBot"

[INFO] Scanning for projects...
[INFO]
[INFO] -------------------------------------------------------
[INFO] Building texas-holdem-java-client 1.1.10
[INFO] -------------------------------------------------------

    ...
[INFO]
[INFO] --- exec-maven-plugin:1.2.1:java (default-cli) @ texas-holdem-java-client ---
[WARNING]
java.lang.reflect.InvocationTargetException
	...
Caused by: java.lang.RuntimeException:
        Did you forget to specify a name for your bot (hint: your email address is a good idea)?
	at se.cygni.texasholdem.player.FullyImplementedBot.getName(FullyImplementedBot.java:85)
	at se.cygni.texasholdem.client.PlayerClient.(PlayerClient.java:74)
        at se.cygni.texasholdem.player.FullyImplementedBot.(FullyImplementedBot.java:52)
            at se.cygni.texasholdem.player.FullyImplementedBot.main(FullyImplementedBot.java:66)
            ... 6 more
[INFO] -------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] -------------------------------------------------------
[INFO] Total time: 18.871s
[INFO] Finished at: Sun Sep 30 01:44:27 CEST 2012
[INFO] Final Memory: 28M/260M
[INFO] -------------------------------------------------------
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.2.1:java (default-cli) on project texas-holdem-java-client: An exception occured while executing the Java class. null: InvocationTargetException: Did you forget to specify a name for your bot (hint: your email address is a good idea)? -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException
</pre>
                <p>OOOPs! You need to implement the method getName() properly. Get started and create the best Poker
                    playing Bot possible!</p>

                <p>Hint: The class se.cygni.texasholdem.player.FullyImplementedBot is an example bot that you can use as
                    a starting point.</p>


            </div>
        </div>
        <!--/span-->

        <div class="span6">
            <div class="well well-large">
                <h2>Getting started with a node.js client</h2>

                <p>The commands below illustrate how to run the example bot in node.js. See <a
                        href="#nodejs_client_prereq">prerequisites</a> if
                    something is missing in your setup.</p>

                <p>The texas-holdem-nodejs-client is verified to work with node.js v0.8.8. Older and newer versions may
                    be okay.</p>
                <pre class="prettyprint">
> node -v
v0.8.8</pre>

                <p>Download the example project: <a
                        href="/download/texas-holdem-nodejs-client-<spring:eval expression="@applicationProperties.getProperty('application.version')" />.zip">texas-holdem-nodejs-client-<spring:eval
                        expression="@applicationProperties.getProperty('application.version')"/>.zip</a>
                </p>

                <p>Extract the zip</p>
                    <pre class="prettyprint">
> unzip texas-holdem-nodejs-client-<spring:eval expression="@applicationProperties.getProperty('application.version')"/>.zip</pre>

                <p>Do a test run:

                    <pre class="prettyprint">
> node play.js
        throw new Error('Did you forget to specify your name? A good idea is t
              ^
Error: Did you forget to specify your name? A good idea is to use your e-mail as username! </pre>
                </p>

                <p>OOOPs! You need to specify a value for the variable playerName in botplayer.js!</p>

                <p>Hint: The file botplayer.js is an example bot that you can use as a starting point.</p>
            </div>
            <!--/span-->
        </div>

        <div class="span6">
            <div class="well well-large">
                <h2>Documentation and links</h2>

                <p>The Maven site for this project is here:
                    <a href="/mavensite">Cygni Texas Hold'em Maven site</a>
                </p>

                <p>Download directory can be viewed here:
                    <a href="/download">Downloads</a>
                </p>

                <p>Maven repo:
                    <a href="/maven2">Cygni Texas Hold'em Maven repo</a>
                </p>

                <p>Add to your pom.xml to enable this repo:
                <pre class="prettyprint">
&lt;repositories&gt;
    &lt;repository&gt;
        &lt;id&gt;poker.cygni.se&lt;/id&gt;
        &lt;url&gt;http://poker.cygni.se/maven2&lt;/url&gt;
    &lt;/repository&gt;
&lt;/repositories&gt;</pre>
                </p>
            </div>
        </div>

        <div class="span6">
            <a name="java_client_prereq"></a>

            <h2>Java prerequisites</h2>

            <p>
                You need a Java JDK of version 6 or above: <a
                    href="http://www.oracle.com/technetwork/java/javase/downloads/index.html" target="_blank">download
                Java</a>
            </p>

            <p>
                You need to install Maven: <a href="http://maven.apache.org/download.html" target="_blank">download
                Maven</a> </br>
                Installation instructions: <a href="http://maven.apache.org/download.html#Installation" target="_blank">Maven
                installation</a>
            </p>

            <a name="nodejs_client_prereq"></a>

            <h2>node.js prerequisites</h2>

            <p>
                Download and install node.js: <a href="http://nodejs.org/" target="_blank">node.js</a>
            </p>

            <a name="java_ide"></a>

            <h2>IDE (Integated Development Environment)</h2>

            <p>
                At Cygni we mainly use Eclipse, IntelliJ or NetBeans but you may choose any development enviroment you
                want. Here are a few suggestions:
            </p>

            <p>
                <a href="http://www.eclipse.org/downloads/">Eclipse</a> (Also add the M2Eclipse plugin for Maven
                support)</br>
                <a href="http://www.jetbrains.com/idea/download/index.html">IntelliJ</a> (Out of the box support for
                Maven)</br>
                <a href="http://netbeans.org/downloads/">NetBeans</a> (Out of the box support for Maven)</br>
                <a href="http://www.sublimetext.com/2">Sublime Text 2</a> (A good text editor)</br>
            </p>

        </div>

    </div>

    <!--/span-->
    <!--/row-->
</div>
<!--/.fluid-container-->
