package ome.smuggler.core.types;

import static util.string.Strings.requireString;

import java.util.UUID;

import util.object.AbstractWrapper;
import util.object.Identifiable;

/**
 * Identifies an import run.
 */
public class ImportId extends AbstractWrapper<String> implements Identifiable {

    private final String uuid; 
    
    public ImportId(String uuid) {
        requireString(uuid);
        this.uuid = uuid;
    }
    
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
