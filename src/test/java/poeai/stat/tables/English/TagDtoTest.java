package poeai.stat.tables.English;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TagDtoTest {

    @ParameterizedTest
    @FieldSource(value = "poeai.stat.tables.English.TagDto#GENERIC_IDS")
    void whenGenericIds_thenTagDtoIsAccessoryTag(String id) {
        assertThat(new TagDto(1, id).isGenericTag())
                .isTrue();
    }

    @ParameterizedTest
    @FieldSource(value = "poeai.stat.tables.English.TagDto#ACCESSORY_KEYWORDS")
    void whenAccessoryKeywords_thenTagDtoIsAccessoryTag(String id) {
        assertThat(new TagDto(1, id).isAccessoryTag())
                .isTrue();
    }

    @ParameterizedTest
    @FieldSource(value = "poeai.stat.tables.English.TagDto#ACCESSORY_KEYWORDS")
    void whenAccessoryKeywordsArePrefixed_thenTagDtoIsAccessoryTag(String id) {
        assertThat(new TagDto(1, "Any_Prefix" + id).isAccessoryTag())
                .isTrue();
    }

    @ParameterizedTest
    @FieldSource(value = "poeai.stat.tables.English.TagDto#ACCESSORY_KEYWORDS")
    void whenAccessoryKeywordsAreSuffixed_thenTagDtoIsAccessoryTag(String id) {
        assertThat(new TagDto(1, id + "_any_suffix").isAccessoryTag())
                .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"weapon", "equipment"})
    void whenOtherIds_thenTagDtoIsNOTLinkedToAccessory(String id) {
        assertThat(new TagDto(1, id).isAccessoryTag())
                .isFalse();
        assertThat(new TagDto(1, id).isGenericTag())
                .isFalse();
    }
}
