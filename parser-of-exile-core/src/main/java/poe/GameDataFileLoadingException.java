package poe;

import org.springframework.core.io.Resource;

import static java.lang.String.format;

public class GameDataFileLoadingException extends RuntimeException {

    public GameDataFileLoadingException(Resource resource,
                                        Throwable cause) {
        super(format("Cannot load game data file %s.", resource.getFilename()), cause);
    }
}
