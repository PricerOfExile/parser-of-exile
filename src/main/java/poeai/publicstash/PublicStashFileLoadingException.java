package poeai.publicstash;

import java.nio.file.Path;

import static java.lang.String.format;

public class PublicStashFileLoadingException extends RuntimeException {

    public PublicStashFileLoadingException(Throwable cause) {
        super("Cannot load any public stash.", cause);
    }

    public PublicStashFileLoadingException(Path path,
                                           Throwable cause) {
        super(format("Cannot load public stash %s.", path), cause);
    }
}
