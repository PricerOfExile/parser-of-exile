package poe.evaluation;

import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import poe.evaluation.modelapi.PoEModelAPIClient;

@RestController
public class ItemEvaluationController {

    @Nonnull
    private final FrontItemParser frontItemParser;
    @Nonnull
    private final PoEModelAPIClient poEModelAPIClient;

    public ItemEvaluationController(@Nonnull FrontItemParser frontItemParser,
                                    @Nonnull PoEModelAPIClient poEModelAPIClient) {
        this.frontItemParser = frontItemParser;
        this.poEModelAPIClient = poEModelAPIClient;
    }

    // TODO - MTI - Shouldn't we expose something else than TEXT ?
    @PostMapping(value = "/evaluate", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> evaluate(@RequestBody String item) {
        var parsedItem = frontItemParser.execute(item);
        var getAiResult = poEModelAPIClient.getAiResult(parsedItem);
        return ResponseEntity.ok(getAiResult);
    }
}
