package ome.smuggler.run;

import static java.util.stream.Collectors.joining;
import static util.sequence.Streams.intersperse;
import static util.string.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ome.smuggler.config.Profiles;

/**
 * Import sever entry point.
 */
public class ImportServer implements RunnableApp {

    @Override
    public void run(List<String> appArgs) {
        String[] p = getProfiles(appArgs);
        for (String x : p) System.out.println(">>> " + x);
    }

    protected String[] getProfiles(List<String> appArgs) {
        if (appArgs == null || appArgs.isEmpty()) {
            appArgs = new ArrayList<>();
            appArgs.add(Profiles.Prod);
        }
        else {
            checkProfiles(appArgs.stream());
        }
        
        return appArgs.toArray(new String[appArgs.size()]);
    }
    
    private void checkProfiles(Stream<String> xs) {
        String badProfiles = intersperse(() -> ", ", 
                                         Profiles.findUnknownProfiles(xs))
                            .collect(joining());
        
        if (!isNullOrEmpty(badProfiles)) {
            throw new IllegalArgumentException(
                                            "Unknown profiles: " + badProfiles);
        }
    }
    
}
