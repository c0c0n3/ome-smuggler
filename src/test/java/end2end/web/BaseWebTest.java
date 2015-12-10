package end2end.web;


import java.net.URI;
import java.util.Arrays;
import java.util.function.Consumer;

import ome.smuggler.Main;

import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


public class BaseWebTest {

    public static URI url(String...xs) {
        UriComponentsBuilder builder = UriComponentsBuilder
                                      .fromUriString("http://localhost:8080/");
        Arrays.asList(xs).forEach(x -> builder.path(x));
        return builder.build().toUri();
    }
    
    private static boolean serverStarted = false;
    
    @BeforeClass
    public static void startImportServer() {
        if (!serverStarted) {   // (*)
            serverStarted = true;
            Main.main(null);
        }
    }
    /* NB will work as long as JUnit runs all tests sequentially in a single
     * thread which I think it does by default?
     */
    
    protected RestTemplate httpClient;
    
    @Before
    public void setup() {
        httpClient = new TestRestTemplate();
        
        additionalSetup();
    }
    
    protected void additionalSetup() { }
    
    protected <X, Y> ResponseEntity<Y> post(URI url, X body, 
            Class<Y> responseType, Consumer<HttpHeaders> setHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        setHeaders.accept(headers);
        
        HttpEntity<X> request = new HttpEntity<>(body, headers);
        
        return httpClient.postForEntity(url, request, responseType);
    }
    
    protected <X, Y> ResponseEntity<Y> post(URI url, X body, Class<Y> responseType) {
        return post(url, body, responseType, 
                    hs -> hs.add(HttpHeaders.ACCEPT, 
                                 MediaType.APPLICATION_JSON_VALUE));
    }
    
}
