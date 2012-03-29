package se.cygni.texasholdem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.server.session.SessionManagerLocal;
import se.cygni.texasholdem.table.GamePlan;

import com.google.common.eventbus.EventBus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class TexasHoldemTest {

    @Autowired
    protected SessionManager sessionManager;

    @Test(expected = NullPointerException.class)
    public void testDuplicatePlayerName() {

        final RegisterForPlayRequest request = new RegisterForPlayRequest();
        request.name = "Emil";
        final RegisterForPlayWrapper rw = new RegisterForPlayWrapper(null,
                request);
        sessionManager.onRegisterForPlay(rw);
    }

    @Configuration
    @ComponentScan(basePackages = { "se.cygni" })
    static class ContextConfiguration {

        @Autowired
        GamePlan gamePlan;

        @Autowired
        EventBus eventBus;

        @Bean
        @Primary
        public SessionManager getSessionManager() {

            return new SessionManagerLocal(eventBus, gamePlan);
        }
    }
}
