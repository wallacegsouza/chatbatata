package br.com.batata.testespark;

import br.com.batata.testespark.ChatBatata;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author r0g0
 */
public class HelloWorldTest {
    
    public HelloWorldTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class HelloWorld.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ChatBatata.main(args);
        assertTrue(true);
        //fail("The test case is a prototype.");
    }
    
}
