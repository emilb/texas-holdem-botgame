package se.cygni.texasholdem.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import se.cygni.texasholdem.communication.message.event.TexasEvent;

public class EventDispatcher {

    Object target;

    @SuppressWarnings("rawtypes")
    Map<Class, Method> invokeMap = new HashMap<Class, Method>();

    public EventDispatcher(final Object target) {

        this.target = target;
        populateInvokeMap();
    }

    private void populateInvokeMap() {

        final Method ms[] = target.getClass().getDeclaredMethods();
        for (final Method m : ms) {
            if (m.getReturnType() != void.class)
                continue;

            if (m.getParameterTypes().length != 1)
                continue;

            // Has to be only one
            final Class<?> parameter = m.getParameterTypes()[0];

            if (TexasEvent.class.isAssignableFrom(parameter)) {
                invokeMap.put(parameter, m);
            }
        }
    }

    public void onEvent(final TexasEvent event) {

        final Class<? extends TexasEvent> eventClass = event.getClass();
        System.out.println(eventClass.getCanonicalName());

        if (invokeMap.containsKey(eventClass)) {
            try {
                invokeMap.get(eventClass).invoke(target, event);
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
