package util.error;

import static util.error.Exceptions.runUnchecked;

import java.io.IOException;

import org.junit.Test;

import util.lambda.ActionE;


public class ExceptionsActionTest {

    private void actionWithException() throws IOException {
        throw new IOException();
    }
    
    @Test(expected = NullPointerException.class)
    public void npeIfNullAction() {
        runUnchecked((ActionE) null);
    }
    
    @Test(expected = IOException.class)
    public void exceptionBubblesUpAsIs() {
        runUnchecked(this::actionWithException);
    }
    
}
