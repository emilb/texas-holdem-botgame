package se.cygni.texasholdem;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

public class SystemSettingsTest {

    @Test
    public void testSystemProperties() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Setting system properties");

        System.setProperty(SystemSettings.PREFIX_PROPERTY + "port", "666");
        System.setProperty(SystemSettings.PREFIX_PROPERTY + "host", "doot.se");

        stopWatch.stop();

        stopWatch.start("Create SystemSettings class");

        final SystemSettings sp = new SystemSettings();

        stopWatch.stop();

        stopWatch.start("Assert values");
        Assert.assertEquals(666, sp.getPort());
        Assert.assertEquals("doot.se", sp.getHost());

        stopWatch.stop();

        System.out.print(stopWatch.prettyPrint());
    }
}
