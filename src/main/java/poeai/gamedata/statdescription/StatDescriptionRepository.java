package poeai.gamedata.statdescription;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import poeai.gamedata.GameDataFileLoadingException;
import poeai.gamedata.stat.Stat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StatDescriptionRepository {

    private final List<StatDescription> statDescriptionBlocks;

    public StatDescriptionRepository(@Value("classpath:/poe.gamedata/files/Metadata@StatDescriptions@stat_descriptions.txt") @Nonnull Resource resource) {
        var blockParser = new StatDescriptionBlockParser();
        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_16LE))) {
            reader.lines().forEachOrdered(blockParser::parse);
        } catch (Exception e) {
            throw new GameDataFileLoadingException(resource, e);
        }
        this.statDescriptionBlocks = blockParser.close().stream()
                .map(StatDescriptionBlock::split)
                .flatMap(List::stream)
                .toList();
    }

    public List<StatDescription> findAllLinkedTo(List<Stat> stats) {
        return statDescriptionBlocks.stream()
                .filter(statDescriptionBlock -> statDescriptionBlock.isLinkedToAny(stats))
                .toList();
    }
}
