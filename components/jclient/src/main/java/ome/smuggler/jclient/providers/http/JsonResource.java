package ome.smuggler.jclient.providers.http;

import ome.smuggler.jclient.providers.json.JsonSinkWriter;
import ome.smuggler.jclient.providers.json.JsonSourceReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;

/**
 * A simple HTTP client to operate on resources represented as JSON and encoded
 * in UTF-8.
 * This client reads HTTP entities into memory in their entirety, which is not
 * likely to be appropriate when dealing with large amounts of data where it
 * would be desirable to process content in constant memory space. (In that
 * case using an object to represent your data may not be the best option anyway
 * and streaming might be your answer instead...)
 */
public class JsonResource<T> {

    private static void acceptJsonOnly(HttpRequest request) {
        ContentType json = ContentType.APPLICATION_JSON;
        request.addHeader("Accept", json.getMimeType());
        request.addHeader("Accept-Charset", json.getCharset().name());  // (*)
    }
    // (*) ContentType.APPLICATION_JSON is constructed with a charset of UTF-8.

    private static void assert2xx(HttpResponse response)
            throws HttpResponseException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            String detail = null;
            try {
                String body = EntityUtils.toString(response.getEntity());
                detail = String.format("[%s] %s",
                                        statusLine.getReasonPhrase(), body);
            } catch (Exception e) {
                detail = statusLine.getReasonPhrase();
            }
            throw new HttpResponseException(statusLine.getStatusCode(), detail);
        }
    }

    private static HttpEntity fetchEntity(HttpResponse response)
            throws ClientProtocolException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new ClientProtocolException("Response contains no entity.");
        }
        return entity;
    }

    private static ContentType ensureJsonContent(HttpEntity entity)
            throws ClientProtocolException {
        ContentType type = ContentType.get(entity);
        String expected = ContentType.APPLICATION_JSON     // (1)
                                     .toString()
                                     .replace(" ", "");    // (2)
        if (type == null) {
            throw new ClientProtocolException("Unspecified content type.");
        }
        String actual = type.toString().replace(" ", "");  // (2)
        if (!expected.equalsIgnoreCase(actual)) {
            throw new ClientProtocolException(
                    String.format("Invalid content type: %s", actual));
        }
        return type;
    }
    /* (1) It's constructed with a charset of UTF-8, so when string-ified it
     * becomes: "application/json; charset=utf-8".
     * (2) Make sure these two are considered equal (note the extra space):
     * "application/json;charset=utf-8", "application/json; charset=utf-8"
     */


    private final URI target;
    private final CloseableHttpClient client;

    /**
     * Creates a new instance to operate on the specified resource.
     * @param target identifies the target resource to operate on.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonResource(URI target) {
        requireNonNull(target, "target");

        this.target = target;
        this.client = HttpClients.createDefault();
    }

    private StringEntity toEntity(T resource) {
        StringWriter sink = new StringWriter();
        new JsonSinkWriter<>(sink).write(resource);
        String serializedResource = sink.toString();
        return new StringEntity(serializedResource,
                                ContentType.APPLICATION_JSON);  // (*)
    }
    // (*) It's constructed with a charset of UTF-8.

    private T fromEntity(HttpEntity resource, Class<T> resourceType)
            throws IOException {
        ContentType type = ensureJsonContent(resource);
        Reader source = new InputStreamReader(
                resource.getContent(), type.getCharset());
        return new JsonSourceReader<>(resourceType, source).read();
    }

    private T handleGet(HttpResponse response, Class<T> resourceType)
            throws IOException {
        assert2xx(response);
        HttpEntity entity = fetchEntity(response);
        return fromEntity(entity, resourceType);
    }

    private ResponseHandler<T> ignoreResponseBodyHandler() {
        return new ResponseHandler<T>() {
            @Override
            public T handleResponse(HttpResponse response) throws IOException {
                assert2xx(response);
                return null;
            }
        };
    }

    /**
     * GET the resource.
     * @param resourceType the type of the JSON object; generics are not
     *                     supported.
     * @return the resource as an object.
     * @throws IOException If a connection or protocol error occurs.
     * @throws RuntimeException if the returned JSON could not be converted to
     * an object.
     */
    public T get(final Class<T> resourceType) throws IOException {
        HttpGet get = new HttpGet(target);
        acceptJsonOnly(get);
        return client.execute(get, new ResponseHandler<T>() {
            @Override
            public T handleResponse(HttpResponse response) throws IOException {
                return handleGet(response, resourceType);
            }
        });
    }

    /**
     * POST the resource.
     * @param resource the resource to POST.
     * @throws IOException If a connection or protocol error occurs.
     * @throws RuntimeException if the resource could not be serialized to JSON.
     */
    public void post(T resource) throws IOException {
        HttpPost post = new HttpPost(target);
        acceptJsonOnly(post);
        post.setEntity(toEntity(resource));
        client.execute(post, ignoreResponseBodyHandler());
    }

    /**
     * PUT the resource.
     * @param resource the resource to PUT.
     * @throws IOException If a connection or protocol error occurs.
     * @throws RuntimeException if the resource could not be serialized to JSON.
     */
    public void put(T resource) throws IOException {
        HttpPut put = new HttpPut(target);
        acceptJsonOnly(put);
        put.setEntity(toEntity(resource));
        client.execute(put, ignoreResponseBodyHandler());
    }

    /**
     * DELETE the resource.
     * @throws IOException If a connection or protocol error occurs.
     */
    public void delete() throws IOException {
        HttpDelete del = new HttpDelete(target);
        client.execute(del, ignoreResponseBodyHandler());
    }

}
