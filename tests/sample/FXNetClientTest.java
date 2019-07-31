package sample;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

class FXNetClientTest {

    FXNetClient fxnet;
    FXNetClientTest test;

    @Before
    void setup(){
        test = new FXNetClientTest();
        fxnet = new FXNetClient();
    }

    @Test
    void testTitleChanged() {
        assertEquals("title changed should be false", false, fxnet.returnTitlechanged());
    }

    @Test
    void testAssignedID() {
        assertEquals("assigned ID should be false", false, fxnet.returnAssignedID());
    }



}