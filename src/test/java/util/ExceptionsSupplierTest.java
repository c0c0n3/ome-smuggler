package util;

import static util.Exceptions.unchecked;

import java.io.IOException;
import java.util.function.Supplier;

import org.junit.Test;

import util.lambda.SupplierE;

public class ExceptionsSupplierTest {
    
    private String supplyWithCheckedException() throws IOException {
        throw new IOException();
    }
    
    private String supplyWithUncheckedException() {
        throw new NullPointerException();
    }
    
    private String feed(Supplier<String> f) {
        return f.get();
    }
    
    @Test(expected = IOException.class)
    public void checkedExceptionBubblesUpAsIs() {
        SupplierE<String> fe = this::supplyWithCheckedException;
        feed(fe);
    }
    
    @Test(expected = IOException.class)
    public void checkedExceptionBubblesUpAsIsUsingUnchecked() {
        feed(unchecked(this::supplyWithCheckedException));
    }
    
    @Test(expected = NullPointerException.class)
    public void uncheckedExceptionBubblesUpAsIs() {
        SupplierE<String> fe = this::supplyWithUncheckedException;
        feed(fe);
    }
    
    @Test(expected = NullPointerException.class)
    public void uncheckedExceptionBubblesUpAsIsUsingUnchecked() {
        feed(unchecked(this::supplyWithUncheckedException));
    }

}
