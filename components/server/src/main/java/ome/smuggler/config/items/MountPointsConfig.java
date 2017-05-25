package ome.smuggler.config.items;

import java.util.Arrays;
import java.util.Objects;

/**
 * TODO
 */
public class MountPointsConfig {
    // NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
    // be (de-)serialized painlessly by SnakeYaml.

    public static class RemoteToLocalMapping {
        // NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
        // be (de-)serialized painlessly by SnakeYaml.

        private String remotePrefix;
        private String localMount;

        public String getRemotePrefix() {
            return remotePrefix;
        }

        public void setRemotePrefix(String remotePrefix) {
            this.remotePrefix = remotePrefix;
        }

        public String getLocalMount() {
            return localMount;
        }

        public void setLocalMount(String localMount) {
            this.localMount = localMount;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof RemoteToLocalMapping) {
                return Objects.equals(other.toString(), this.toString());
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("%s | %s", remotePrefix, localMount);
        }
    }


    private boolean enableTranslation;
    private RemoteToLocalMapping[] remoteToLocalMap;

    public boolean isEnableTranslation() {
        return enableTranslation;
    }

    public void setEnableTranslation(boolean enableTranslation) {
        this.enableTranslation = enableTranslation;
    }

    public RemoteToLocalMapping[] getRemoteToLocalMap() {
        return remoteToLocalMap;
    }

    public void setRemoteToLocalMap(RemoteToLocalMapping[] remoteToLocalMap) {
        this.remoteToLocalMap = remoteToLocalMap;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MountPointsConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }

    @Override
    public String toString() {
        String xs = Arrays.toString(remoteToLocalMap);
        return String.format("%s | %s", enableTranslation, xs);
    }

}
