package poe.gamedata.mod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModRepositoryTest {

    private ModRepository modRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private Resource resource;

    @Nested
    class GivenEmptyModDtosList {

        @BeforeEach
        void before() throws IOException {
            when(objectMapper.readValue(nullable(InputStream.class), any(TypeReference.class)))
                    .thenReturn(List.of());
            modRepository = new ModRepository(resource, objectMapper);
        }

        @Test
        void thenFindAllNonUniqueEquipmentModsIsEmpty() {
            assertThat(modRepository.findAllNonUniqueAndEquipmentRelated())
                    .isEmpty();
        }
    }

    @Nested
    class GivenAnEquipmentRelatedMod {

        @Nested
        class GivenThisModIsNotUniqueNorWeaponTree {

            private Mod expectedMod;

            @BeforeEach
            void before() throws IOException {
                expectedMod = mock(Mod.class);
                when(expectedMod.isEquipmentRelated())
                        .thenReturn(true);
                when(expectedMod.isNotUniqueNorWeaponTree())
                        .thenReturn(true);
                when(objectMapper.readValue(nullable(InputStream.class), any(TypeReference.class)))
                        .thenReturn(List.of(expectedMod));
                modRepository = new ModRepository(resource, objectMapper);
            }

            @Test
            void thenFindAllNonUniqueEquipmentModsReturnsExpectedMod() {
                assertThat(modRepository.findAllNonUniqueAndEquipmentRelated())
                        .contains(expectedMod);
            }
        }

        @Nested
        class GivenThisModIsUniqueOrWeaponTree {

            @BeforeEach
            void before() throws IOException {
                Mod mod = mock(Mod.class);
                when(mod.isEquipmentRelated())
                        .thenReturn(true);
                when(mod.isNotUniqueNorWeaponTree())
                        .thenReturn(false);
                when(objectMapper.readValue(nullable(InputStream.class), any(TypeReference.class)))
                        .thenReturn(List.of(mod));
                modRepository = new ModRepository(resource, objectMapper);
            }

            @Test
            void thenFindAllNonUniqueEquipmentModsReturnsEmpty() {
                assertThat(modRepository.findAllNonUniqueAndEquipmentRelated())
                        .isEmpty();
            }
        }
    }

    @Nested
    class GivenAnModWhichIsNotEquipmentRelated {

        @Nested
        class GivenThisModIsUniqueOrWeaponTree {

            @BeforeEach
            void before() throws IOException {
                Mod mod = mock(Mod.class);
                when(mod.isEquipmentRelated())
                        .thenReturn(false);
                when(objectMapper.readValue(nullable(InputStream.class), any(TypeReference.class)))
                        .thenReturn(List.of(mod));
                modRepository = new ModRepository(resource, objectMapper);
            }

            @Test
            void thenFindAllNonUniqueEquipmentModsReturnsEmpty() {
                assertThat(modRepository.findAllNonUniqueAndEquipmentRelated())
                        .isEmpty();
            }
        }
    }

    @Nested
    class IntegrationTest {

        @BeforeEach
        void before() {
            modRepository = new ModRepository(
                    new ClassPathResource("/poe.gamedata/tables/English/Mods.json"),
                    new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            );
        }

        @Test
        void thenFindAllNonUniqueEquipmentModsReturnsElements() {
            assertThat(modRepository.findAllNonUniqueAndEquipmentRelated())
                    .hasSizeGreaterThan(0);
        }
    }
}
