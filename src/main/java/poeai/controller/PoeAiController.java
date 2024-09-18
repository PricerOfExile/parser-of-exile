package poeai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PoeAiController {

    private final FrontItemParser frontItemParser;
    private final PythonAiService pythonAiService;

    @PostMapping("evaluate")
    public ResponseEntity<String> evaluate(@RequestBody String item){
        var parsedItem = frontItemParser.execute(item);
        var getAiResult = pythonAiService.getAiResult(parsedItem);
        return ResponseEntity.ok(getAiResult);
    }
}
