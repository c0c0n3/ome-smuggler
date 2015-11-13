package ome.smuggler.core.types;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import util.object.AbstractWrapper;
import util.object.Identifiable;

/**
 * Identifies an import run.
 */
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class ImportId extends AbstractWrapper<String> implements Identifiable {

    private final String uuid; 
    
    public ImportId() {
        uuid = UUID.randomUUID().toString();
    }
    
    @Override
    public String id() {
        return uuid;
    }

    @Override
    public String get() {
        return uuid;
    }
    
}
