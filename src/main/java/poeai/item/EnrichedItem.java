package poeai.item;

import poeai.item.dto.Item;

public record EnrichedItem(String stashId,
                           Item item,
                           Price price) {

    public String id() {
        return stashId + "_" + item.id();
    }

    public boolean isAccessories() {
        return item.isAccessories();
    }

    public boolean isNotUnique() {
        return item.isNotUnique();
    }

    public boolean identified() {
        return item.identified();
    }

    public boolean hasPrice() {
        return price != null;
    }
}
