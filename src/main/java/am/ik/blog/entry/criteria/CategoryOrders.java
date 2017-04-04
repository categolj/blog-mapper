package am.ik.blog.entry.criteria;

import java.util.HashSet;
import java.util.Set;

import am.ik.blog.entry.Category;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by makit on 西暦17/04/05.
 */
@Data
@RequiredArgsConstructor
public class CategoryOrders {
	private final Set<CategoryOrder> value = new HashSet<>();

	public CategoryOrders add(CategoryOrder categoryOrder) {
		value.add(categoryOrder);
		return this;
	}

	public CategoryOrders add(Category category, int categoryOrder) {
		return add(new CategoryOrder(category, categoryOrder));
	}
}
