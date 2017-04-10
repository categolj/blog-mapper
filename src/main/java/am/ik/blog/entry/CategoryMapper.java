package am.ik.blog.entry;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
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

	@Select("SELECT DISTINCT GROUP_CONCAT(DISTINCT category_name ORDER BY category_order ASC SEPARATOR ',') category FROM category GROUP BY entry_id ORDER BY category")
	List<String> findAllConcatenatedCategory();

	List<String> findConcatenatedCategoryLikeCategoryName(
			@Param("categoryName") String categoryName);
}
