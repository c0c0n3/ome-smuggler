package util.error;

import static util.error.Exceptions.unchecked;

import java.io.IOException;

import org.junit.Test;

import util.lambda.ActionE;


public class ExceptionsActionTest {

    private void actionWithException() throws IOException {
        throw new IOException();
    }
    
    @Test(expected = NullPointerException.class)
    public void npeIfNullAction() {
        unchecked((ActionE) null);
    }
    
    @Test(expected = IOException.class)
    public void exceptionBubblesUpAsIs() {
        unchecked(this::actionWithException);
    }
    
}
