package poeai.item.dto;

import java.util.List;

public record ExtendedData(String category,
                           List<String> subcategories) {

    public boolean hasSubCategory(String subCategory) {
        return subcategories.contains(subCategory);
    }
}
