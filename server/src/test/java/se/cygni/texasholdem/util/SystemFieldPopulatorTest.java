package se.cygni.texasholdem.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SystemFieldPopulatorTest {

    private static final String PROPERTY_PREFIX = "my.prefix.";
    TestClass testClass;
    SystemFieldPopulator sp;

    @Before
    public void setup() {

        testClass = new TestClass();
        sp = new SystemFieldPopulator(testClass, PROPERTY_PREFIX);
    }

    private String getPrefixedProperty(final String prop) {

        return PROPERTY_PREFIX + prop;
    }

    private void setSysProp(final String prop, final Object val) {

        System.setProperty(getPrefixedProperty(prop), val.toString());
    }

    private void clearSysProp(final String prop) {

        System.clearProperty(getPrefixedProperty(prop));
    }

    @Test
    public void testStringValue() {

        final String prop = "strVal";
        final String orgVal = "astring";
        final String newVal = "ILoveJUnit";

        setSysProp(prop, newVal);

        testClass.setStrVal(orgVal);

        assertEquals(orgVal, testClass.getStrVal());
        sp.populateValuesFromSystemProperties();
        assertEquals(newVal, testClass.getStrVal());

        clearSysProp(prop);
    }

    @Test
    public void testLongValue() {

        final String prop = "floatVal";
        final Float orgVal = Float.valueOf(23.4f);
        final Float newVal = Float.valueOf(56.7f);

        setSysProp(prop, newVal);

        testClass.setFloatVal(orgVal);

        assertEquals(orgVal, testClass.getFloatVal(), 0.001f);
        sp.populateValuesFromSystemProperties();
        assertEquals(newVal, testClass.getFloatVal(), 0.001f);

        clearSysProp(prop);
    }

    @Test
    public void testIntValue() {

        final String prop = "intVal";
        final int orgVal = 455;
        final int newVal = -230;

        setSysProp(prop, newVal);

        testClass.setIntVal(orgVal);

        assertEquals(orgVal, testClass.getIntVal());
        sp.populateValuesFromSystemProperties();
        assertEquals(newVal, testClass.getIntVal());

        clearSysProp(prop);
    }

    @Test
    public void testDoubleValue() {

        final String prop = "doubleVal";
        final double orgVal = -12.6d;
        final double newVal = 35.4d;

        setSysProp(prop, newVal);

        testClass.setDoubleVal(orgVal);

        assertEquals(orgVal, testClass.getDoubleVal(), 0.001d);
        sp.populateValuesFromSystemProperties();
        assertEquals(newVal, testClass.getDoubleVal(), 0.001d);

        clearSysProp(prop);
    }

    @Test
    public void testFloatValue() {

        final String prop = "floatVal";
        final float orgVal = -12.6f;
        final float newVal = 35.4f;

        setSysProp(prop, newVal);

        testClass.setFloatVal(orgVal);

        assertEquals(orgVal, testClass.getFloatVal(), 0.001f);
        sp.populateValuesFromSystemProperties();
        assertEquals(newVal, testClass.getFloatVal(), 0.001f);

        clearSysProp(prop);
    }

    @Test
    public void testInvalidFloatValue() {

        final String prop = "floatVal";
        final float orgVal = -12.6f;
        setSysProp(prop, "notAFloat");

        testClass.setFloatVal(orgVal);

        assertEquals(orgVal, testClass.getFloatVal(), 0.001f);
        sp.populateValuesFromSystemProperties();
        assertEquals(orgVal, testClass.getFloatVal(), 0.001f);

        clearSysProp(prop);
    }

    @Test
    public void testBooleanValue() {

        final String prop = "booleanVal";
        final boolean orgVal = true;
        final boolean newVal = false;

        setSysProp(prop, newVal);

        testClass.setBooleanVal(orgVal);

        assertEquals(orgVal, testClass.getBooleanVal());
        sp.populateValuesFromSystemProperties();
        assertEquals(newVal, testClass.getBooleanVal());

        clearSysProp(prop);
    }

    @Test
    public void testFinalValue() {

        final String prop = "aFinalString";
        final String orgVal = testClass.getAFinalString();
        final String newVal = "shouldNotWork";

        setSysProp(prop, newVal);

        assertEquals(orgVal, testClass.getAFinalString());
        sp.populateValuesFromSystemProperties();
        assertEquals(orgVal, testClass.getAFinalString());

        clearSysProp(prop);
    }

    @Test
    public void testEnum() {

        final String prop = "choice";
        final ChoiceOfEnum orgVal = ChoiceOfEnum.BeTheBest;
        final ChoiceOfEnum newVal = ChoiceOfEnum.BeTheWorst;

        setSysProp(prop, newVal);

        testClass.setChoice(orgVal);
        assertEquals(orgVal, testClass.getChoice());
        sp.populateValuesFromSystemProperties();
        assertEquals(newVal, testClass.getChoice());

        clearSysProp(prop);
    }

    enum ChoiceOfEnum {
        BeTheBest,
        BeTheWorst
    }

    public static class TestClass {

        private String strVal;
        private Long longVal;
        private int intVal;
        private double doubleVal = 1.23d;
        private Float floatVal;
        private Boolean booleanVal;
        private final String aFinalString = "joy";
        private ChoiceOfEnum choice;

        public String getStrVal() {

            return strVal;
        }

        protected void setStrVal(final String strVal) {

            this.strVal = strVal;
        }

        public Long getLongVal() {

            return longVal;
        }

        public void setLongVal(final Long longVal) {

            this.longVal = longVal;
        }

        public int getIntVal() {

            return intVal;
        }

        public void setIntVal(final int intVal) {

            this.intVal = intVal;
        }

        public double getDoubleVal() {

            return doubleVal;
        }

        public void setDoubleVal(final double doubleVal) {

            this.doubleVal = doubleVal;
        }

        public Float getFloatVal() {

            return floatVal;
        }

        public void setFloatVal(final Float floatVal) {

            this.floatVal = floatVal;
        }

        public Boolean getBooleanVal() {

            return booleanVal;
        }

        public void setBooleanVal(final Boolean booleanVal) {

            this.booleanVal = booleanVal;
        }

        public String getAFinalString() {

            return aFinalString;
        }

        private ChoiceOfEnum getChoice() {

            return choice;
        }

        private void setChoice(final ChoiceOfEnum choice) {

            this.choice = choice;
        }

    }
}
