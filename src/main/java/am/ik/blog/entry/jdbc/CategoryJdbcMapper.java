package am.ik.blog.entry.jdbc;

import java.util.List;

import am.ik.blog.entry.CategoryMapper;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryJdbcMapper implements CategoryMapper {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public CategoryJdbcMapper(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<String> findAllConcatenatedCategory() {
		return this.jdbcTemplate.query(
				"SELECT DISTINCT GROUP_CONCAT(DISTINCT category_name ORDER BY category_order ASC SEPARATOR ',') category"
						+ " FROM category" //
						+ " GROUP BY entry_id" //
						+ " ORDER BY category",
				(rs, i) -> rs.getString("category"));
	}

	@Override
	public List<String> findConcatenatedCategoryLikeCategoryName(String categoryName) {
		MapSqlParameterSource source = new MapSqlParameterSource()
				.addValue("category_name", categoryName + "%");
		return this.jdbcTemplate.query(
				" SELECT DISTINCT GROUP_CONCAT(DISTINCT category_name ORDER BY category_order ASC SEPARATOR ',') category"
						+ " FROM category" //
						+ " GROUP BY entry_id" //
						+ " HAVING category LIKE :category_name" //
						+ " ORDER BY category",
				source, (rs, i) -> rs.getString("category"));
	}
}
