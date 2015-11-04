package ome.smuggler.core.data;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.net.URI;
import java.util.Optional;


/**
 * Details what an import task must do.
 */
public class ImportInput {

    private final Email experimenterEmail;
    private final URI target;
    private final URI omero;
    private final String sessionKey;
    private Optional<String> name;
    private Optional<String> description;    
    private Optional<PositiveInt> datasetOrScreenId;

    public ImportInput(Email experimenterEmail, URI target, URI omero, 
                       String sessionKey) {
        requireNonNull(experimenterEmail, "experimenterEmail");
        requireNonNull(target, "target");
        requireNonNull(omero, "omero");
        requireString(sessionKey, "sessionKey");
        
        this.experimenterEmail = experimenterEmail;
        this.target = target;
        this.omero = omero;
        this.sessionKey = sessionKey;
        name = Optional.empty();
        description = Optional.empty();
        datasetOrScreenId = Optional.empty();
    }

    public Email getExperimenterEmail() {
        return experimenterEmail;
    }

    public URI getTarget() {
        return target;
    }

    public URI getOmeroHost() {
        return omero;
    }

    public String getSessionKey() {
        return sessionKey;
    }
    
    public Optional<String> getName() {
        return name;
    }

    public ImportInput setName(String n) {
        this.name = Optional.ofNullable(n);
        return this;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public ImportInput setDescription(String d) {
        this.description = Optional.ofNullable(d);
        return this;
    }
    
    public Optional<PositiveInt> getDatasetOrScreenId() {
        return datasetOrScreenId;
    }
    
    public ImportInput setDatasetId(PositiveInt id) {
        requireNonNull(id, "id");
        datasetOrScreenId = Optional.of(new DatasetId(id.get()));
        return this;
    }
    
    public ImportInput setScreenId(PositiveInt id) {
        requireNonNull(id, "id");
        datasetOrScreenId = Optional.of(new ScreenId(id.get()));
        return this;
    }

    // TODO annotations!
    // should it be 2 classes? AnnoRef and AnnoText??
}
