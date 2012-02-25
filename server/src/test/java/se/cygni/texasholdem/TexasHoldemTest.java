package se.cygni.texasholdem;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.server.SessionManager;
import se.cygni.texasholdem.server.eventbus.RequestContextWrapper;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class TexasHoldemTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    protected SessionManager sessionManager;

    @Test
    public void testDuplicatePlayerName() {

        final RegisterForPlayRequest request = new RegisterForPlayRequest();
        request.name = "Emil";
        final RequestContextWrapper rw = new RequestContextWrapper(null,
                request);
        sessionManager.onRegisterForPlay(rw);
    }

    @Configuration
    @ComponentScan(basePackages = { "se.cygni" })
    static class ContextConfiguration {
    }
}
