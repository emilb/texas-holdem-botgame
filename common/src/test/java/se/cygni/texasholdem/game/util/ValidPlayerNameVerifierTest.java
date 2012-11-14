package se.cygni.texasholdem.game.util;

import org.junit.Test;
import se.cygni.texasholdem.game.exception.InvalidNameException;

public class ValidPlayerNameVerifierTest {

    @Test
    public void testVerifyNameValidName() throws Exception {
        String name = "emil";
        ValidPlayerNameVerifier.verifyName(name);
    }

    @Test (expected = InvalidNameException.class)
    public void testVerifyNameInvalidName() throws Exception {
        String name = "<script>alert(\"Join freeplay\")</script>";
        ValidPlayerNameVerifier.verifyName(name);
    }
}
