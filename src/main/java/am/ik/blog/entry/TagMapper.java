package am.ik.blog.entry;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TagMapper {
	default List<Tag> findOrderByTagNameAsc() {
		return findTagStringOrderByTagNameAsc().stream().map(Tag::new).collect(toList());
	}

	@Select("SELECT tag_name FROM tag ORDER BY tag_name ASC")
	List<String> findTagStringOrderByTagNameAsc();
}
