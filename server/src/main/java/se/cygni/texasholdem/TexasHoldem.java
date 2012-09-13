package se.cygni.texasholdem;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TexasHoldem {

    public static void main(final String[] args) {

        TexasHoldem.setArgsAsSystemProperties(args);

        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("se.cygni");
        ctx.refresh();

    }

    private static void setArgsAsSystemProperties(final String[] args) {

        for (final String arg : args) {
            final String[] propVal = StringUtils.split(arg, '=');
            if (propVal.length != 2) {
                continue;
            }

            System.setProperty(
                    SystemSettings.PREFIX_PROPERTY + propVal[0],
                    propVal[1]);
        }
    }
}
