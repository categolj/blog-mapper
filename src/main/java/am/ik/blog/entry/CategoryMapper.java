package am.ik.blog.entry;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface CategoryMapper {
	default List<Categories> findAll() {
		return findAllConcatenatedCategory().stream()
				.map(s -> new Categories(
						Stream.of(s.split(",")).map(Category::new).collect(toList())))
				.collect(toList());
	}

	default List<Categories> findLikeCategoryName(String categoryName) {
		return findConcatenatedCategoryLikeCategoryName(categoryName).stream()
				.map(s -> new Categories(
						Stream.of(s.split(",")).map(Category::new).collect(toList())))
				.collect(toList());
	}

	List<String> findAllConcatenatedCategory();

	List<String> findConcatenatedCategoryLikeCategoryName(String categoryName);
}
