package ome.smuggler.config;

import static util.error.Exceptions.unchecked;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.web.ImportController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Additional Spring bean wiring and configuration for the web app.
 */
@Configuration
public class WebWiring extends WebMvcConfigurerAdapter {

    @Autowired
    private ImportLogConfig importLogCfg;
    
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
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path importLogDir = Paths.get(importLogCfg.getImportLogDir())
                                 .toAbsolutePath();
        unchecked(() -> Files.createDirectories(importLogDir)).get();  // (*)
        
        String importStatusPattern = String
                                   .format("%s/**", ImportController.ImportUrl);
        String statusFilesLocation = importLogDir.toUri().toString();
        registry.addResourceHandler(importStatusPattern)
                .addResourceLocations(statusFilesLocation);
    }
    /* (*) The directory must exist before we register the handler otherwise
     * registration will silently fail and instead of our import logs we'd 
     * get a fat 404 from Spring MVC...
     */
}
