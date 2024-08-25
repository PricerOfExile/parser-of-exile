package poeai.item.dto;

import java.util.List;

public record ItemProperty(String name,
                           List<List<Object>> values) {
}
