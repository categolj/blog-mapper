package am.ik.blog.entry;

import static am.ik.blog.entry.Asserts.*;
import static am.ik.blog.entry.criteria.SearchCriteria.defaults;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import am.ik.blog.entry.criteria.CategoryOrders;
import am.ik.blog.entry.criteria.SearchCriteria;
import am.ik.blog.mybatis.AutoConfigureMyBatis;

@RunWith(SpringRunner.class)
@JdbcTest
@ContextConfiguration
@AutoConfigureMyBatis
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({ "classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql" })
public class EntryMapperTest {
	@Autowired
	EntryMapper entryMapper;

	@Test
	public void count() throws Exception {
		assertThat(entryMapper.count(SearchCriteria.DEFAULT)).isEqualTo(3L);
	}

	@Test
	public void findOne() throws Exception {
		Entry entry = entryMapper.findOne(new EntryId(99999L));
		assertEntry99999(entry, false);
	}

	@Test
	public void findPageIncludeContent() throws Exception {
		Page<Entry> entries = entryMapper.findPage(
				SearchCriteria.builder().excludeContent(false).build(),
				new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(3L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0), false);
		assertEntry99998(content.get(1), false);
		assertEntry99997(content.get(2), false);
	}

	@Test
	public void findPageDefault() throws Exception {
		Page<Entry> entries = entryMapper.findPage(SearchCriteria.DEFAULT,
				new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(3L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0), true);
		assertEntry99998(content.get(1), true);
		assertEntry99997(content.get(2), true);
	}

	@Test
	public void findPageByCreatedBy() throws Exception {
		SearchCriteria criteria = defaults().createdBy(new Name("making")).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(2L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0), true);
		assertEntry99998(content.get(1), true);
	}

	@Test
	public void findPageByLastModifiedBy() throws Exception {
		SearchCriteria criteria = defaults().lastModifiedBy(new Name("making")).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(3L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0), true);
		assertEntry99998(content.get(1), true);
		assertEntry99997(content.get(2), true);
	}

	@Test
	public void findPageByCategory() throws Exception {
		SearchCriteria criteria = defaults()
				.categoryOrders(new CategoryOrders().add(new Category("y"), 2)).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(2L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0), true);
		assertEntry99997(content.get(1), true);
	}

	@Test
	public void findPageByTag() throws Exception {
		SearchCriteria criteria = defaults().tag(new Tag("test3")).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(2L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0), true);
		assertEntry99997(content.get(1), true);
	}

	@Test
	public void insert() throws Exception {
		Entry entry = entryMapper.findOne(new EntryId(99999L)).copy()
				.entryId(new EntryId(89999L)).build();
		entryMapper.save(entry);
		assertThat(entryMapper.findOne(new EntryId(89999L))).isEqualTo(entry);
	}

	@Test
	public void update() throws Exception {
		Entry entry = entryMapper.findOne(new EntryId(99999L)).copy()
				.content(new Content("Updated")).build();
		entryMapper.save(entry);
		assertThat(entryMapper.findOne(new EntryId(99999L))).isEqualTo(entry);
	}

	@Test
	public void delete() throws Exception {
		int count = entryMapper.delete(new EntryId(99999L));
		assertThat(count).isEqualTo(1);
		assertThat(entryMapper.findOne(new EntryId(99999L))).isNull();
	}

	@Configuration
	static class Config {
	}
}