package poeai.stat.tables.English;

import java.util.List;

public class ModDtoRepository {

    private final List<ModDto> modDtos;

    public ModDtoRepository(List<ModDto> modDtos) {
        this.modDtos = modDtos;
    }

    public List<ModDto> findAllNonUniqueAndEquipmentRelated() {
        return modDtos.stream()
                .filter(ModDto::isEquipmentRelated)
                .filter(ModDto::isNotUniqueNorWeaponTree)
                .toList();
    }
}
