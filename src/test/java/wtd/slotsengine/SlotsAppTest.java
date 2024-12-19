package wtd.slotsengine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SlotsAppTest {

    private static final Logger log = LoggerFactory.getLogger(SlotsAppTest.class.getName());
    private final boolean testAssert;

    public SlotsAppTest() {
        this.testAssert = true;
    }

    @Test
    @DisplayName("Test console app")
    public void testSlotsApp() {
        log.info("Test console app");
        assertTrue(true, "Testing console app");
        assertTrue(testAssert, "Testing assertion+");
    }
}