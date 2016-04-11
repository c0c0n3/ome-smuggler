package ome.smuggler.web;

import ome.smuggler.web.imports.ImportController;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Logs every import request and, in addition, if the application was started
 * with the DEBUG log level then any other request will be logged too.
 */
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    /**
     * Creates a new instance.
     */
    public RequestLoggingFilter() {
        setIncludeClientInfo(true);
        setIncludeQueryString(true);
        setMaxPayloadLength(4096);
        setIncludePayload(true);
    }

    private boolean isImportRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getRequestURI())
                       .map(path -> path.endsWith(ImportController.ImportUrl))
                       .orElse(false);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return isImportRequest(request) || logger.isDebugEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (isImportRequest(request)) {
            logger.info(message);
        }
        logger.debug(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        if (isImportRequest(request)) {
            logger.info(message);
        }
        logger.debug(message);
    }

}
/* NOTE. Quick solution to logging requests.
 * Better than nothing, but far from optimal. If needed we can improve on this.
 */