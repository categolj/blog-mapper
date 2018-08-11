package am.ik.blog.entry.jdbc;

import java.util.List;

import am.ik.blog.entry.TagMapper;

import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TagJdbcMapper implements TagMapper {
	private final JdbcTemplate jdbcTemplate;

	public TagJdbcMapper(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@NewSpan
	public List<String> findTagStringOrderByTagNameAsc() {
		return this.jdbcTemplate.query("SELECT tag_name FROM tag ORDER BY tag_name ASC",
				(rs, i) -> rs.getString("tag_name"));
	}
}
