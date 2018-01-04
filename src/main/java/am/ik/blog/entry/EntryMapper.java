package am.ik.blog.entry;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import am.ik.blog.entry.criteria.SearchCriteria;
import reactor.core.publisher.Flux;

@Mapper
public interface EntryMapper {
	long count(@Param("criteria") SearchCriteria searchCriteria);

	Entry findOne(@Param("entryId") EntryId entryId,
			@Param("excludeContent") boolean excludeContent);

	List<Entry> findAll(@Param("criteria") SearchCriteria searchCriteria,
			@Param("pageable") Pageable pageable);

	void collectAll(@Param("criteria") SearchCriteria searchCriteria,
			@Param("pageable") Pageable pageable, ResultHandler<Entry> handler);

	void save(Entry entry);

	int delete(@Param("entryId") EntryId entryId);

	default Page<Entry> findPage(SearchCriteria searchCriteria, Pageable pageable) {
		long count = count(searchCriteria);
		if (count <= 0) {
			return new PageImpl<>(Collections.emptyList(), pageable, count);
		}
		else {
			return new PageImpl<>(findAll(searchCriteria, pageable), pageable, count);
		}
	}

	default Flux<Entry> collectAll(SearchCriteria searchCriteria, Pageable pageable) {
		return Flux.create(sink -> {
			EntryMapper.this.collectAll(searchCriteria, pageable, context -> {
				Entry entry = context.getResultObject();
				sink.next(entry);
			});
			sink.complete();
		});
	}
}
