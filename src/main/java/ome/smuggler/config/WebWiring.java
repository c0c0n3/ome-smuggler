package ome.smuggler.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Additional Spring bean wiring and configuration for the web app.
 */
@Configuration
public class WebWiring extends WebMvcConfigurerAdapter {

    private void setStringConverterMediaTypes(HttpMessageConverter<?> x) {
        StringHttpMessageConverter converter = (StringHttpMessageConverter) x;
        List<MediaType> mediaTypes = Arrays.asList(
                new MediaType("text", "plain", StandardCharsets.UTF_8), 
                MediaType.ALL);
        converter.setSupportedMediaTypes(mediaTypes);
    }
    
    @Override 
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                  .filter(x -> x instanceof StringHttpMessageConverter)
                  .forEach(this::setStringConverterMediaTypes);
    }
    /* NB this overrides the default string converter which uses ISO-8859-1 as
     * default character encoding. The default encoding is what this converter
     * uses if the request doesn't specify one.
     * So with this set up, we can produce response strings defaulted to UTF-8
     * without having to specify UTF-8 as charset every time. 
     */
    /* NOTE. The method configureMessageConverters gives you an empty list to
     * which to add your converters; this list will be used in place of the
     * default Spring converters.
     */
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer cfg) {
        cfg.favorParameter(false)      // disable 'resource?format=xxx'
           .favorPathExtension(false)  // disable 'resource.xxx'
           .ignoreAcceptHeader(false)  // use Accept header
           .defaultContentType(MediaType.TEXT_PLAIN);  // (*)
    }
    /* (*) this way methods that return a string need to do nothing and the
     * client doesn't need to specify an accept header either.
     */
}
