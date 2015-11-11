package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.sequence.Arrayz.hasNulls;
import static util.string.Strings.requireString;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


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
    private Optional<PositiveN> datasetOrScreenId;
    private List<TextAnnotation> textAnnotations;
    private List<PositiveN> annotationIds;

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
        textAnnotations = new ArrayList<>();
        annotationIds = new ArrayList<>();
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
    
    public Optional<PositiveN> getDatasetOrScreenId() {
        return datasetOrScreenId;
    }
    
    public ImportInput setDatasetId(PositiveN id) {
        requireNonNull(id, "id");
        datasetOrScreenId = Optional.of(new DatasetId(id.get()));
        return this;
    }
    
    public ImportInput setDatasetId(Optional<DatasetId> id) {
        requireNonNull(id, "id");
        id.ifPresent(x -> setDatasetId(x));
        return this;
    }
    
    public ImportInput setScreenId(PositiveN id) {
        requireNonNull(id, "id");
        datasetOrScreenId = Optional.of(new ScreenId(id.get()));
        return this;
    }

    public ImportInput setScreenId(Optional<ScreenId> id) {
        requireNonNull(id, "id");
        id.ifPresent(x -> setScreenId(x));
        
        return this;
    }
    
    public ImportInput addTextAnnotation(TextAnnotation...xs) {
        if (xs == null || hasNulls(xs)) {  // zero len is okay tho
            throw new NullPointerException();
        }
        if (xs.length == 1) {
            textAnnotations.add(xs[0]);
        }
        if (xs.length > 1) {
            textAnnotations.addAll(Arrays.asList(xs));
        }
        
        return this;
    }
    
    public ImportInput addTextAnnotations(Stream<TextAnnotation> xs) {
        requireNonNull(xs, "xs");
        xs.forEach(x -> addTextAnnotation(x));
        
        return this;
    }
    
    public ImportInput addAnnotationId(PositiveN...xs) {
        if (xs == null || hasNulls(xs)) {  // zero len is okay tho
            throw new NullPointerException();
        }
        if (xs.length == 1) {
            annotationIds.add(xs[0]);
        }
        if (xs.length > 1) {
            annotationIds.addAll(Arrays.asList(xs));
        }
        
        return this;
    }
    
    public ImportInput addAnnotationIds(Stream<PositiveN> xs) {
        requireNonNull(xs, "xs");
        xs.forEach(x -> addAnnotationId(x));
        
        return this;
    }
    
    public Stream<TextAnnotation> getTextAnnotations() {
        return textAnnotations.stream();
    }
    
    public Stream<PositiveN> getAnnotationIds() {
        return annotationIds.stream();
    }
    
}
