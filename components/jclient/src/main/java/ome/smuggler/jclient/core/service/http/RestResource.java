package ome.smuggler.jclient.core.service.http;

/**
 * Provides access, via the HTTP protocol, to a REST resource representable as
 * a Java object.
 * @param <T> the type of the objects that represent the resource.
 */
public interface RestResource<T> {

    /**
     * GET the resource.
     * @param resourceType the type of the object to deserialize the resource
     *                     into; generics are not supported.
     * @return the resource as an object.
     * @throws RuntimeException If a connection or protocol error occurs or if
     * the returned data could not be converted to an object.
     */
    T get(final Class<T> resourceType);

    /**
     * POST the resource.
     * @param resource the resource to POST.
     * @throws RuntimeException If a connection or protocol error occurs or if
     * the resource could not be serialized.
     */
    void post(T resource);

    /**
     * PUT the resource.
     * @param resource the resource to PUT.
     * @throws RuntimeException If a connection or protocol error occurs or if
     * the resource could not be serialized.
     */
    void put(T resource);

    /**
     * DELETE the resource.
     * @throws RuntimeException If a connection or protocol error occurs.
     */
    void delete();

}
