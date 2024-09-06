package poeai.stat.tables.English;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;
import poeai.gamedata.tag.Tag;

import static org.assertj.core.api.Assertions.assertThat;

class TagTest {

    @ParameterizedTest
    @FieldSource(value = "poeai.gamedata.tag.Tag#GENERIC_IDS")
    void whenGenericIds_thenTagIsAccessoryTag(String id) {
        assertThat(new Tag(1, id).isGenericTag())
                .isTrue();
    }

    @ParameterizedTest
    @FieldSource(value = "poeai.gamedata.tag.Tag#ACCESSORY_KEYWORDS")
    void whenAccessoryKeywords_thenTagIsAccessoryTag(String id) {
        assertThat(new Tag(1, id).isAccessoryTag())
                .isTrue();
    }

    @ParameterizedTest
    @FieldSource(value = "poeai.gamedata.tag.Tag#ACCESSORY_KEYWORDS")
    void whenAccessoryKeywordsArePrefixed_thenTagIsAccessoryTag(String id) {
        assertThat(new Tag(1, "Any_Prefix" + id).isAccessoryTag())
                .isTrue();
    }

    @ParameterizedTest
    @FieldSource(value = "poeai.gamedata.tag.Tag#ACCESSORY_KEYWORDS")
    void whenAccessoryKeywordsAreSuffixed_thenTagIsAccessoryTag(String id) {
        assertThat(new Tag(1, id + "_any_suffix").isAccessoryTag())
                .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"weapon", "equipment"})
    void whenOtherIds_thenTagIsNOTLinkedToAccessory(String id) {
        assertThat(new Tag(1, id).isAccessoryTag())
                .isFalse();
        assertThat(new Tag(1, id).isGenericTag())
                .isFalse();
    }
}
