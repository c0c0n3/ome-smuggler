package ome.smuggler.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import ome.smuggler.core.io.FileOps;
import ome.smuggler.core.types.Nat;

@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(ImportController.ImportUrl)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportStatusController {

    private static final String ImportIdPathVar = "importId";
    
    @RequestMapping(method = GET,
                    value = "{" + ImportIdPathVar + "}",
                    produces = MediaType.TEXT_PLAIN_VALUE)  // has no effect, no content type set
    public void getStatusUpdate(
            @PathVariable(value=ImportIdPathVar) String importId, 
            HttpServletResponse response) throws IOException {
        
        stream(importId, response);
    }
    
    private void stream(String importId, HttpServletResponse response) throws IOException {
        Path importLog = Paths.get("import/log/" + importId);
        Nat importLogSize = FileOps.byteLength(importLog);  
        
        if (importLogSize.get() <= 100) { // <= Integer.MAX_VALUE) {
            response.setContentLength(importLogSize.get().intValue());
        } // else can't set; will result in chunked transfer encoding.
          // (to see this, use a 200M file and don't set the content-length)
        
        ServletOutputStream out = response.getOutputStream();
        FileOps.transfer(importLog, importLogSize, out);
        // NB file is possibly being written to, so we can't just use Files.copy
        // as it may write more bytes than we declared for the content-length.
    }
    
    
    private void streamLargeFile(HttpServletResponse response) throws IOException {
        Path f200 = Paths.get("/home/andrea/f200.mib");
        Nat len = FileOps.byteLength(f200);
        response.setContentLength(len.get().intValue());  // if not set => chunked enc
        ServletOutputStream out = response.getOutputStream();
        //out.flush();  // makes no difference; file's never sucked into mem anyhoo
        FileOps.transfer(f200, len, out);
    }
    
    private void plainCopy(String importId, HttpServletResponse response) throws IOException {
        Path p = Paths.get("import/log/" + importId);
        ServletOutputStream out = response.getOutputStream();
        Files.copy(p, out);
    }
    
    private void servletBuffering(int len, HttpServletResponse response) throws IOException {
        response.setContentLength(len);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        for (int k = 0; k < len / 2; ++k) out.write(90);
        //out.flush();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int k = 0; k < len / 2; ++k) out.write(91);
        //out.flush();
    }
    
    private void avoidServletBuffering(int len, HttpServletResponse response) throws IOException {
        response.setContentLength(len);
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        for (int k = 0; k < len / 2; ++k) out.write(90);
        out.flush();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int k = 0; k < len / 2; ++k) out.write(91);
        out.flush();
    }
    
}
