package util.runtime.jvm;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.function.Function;

import util.lambda.FunctionE;

/**
 * Locates the base directory or jar file from which a given class was loaded.
 */
public class ClassPathLocator {

    /**
     * Attempts to determine the base directory or jar file containing the 
     * given class.
     * This may fail for any of the reasons below:
     * <ul>
     *  <li>This JVM doesn't have the "getProtectionDomain" permission.</li>
     *  <li>{@code clazz} is not in a jar file or directory on the host.</li>
     * </ul>
     * @param clazz a class to determine a containing jar file or directory.
     * @return an absolute path pointing to the class base location if the 
     * class could be determined to be in a local directory or jar file; 
     * an empty optional otherwise.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws SecurityException if a security manager exists and its 
     * the application doesn't have the "getProtectionDomain" permission.
     */
    public static Optional<Path> findBase(Class<?> clazz) {
        requireNonNull(clazz, "clazz");
        
        Function<URL, URI> fromURL = (FunctionE<URL, URI>) URL::toURI;
        return Optional.ofNullable(clazz)
                       .map(Class::getProtectionDomain)
                       .map(ProtectionDomain::getCodeSource)  // javadoc says it may be null!
                       .map(CodeSource::getLocation)
                       .map(fromURL)  // can't throw b/c we have a valid URL
                       .map(Paths::get)
                       .filter(Files::exists)
                       .map(Path::normalize)
                       .map(Path::toAbsolutePath);
    }
    
}
