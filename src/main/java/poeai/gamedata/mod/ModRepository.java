package poeai.gamedata.mod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import poeai.gamedata.GameDataFileLoadingException;

import java.util.List;
import java.util.Objects;

@Service
public class ModRepository {

    private final List<Mod> mods;

    public ModRepository(@Value("classpath:/poe.gamedata/tables/English/Mods.json") @Nonnull Resource resource,
                         @Nonnull ObjectMapper objectMapper) {
        Objects.requireNonNull(resource, "Resource is mandatory.");
        Objects.requireNonNull(objectMapper, "ObjectMapper is mandatory.");
        TypeReference<List<Mod>> listModType = new TypeReference<>() {
            // Note : hint for Jackson to parse the file.
        };
        try(var inputStream = resource.getInputStream()) {
            mods = objectMapper.readValue(inputStream, listModType);
        } catch (Exception e) {
            throw new GameDataFileLoadingException(resource, e);
        }
    }

    public List<Mod> findAllNonUniqueAndEquipmentRelated() {
        return mods.stream()
                .filter(Mod::isEquipmentRelated)
                .filter(Mod::isNotUniqueNorWeaponTree)
                .toList();
    }
}
