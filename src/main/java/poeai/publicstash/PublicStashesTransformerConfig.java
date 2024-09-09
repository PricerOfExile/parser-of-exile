package poeai.publicstash;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "public-stashes")
public class PublicStashesTransformerConfig {

    private Path sourceFolder;

    private int itemReadLimit;

    public Path getSourceFolder() {
        return sourceFolder;
    }

    public void setSourceFolder(Path sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    public int getItemReadLimit() {
        return itemReadLimit;
    }

    public void setItemReadLimit(int itemReadLimit) {
        this.itemReadLimit = itemReadLimit;
    }
}
