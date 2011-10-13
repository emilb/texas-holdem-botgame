package se.cygni.texasholdem;

import org.junit.Assert;
import org.junit.Test;

public class SystemSettingsTest {

    @Test
    public void testSystemProperties() {

        System.setProperty(SystemSettings.PREFIX_PROPERTY + "port", "666");
        final SystemSettings sp = new SystemSettings();

        Assert.assertEquals(666, sp.getPort());
    }
}
