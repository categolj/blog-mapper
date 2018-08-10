package am.ik.blog.entry;

import java.util.Collections;
import java.util.List;

import am.ik.blog.entry.criteria.SearchCriteria;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface EntryMapper {
	long count(SearchCriteria searchCriteria);

	Entry findOne(EntryId entryId, boolean excludeContent);

	List<Entry> findAll(SearchCriteria searchCriteria, Pageable pageable);

	void save(Entry entry);

	int delete(EntryId entryId);

	default Page<Entry> findPage(SearchCriteria searchCriteria, Pageable pageable) {
		long count = count(searchCriteria);
		if (count <= 0) {
			return new PageImpl<>(Collections.emptyList(), pageable, count);
		}
		else {
			return new PageImpl<>(findAll(searchCriteria, pageable), pageable, count);
		}
	}

	Flux<Entry> collectAll(SearchCriteria searchCriteria, Pageable pageable);
}
