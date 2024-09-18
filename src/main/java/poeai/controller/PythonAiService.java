package poeai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import poeai.publicstash.model.DumpedItem;

import java.util.Random;

@Service
@Slf4j
public class PythonAiService {

    public String getAiResult(DumpedItem dumpedItem) {
        // we are supposed to call python ai here with parsedItem
        // and return a rating
        // for now we just return random rating

        var ranks = new String[]{"S", "M", "L"};
        var random = new Random();
        var index = random.nextInt(ranks.length);
        return ranks[index];
    }

}
