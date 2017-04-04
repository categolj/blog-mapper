package am.ik.blog.entry.criteria;

import am.ik.blog.entry.Category;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by makit on 西暦17/04/05.
 */
@Data
@RequiredArgsConstructor
public class CategoryOrder {
    private final Category category;
    private final int categoryOrder;
}
