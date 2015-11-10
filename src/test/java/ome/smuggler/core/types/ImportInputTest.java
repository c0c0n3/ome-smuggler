package ome.smuggler.core.types;

import static ome.smuggler.core.types.ValueParserFactory.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class ImportInputTest {

    private static PositiveN posN(String value) {
        return positiveInt(value).getRight();
    }
    
    private TextAnnotation anno(String ns, String value) {
        return textAnnotation(ns, value).getRight();
    }
    
    private static ImportInput makeNew() {
        return new ImportInput(email("user@micro.edu").getRight(), 
                               uri("target/file").getRight(), 
                               uri("omero:1234").getRight(), 
                               "sessionKey");
    }
    
    private static void assertEmptyOptional(Optional<?> x) {
        assertNotNull(x);
        assertFalse(x.isPresent());
    }
    
    private static void assertOptionalValue(Optional<?> actual, Object expected) {
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(expected));
    }
    
    private static <T> void assertStream(Stream<T> actual, T[] expected) {
        assertNotNull(actual);
        assertArrayEquals(expected, actual.toArray());
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullEmail() {
        new ImportInput(null, 
                        uri("target").getRight(), 
                        uri("omero").getRight(), 
                        "sessionKey");
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullTarget() {
        new ImportInput(email("some@where").getRight(), 
                        null, 
                        uri("omero").getRight(), 
                        "sessionKey");
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullOmero() {
        new ImportInput(email("some@where").getRight(), 
                        uri("target").getRight(), 
                        null, 
                        "sessionKey");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullSessionKey() {
        new ImportInput(email("some@where").getRight(), 
                        uri("target").getRight(), 
                        uri("omero").getRight(), 
                        null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptySessionKey() {
        new ImportInput(email("some@where").getRight(), 
                        uri("target").getRight(), 
                        uri("omero").getRight(), 
                        "");
    }
    
    @Test
    public void nameEmptyIfUnset() {
        assertEmptyOptional(makeNew().getName());
        assertEmptyOptional(makeNew().setName(null).getName());
    }
    
    @Test
    public void nameHasValueIfSet() {
        assertOptionalValue(makeNew().setName("").getName(), "");
        assertOptionalValue(makeNew().setName("x").getName(), "x");
    }
    
    @Test
    public void descriptionEmptyIfUnset() {
        assertEmptyOptional(makeNew().getDescription());
        assertEmptyOptional(makeNew().setDescription(null).getDescription());
    }
    
    @Test
    public void descriptionHasValueIfSet() {
        assertOptionalValue(makeNew().setDescription("").getDescription(), "");
        assertOptionalValue(makeNew().setDescription("x").getDescription(), "x");
    }
    
    @Test
    public void datatsetOrScreenEmptyIfUnset() {
        assertEmptyOptional(makeNew().getDatasetOrScreenId());
    }
    
    @Test
    public void settingDatasetOverridesScreen() {
        PositiveN one = posN("1"), two = posN("2");
        ImportInput x = makeNew();
        x.setScreenId(one).setDatasetId(two);
        
        assertOptionalValue(x.getDatasetOrScreenId(), two);
    }
    
    @Test
    public void settingScreenOverridesDataset() {
        PositiveN one = posN("1"), two = posN("2");
        ImportInput x = makeNew();
        x.setDatasetId(one).setScreenId(two);
        
        assertOptionalValue(x.getDatasetOrScreenId(), two);
    }
    
    @Test (expected = NullPointerException.class)
    public void setDatasetIdThrowsIfNullArg() {
        makeNew().setDatasetId(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void setScreenIdThrowsIfNullArg() {
        makeNew().setScreenId(null);
    }
    
    @Test
    public void textAnnotationsEmptyIfUnset() {
        assertStream(makeNew().getTextAnnotations(), array());
        assertStream(makeNew().addTextAnnotation().getTextAnnotations(), array());
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationThrowsIfNullArray() {
        makeNew().addTextAnnotation((TextAnnotation[])null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationThrowsIfNullArg() {
        makeNew().addTextAnnotation((TextAnnotation)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationThrowsIfArrayHasNulls() {
        makeNew().addTextAnnotation(anno("n", "v"), (TextAnnotation)null);
    }
    
    @Test
    public void annotationsReturnedInSameOrderTheyWereAdded() {
        TextAnnotation a1 = anno("n1", "v1");
        TextAnnotation a2 = anno("n2", "v2");
        assertStream(makeNew().addTextAnnotation(a1, a2).getTextAnnotations(), 
                     array(a1, a2));
    }
    
    @Test
    public void annotationIdsEmptyIfUnset() {
        assertStream(makeNew().getAnnotationIds(), array());
        assertStream(makeNew().addAnnotationId().getAnnotationIds(), array());
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdThrowsIfNullArray() {
        makeNew().addAnnotationId((PositiveN[])null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdThrowsIfNullArg() {
        makeNew().addAnnotationId((PositiveN)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdThrowsIfArrayHasNulls() {
        makeNew().addAnnotationId(posN("1"), (PositiveN)null);
    }
    
    @Test
    public void annotationIdsReturnedInSameOrderTheyWereAdded() {
        PositiveN id1 = posN("1");
        PositiveN id2 = posN("2");
        assertStream(makeNew().addAnnotationId(id1, id2).getAnnotationIds(), 
                     array(id1, id2));
    }
    
}
