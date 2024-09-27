package poe.evaluation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import poe.evaluation.modelapi.PoEModelAPIClient;

@RestController
@RequiredArgsConstructor
public class ItemEvaluationController {

    private final FrontItemParser frontItemParser;
    private final PoEModelAPIClient poEModelAPIClient;

    @PostMapping(value = "evaluate", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> evaluate(@RequestBody String item){
        var parsedItem = frontItemParser.execute(item);
        var getAiResult = poEModelAPIClient.getAiResult(parsedItem);
        return ResponseEntity.ok(getAiResult);
    }
}
