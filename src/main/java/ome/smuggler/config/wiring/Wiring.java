package ome.smuggler.config.wiring;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.service.imports.ImportLogDisposer;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.core.service.imports.ImportTracker;
import ome.smuggler.core.service.imports.impl.ImportEnv;
import ome.smuggler.core.service.imports.impl.ImportFailureHandler;
import ome.smuggler.core.service.imports.impl.ImportLogDeleteAction;
import ome.smuggler.core.service.imports.impl.ImportMonitor;
import ome.smuggler.core.service.imports.impl.ImportRunner;
import ome.smuggler.core.service.imports.impl.ImportTrigger;
import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.service.mail.impl.MailEnv;
import ome.smuggler.core.service.mail.impl.MailFailureHandler;
import ome.smuggler.core.service.mail.impl.MailTrigger;
import ome.smuggler.core.service.mail.impl.Mailer;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean wiring configuration.
 */
@Configuration
public class Wiring {
    
    @Bean
    public ImportEnv importEnv(
            ImportConfigSource config, 
            CliImporterConfig cliConfig, 
            ChannelSource<QueuedImport> importSourceChannel,
            SchedulingSource<ImportLogFile> importGcSourceChannel) {
        ImportEnv env = new ImportEnv(config, cliConfig, importSourceChannel, 
                                      importGcSourceChannel);
        env.ensureDirectories();
        return env;
    }
    
    @Bean
    public ImportRequestor importRequestor(ImportEnv env) {
        return new ImportTrigger(env);
    }
    
    @Bean
    public ImportProcessor importProcessor(ImportEnv env) {
        return new ImportRunner(env);
    }
    
    @Bean
    public ImportLogDisposer importLogDisposer() {
        return new ImportLogDeleteAction();
    }
    
    @Bean
    public FailedImportHandler failedImportHandler(ImportEnv env) {
        return new ImportFailureHandler(env);
    }
    
    @Bean
    public ImportTracker importTracker(ImportEnv env) {
        return new ImportMonitor(env);
    }
    
    /* ---------------------------------------------------------------------- */
    // MAIL
    
    @Bean
    public MailEnv mailEnv(MailConfigSource config, 
            ChannelSource<PlainTextMail> mailSourceChannel) {
        MailEnv env = new MailEnv(config, mailSourceChannel);
        
        env.ensureDirectories();
        return env;
    }
    
    @Bean 
    public MailRequestor mailRequestor() {
        return new MailTrigger();
    }
    
    @Bean
    public MailProcessor mailProcessor() {
        return new Mailer();
    }
    
    @Bean
    public FailedMailHandler failedMailHandler() {
        return new MailFailureHandler();
    }
    
}
