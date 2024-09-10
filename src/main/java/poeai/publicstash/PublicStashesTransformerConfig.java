package poeai.publicstash;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "public-stashes")
public class PublicStashesTransformerConfig {

    private Path sourceFolder;

    private Path targetFolder;

    public Path getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(Path targetFolder) {
        this.targetFolder = targetFolder;
    }

    public Path getSourceFolder() {
        return sourceFolder;
    }

    public void setSourceFolder(Path sourceFolder) {
        this.sourceFolder = sourceFolder;
    }
}
