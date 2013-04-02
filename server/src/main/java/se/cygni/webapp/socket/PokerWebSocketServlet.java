package se.cygni.webapp.socket;

import com.google.common.eventbus.EventBus;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(value="/websockets/test", loadOnStartup=1)
public class PokerWebSocketServlet extends org.eclipse.jetty.websocket.servlet.WebSocketServlet {

    private EventBus eventBus;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        EventBus eventBus = springContext.getBean(EventBus.class);
        this.eventBus = eventBus;

        System.out.println("EventBus: " + eventBus);
    }

    public PokerWebSocketServlet() {
        System.out.println("Created hellowebsocketservlet");
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        System.out.println("Created PokerWebSocketAdapter via factory!");
//        factory.register(PokerWebSocketAdapter.class);

        /* ToDo: Fix this to return a Spring-bean

        */
        factory.setCreator(new WebSocketCreator() {
            @Override
            public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
                return new PokerWebSocketAdapter(eventBus);
            }
        });
    }

}
