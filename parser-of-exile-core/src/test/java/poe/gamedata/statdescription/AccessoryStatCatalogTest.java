package poe.gamedata.statdescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poe.gamedata.mod.Mod;
import poe.gamedata.mod.ModRepository;
import poe.gamedata.stat.Stat;
import poe.gamedata.stat.StatRepository;
import poe.gamedata.tag.TagRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessoryStatCatalogTest {

    private AccessoryStatCatalog accessoryStatCatalog;
    @Mock
    private ModRepository modRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private StatRepository statRepository;

    @Nested
    class GivenModRepositoryReturnsEmpty {

        @BeforeEach
        void before() {
            when(modRepository.findAllNonUniqueAndEquipmentRelated())
                    .thenReturn(List.of());
        }

        @Nested
        class WhenCallingFindAllNonUniqueAndAccessoryRelated {

            @BeforeEach
            void before() {
                accessoryStatCatalog = new AccessoryStatCatalog(
                        modRepository,
                        tagRepository,
                        statRepository
                );
            }

            @Test
            void thenResultIsEmpty() {
                assertThat(accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated())
                        .isEmpty();
            }
        }
    }

    @Nested
    class GivenOneAccessoryRelatedMod {

        private Mod accessoryRelatedMod;

        @BeforeEach
        void before() {
            accessoryRelatedMod = mock(Mod.class);
            when(accessoryRelatedMod.statIndexes())
                    .thenReturn(Set.of(1, 2, 3));

            when(modRepository.findAllNonUniqueAndEquipmentRelated())
                    .thenReturn(List.of(accessoryRelatedMod));
        }

        @Nested
        class GivenThisModCanBeCraftedOnAccessory {

            @BeforeEach
            void before() {
                when(accessoryRelatedMod.canBeCraftedOn(anyList()))
                        .thenReturn(true);
            }

            @Nested
            class WhenCallingTheMethod {

                private List<Stat> result;

                @BeforeEach
                void before() {
                    accessoryStatCatalog = new AccessoryStatCatalog(
                            modRepository,
                            tagRepository,
                            statRepository
                    );
                    result = accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated();
                }

                @Test
                void thenStatRepositoryIsCalledWith() {
                    verify(statRepository).getByIndex(1);
                    verify(statRepository).getByIndex(2);
                    verify(statRepository).getByIndex(3);
                }

                @Test
                void thenResultContains3Elements() {
                    assertThat(result)
                            .hasSize(3);
                }
            }
        }

        @Nested
        class GivenThisModCanSpawnOnAccessory {

            @BeforeEach
            void before() {
                when(accessoryRelatedMod.canBeCraftedOn(anyList()))
                        .thenReturn(true);
            }

            @Nested
            class WhenCallingTheMethod {

                private List<Stat> result;

                @BeforeEach
                void before() {
                    accessoryStatCatalog = new AccessoryStatCatalog(
                            modRepository,
                            tagRepository,
                            statRepository
                    );
                    result = accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated();
                }

                @Test
                void thenStatRepositoryIsCalledWith() {
                    verify(statRepository).getByIndex(1);
                    verify(statRepository).getByIndex(2);
                    verify(statRepository).getByIndex(3);
                }

                @Test
                void thenResultContains3Elements() {
                    assertThat(result)
                            .hasSize(3);
                }
            }
        }

        @Nested
        class GivenThisModIsSynthesis {

            @BeforeEach
            void before() {
                when(accessoryRelatedMod.isSynthesis())
                        .thenReturn(true);
            }

            @Nested
            class WhenCallingTheMethod {

                private List<Stat> result;

                @BeforeEach
                void before() {
                    accessoryStatCatalog = new AccessoryStatCatalog(
                            modRepository,
                            tagRepository,
                            statRepository
                    );
                    result = accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated();
                }

                @Test
                void thenStatRepositoryIsCalledWith() {
                    verify(statRepository).getByIndex(1);
                    verify(statRepository).getByIndex(2);
                    verify(statRepository).getByIndex(3);
                }

                @Test
                void thenResultContains3Elements() {
                    assertThat(result)
                            .hasSize(3);
                }
            }
        }

        @Nested
        class GivenThisModIsImplicitAccessory {

            @BeforeEach
            void before() {
                when(accessoryRelatedMod.isImplicitAccessory())
                        .thenReturn(true);
            }

            @Nested
            class WhenCallingTheMethod {

                private List<Stat> result;

                @BeforeEach
                void before() {
                    accessoryStatCatalog = new AccessoryStatCatalog(
                            modRepository,
                            tagRepository,
                            statRepository
                    );
                    result = accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated();
                }

                @Test
                void thenStatRepositoryIsCalledWith() {
                    verify(statRepository).getByIndex(1);
                    verify(statRepository).getByIndex(2);
                    verify(statRepository).getByIndex(3);
                }

                @Test
                void thenResultContains3Elements() {
                    assertThat(result)
                            .hasSize(3);
                }
            }
        }
    }
}
