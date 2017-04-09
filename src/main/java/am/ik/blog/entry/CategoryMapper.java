package am.ik.blog.entry;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper {
	@Select("SELECT DISTINCT GROUP_CONCAT(DISTINCT category_name ORDER BY category_order ASC SEPARATOR ',') category FROM category GROUP BY entry_id ORDER BY category")
	List<String> findAllConcatenatedCategory();

	List<String> findConcatenatedCategoryLikeCategoryName(
			@Param("categoryName") String categoryName);
}
