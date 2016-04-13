package ome.cli.omero.session;

import java.util.Optional;
import java.util.stream.Stream;

import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import Ice.ConnectionRefusedException;
import Ice.DNSException;

public enum ExitCode {
    
    Ok(0),
    InvalidArgs(1),
    InvalidPort(2),
    BadHostName(3, DNSException.class),
    ConnectionRefused(4, ConnectionRefusedException.class),
    CannotCreateSession(5, CannotCreateSessionException.class),
    PermissionDenied(6, PermissionDeniedException.class),
    ServerError(7, omero.ServerError.class),
    InternalError(8);
    
    
    private final int code;
    private final Optional<Class<?>> errorType;
    
    ExitCode(int code) {
        this.code = code;
        this.errorType = Optional.empty();
    }
    
    ExitCode(int code, Class<?> e) {
        this.code = code;
        this.errorType = Optional.of(e);
    }

    public int code() {
        return code;
    }
    
    public static ExitCode codeFor(Exception e) {
        if (e == null) {
            return InternalError;
        }
        return Stream.of(ExitCode.values())
                     .filter(x -> x.errorType.isPresent())
                     .filter(x -> x.errorType.get().equals(e.getClass()))
                     .findFirst()
                     .orElse(InternalError);
    }
    
}
