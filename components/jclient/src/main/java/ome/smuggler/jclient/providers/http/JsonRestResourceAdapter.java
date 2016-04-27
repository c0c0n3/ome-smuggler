package ome.smuggler.jclient.providers.http;

import ome.smuggler.jclient.core.service.http.RestResource;

import java.io.IOException;
import java.net.URI;

/**
 * Turns a {@link JsonResource} into a {@link RestResource} by wrapping errors
 * into runtime exceptions.
 */
public class JsonRestResourceAdapter<T> implements RestResource<T> {

    private final JsonResource<T> adaptee;

    /**
     * Creates a new instance to operate on the specified resource.
     * @param target identifies the target resource to operate on.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonRestResourceAdapter(URI target) {
        this.adaptee = new JsonResource<>(target);
    }

    @Override
    public T get(Class<T> resourceType) {
        try {
            return adaptee.get(resourceType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void post(final T resource) {
        try {
            adaptee.post(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(T resource) {
        try {
            adaptee.put(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete() {
        try {
            adaptee.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
