package poeai.stat.tables.English;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModDtoRepositoryTest {

    private ModDtoRepository modDtoRepository;

    @Nested
    class GivenEmptyModDtosList {

        @BeforeEach
        void before() {
            modDtoRepository = new ModDtoRepository(List.of());
        }

        @Test
        void thenFindAllNonUniqueEquipmentModsIsEmpty() {
            assertThat(modDtoRepository.findAllNonUniqueAndEquipmentRelated())
                    .isEmpty();
        }
    }

    @Nested
    class GivenAnEquipmentRelatedMod {

        @Nested
        class GivenThisModIsNotUniqueNorWeaponTree {

            private ModDto expectedModDto;

            @BeforeEach
            void before() {
                expectedModDto = mock(ModDto.class);
                when(expectedModDto.isEquipmentRelated())
                        .thenReturn(true);
                when(expectedModDto.isNotUniqueNorWeaponTree())
                        .thenReturn(true);
                modDtoRepository = new ModDtoRepository(List.of(expectedModDto));
            }

            @Test
            void thenFindAllNonUniqueEquipmentModsReturnsExpectedMod() {
                assertThat(modDtoRepository.findAllNonUniqueAndEquipmentRelated())
                        .contains(expectedModDto);
            }
        }

        @Nested
        class GivenThisModIsUniqueOrWeaponTree {

            @BeforeEach
            void before() {
                ModDto modDto = mock(ModDto.class);
                when(modDto.isEquipmentRelated())
                        .thenReturn(true);
                when(modDto.isNotUniqueNorWeaponTree())
                        .thenReturn(false);
                modDtoRepository = new ModDtoRepository(List.of(modDto));
            }

            @Test
            void thenFindAllNonUniqueEquipmentModsReturnsEmpty() {
                assertThat(modDtoRepository.findAllNonUniqueAndEquipmentRelated())
                        .isEmpty();
            }
        }
    }

    @Nested
    class GivenAnModWhichIsNotEquipmentRelated {

        @Nested
        class GivenThisModIsUniqueOrWeaponTree {

            @BeforeEach
            void before() {
                ModDto modDto = mock(ModDto.class);
                when(modDto.isEquipmentRelated())
                        .thenReturn(false);
                modDtoRepository = new ModDtoRepository(List.of(modDto));
            }

            @Test
            void thenFindAllNonUniqueEquipmentModsReturnsEmpty() {
                assertThat(modDtoRepository.findAllNonUniqueAndEquipmentRelated())
                        .isEmpty();
            }
        }
    }

    @Nested
    class IntegrationTest {

        @BeforeEach
        void before() throws IOException {
            var objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            var modsFile = ModDtoRepository.class.getResourceAsStream("Mods.json");
            TypeReference<List<ModDto>> listModDtoType = new TypeReference<>() {
            };
            modDtoRepository = new ModDtoRepository(objectMapper.readValue(modsFile, listModDtoType));
        }

        @Test
        void thenFindAllNonUniqueEquipmentModsReturnsElements() {
            assertThat(modDtoRepository.findAllNonUniqueAndEquipmentRelated())
                    .hasSizeGreaterThan(0);
        }
    }
}
