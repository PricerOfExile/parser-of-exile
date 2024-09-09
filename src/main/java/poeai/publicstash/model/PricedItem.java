package poeai.publicstash.model;

public record PricedItem(Item item,
                         Price price) {

    public String id() {
        return item.id();
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

    public boolean isNotTrinket() {
        return item.isNotTrinket();
    }
}
