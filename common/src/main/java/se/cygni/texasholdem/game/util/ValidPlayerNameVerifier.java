package se.cygni.texasholdem.game.util;

import org.apache.commons.lang.StringUtils;
import se.cygni.texasholdem.game.exception.InvalidNameException;

public final class ValidPlayerNameVerifier {

    private static final String validCharacters = "abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ1234567890_-";

    public static void verifyName(String name) throws InvalidNameException {

        int indexOfFirstInvalidChar = StringUtils.indexOfAnyBut(name, validCharacters);

        if (indexOfFirstInvalidChar < 0) {
            return;
        }

        throw new InvalidNameException(name + " is not a legal user name. a-ö, A-Ö, 1-9 and -_ are allowed. " +
                "First illegal character at pos " + indexOfFirstInvalidChar + ": '" +
                name.substring(indexOfFirstInvalidChar, indexOfFirstInvalidChar+1) + "'");

    }
}
