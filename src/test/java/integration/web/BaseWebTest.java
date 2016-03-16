package integration.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static util.sequence.Arrayz.isNullOrZeroLength;

import java.io.StringReader;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.providers.json.JsonSourceReader;
import util.servlet.http.CharEncodingFilter;


@WebAppConfiguration
@ContextHierarchy({
        //@ContextConfiguration(classes = Wiring.class),
        @ContextConfiguration(classes = IntegrationWiring.class)
})
//@ActiveProfiles({Profiles.Dev})
public class BaseWebTest {
    
    @Autowired
    protected WebApplicationContext wac;
    
    protected MockMvc mockMvc;
    
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                 .webAppContextSetup(wac)
                 .addFilters(
                         CharEncodingFilter.Utf8Request(), 
                         CharEncodingFilter.Utf8Response())
                 .build();
        
        additionalSetup();
    }
    
    protected void additionalSetup() { }
    
    protected ResultActions doGet(String url, MediaType...ts) throws Exception {
        MockHttpServletRequestBuilder request = get(url);
        if (!isNullOrZeroLength(ts)) {
            request.accept(ts);
        }
        return mockMvc
              .perform(request)
              .andDo(print());  // comment this in/out to see/hide Spring dump
    }
    
    protected String doGetAndReturnResponseBody(String url) throws Exception {
        return doGet(url).andReturn().getResponse().getContentAsString();
    }
    
    protected ResultActions expectTextUtf8ContentType(ResultActions x) 
            throws Exception {
        return x
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().encoding("UTF-8"));
    }
    
    protected <T> T doGetAndReadJson(String url, Class<T> valueType) 
            throws Exception {
        String serializedData = 
                doGet(url, MediaType.APPLICATION_JSON)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        StringReader source = new StringReader(serializedData);
        SourceReader<T> reader = new JsonSourceReader<>(valueType, source);
            
        return reader.read();
    }
    
}
