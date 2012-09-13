package se.cygni.texasholdem.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.player.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This ClientEventDispatcher uses reflection to notify a player of incoming events.
 * <p/>
 * Upon instantiation of this class the target object is analyzed for methods
 * that take an event class as argument (and also has that same method declared
 * in the interface).
 * <p/>
 * The matching methods are stored in a map for quick lookups.
 *
 * @author emil
 */
public class ClientEventDispatcher {

    private static Logger log = LoggerFactory
            .getLogger(ClientEventDispatcher.class);

    private final Player target;

    @SuppressWarnings("rawtypes")
    private final Map<Class, Method> invokeMap = new HashMap<Class, Method>();

    public ClientEventDispatcher(final Player target) {

        this.target = target;
        populateInvokeMap();
    }

    /**
     * Populates the invoke map with matching methods and arguments.
     */
    private void populateInvokeMap() {

        log.trace("Populating invoke map");

        final Method ms[] = target.getClass().getMethods();
        for (final Method m : ms) {
            if (m.getReturnType() != void.class) {
                continue;
            }

            if (m.getParameterTypes().length != 1) {
                continue;
            }

            if (!Modifier.isPublic(m.getModifiers())) {
                continue;
            }

            if (!isDeclaredInInterface(m)) {
                continue;
            }

            // Has to be only one
            final Class<?> parameter = m.getParameterTypes()[0];

            if (TexasEvent.class.isAssignableFrom(parameter)) {
                invokeMap.put(parameter, m);
            }
        }

        if (log.isTraceEnabled()) {
            printInvokeMap();
        }
    }

    /**
     * Prints the invoke map
     */
    @SuppressWarnings("rawtypes")
    private void printInvokeMap() {

        log.trace("Invoke map:");
        for (final Entry<Class, Method> entry : invokeMap.entrySet()) {

            log.trace("{} => {}", entry.getKey().getSimpleName(), entry
                    .getValue().getName());
        }
    }

    /**
     * Returns true if the Method m is also declared in the interface.
     *
     * @param m
     *
     * @return
     */
    private boolean isDeclaredInInterface(final Method m) {

        try {
            final Method ifaceMethod = Player.class.getMethod(
                    m.getName(), m.getParameterTypes());
            if (ifaceMethod != null) {
                return true;
            }
        } catch (final Exception e) {
        }

        return false;
    }

    /**
     * Invokes the corresponding method for the Player matching
     * the current event.
     *
     * @param event
     */
    public void onEvent(final TexasEvent event) {

        if (event == null) {
            return;
        }

        final Class<? extends TexasEvent> eventClass = event.getClass();
        log.trace("onEvent, looking for matching method for argument: {}",
                eventClass.getSimpleName());

        if (invokeMap.containsKey(eventClass)) {
            try {
                invokeMap.get(eventClass).invoke(target, event);
            } catch (final Exception e) {
                log.error("Failed to invoke target method", e);
            }
        }
        else {
            log.warn(
                    "Could not dispatch event of type: {}, no matching method found",
                    eventClass.getSimpleName());
        }
    }
}
