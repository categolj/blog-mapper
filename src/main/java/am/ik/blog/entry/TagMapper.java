package am.ik.blog.entry;

import java.util.List;

import static java.util.stream.Collectors.toList;

public interface TagMapper {
	default List<Tag> findOrderByTagNameAsc() {
		return findTagStringOrderByTagNameAsc().stream().map(Tag::new).collect(toList());
	}

	List<String> findTagStringOrderByTagNameAsc();
}
