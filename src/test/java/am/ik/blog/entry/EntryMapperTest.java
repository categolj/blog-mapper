package am.ik.blog.entry;

import static am.ik.blog.entry.Asserts.*;
import static am.ik.blog.entry.criteria.SearchCriteria.defaults;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import am.ik.blog.entry.criteria.CategoryOrders;
import am.ik.blog.entry.criteria.SearchCriteria;

@RunWith(SpringRunner.class)
@ContextConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({ "classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql" })
@EnableAutoConfiguration
public class EntryMapperTest {
	@Autowired
	EntryMapper entryMapper;

	@Test
	public void count() throws Exception {
		assertThat(entryMapper.count(SearchCriteria.DEFAULT)).isEqualTo(3L);
	}

	@Test
	public void findOne() throws Exception {
		Entry entry = entryMapper.findOne(new EntryId(99999L), false);
		assertEntry99999(entry).assertContent().assertFrontMatterDates();
	}

	@Test
	public void findOneExcludeContent() throws Exception {
		Entry entry = entryMapper.findOne(new EntryId(99999L), true);
		assertEntry99999(entry).assertThatContentIsNotSet().assertFrontMatterDates();
	}

	@Test
	public void findPageIncludeContent() throws Exception {
		Page<Entry> entries = entryMapper.findPage(
				SearchCriteria.builder().excludeContent(false).build(),
				new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(3L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertContent().assertFrontMatterDates();
		assertEntry99998(content.get(1)).assertContent().assertFrontMatterDates();
		assertEntry99997(content.get(2)).assertContent().assertFrontMatterDates();
	}

	@Test
	public void findPageDefault() throws Exception {
		Page<Entry> entries = entryMapper.findPage(SearchCriteria.DEFAULT,
				new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(3L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99998(content.get(1)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99997(content.get(2)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
	}

	@Test
	public void findPageByCreatedBy() throws Exception {
		SearchCriteria criteria = defaults().createdBy(new Name("making")).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(2L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99998(content.get(1)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
	}

	@Test
	public void findPageByLastModifiedBy() throws Exception {
		SearchCriteria criteria = defaults().lastModifiedBy(new Name("making")).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(3L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99998(content.get(1)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99997(content.get(2)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
	}

	@Test
	public void findPageByCategory() throws Exception {
		SearchCriteria criteria = defaults()
				.categoryOrders(new CategoryOrders().add(new Category("y"), 1)).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(2L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99997(content.get(1)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
	}

	@Test
	public void findPageByTag() throws Exception {
		SearchCriteria criteria = defaults().tag(new Tag("test3")).build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(2L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99997(content.get(1)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
	}

	@Test
	public void findPageByKeyword() throws Exception {
		SearchCriteria criteria = defaults().keyword("test").build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(3L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99998(content.get(1)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99997(content.get(2)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
	}

	@Test
	@Sql({ "classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql",
			"classpath:/update-test-data-for-search.sql" })
	public void findPageByKeyword_ModifiedData() throws Exception {
		SearchCriteria criteria = defaults().keyword("test").build();
		Page<Entry> entries = entryMapper.findPage(criteria, new PageRequest(0, 10));
		assertThat(entries.getTotalElements()).isEqualTo(2L);
		List<Entry> content = entries.getContent();
		assertEntry99999(content.get(0)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
		assertEntry99997(content.get(1)).assertThatContentIsNotSet()
				.assertFrontMatterDates();
	}

	@Test
	public void insert() throws Exception {
		Entry entry99999 = entryMapper.findOne(new EntryId(99999L), false);
		Entry entry = entry99999.copy().entryId(new EntryId(89999L)).build();
		entryMapper.save(entry);
		Entry saved = entryMapper.findOne(new EntryId(89999L), false);
		assertThat(saved).isEqualTo(entry);
		assertThat(saved.isPremium()).isFalse();
	}

	@Test
	public void insertPremium() throws Exception {
		Entry entry99997 = entryMapper.findOne(new EntryId(99997L), false);
		Entry entry = entry99997.copy().entryId(new EntryId(89997L)).build();
		entryMapper.save(entry);
		Entry saved = entryMapper.findOne(new EntryId(89997L), false);
		assertThat(saved).isEqualTo(entry);
		assertThat(saved.isPremium()).isTrue();
		assertThat(saved.frontMatter().point).isEqualTo(new PremiumPoint(50));
	}

	@Test
	public void update() throws Exception {
		Entry entry99999 = entryMapper.findOne(new EntryId(99999L), false);
		Entry entry = entry99999.copy().content(new Content("Updated")).build();
		entryMapper.save(entry);
		assertThat(entryMapper.findOne(new EntryId(99999L), false)).isEqualTo(entry);
	}

	@Test
	public void updateFromNonPremiumToPremium() throws Exception {
		Entry entry99999 = entryMapper.findOne(new EntryId(99999L), false);
		assertThat(entry99999.isPremium()).isFalse();

		FrontMatter frontMatter99999 = entry99999.getFrontMatter();
		FrontMatter frontMatter = new FrontMatter(frontMatter99999.title,
				frontMatter99999.categories, frontMatter99999.tags, frontMatter99999.date,
				frontMatter99999.updated, new PremiumPoint(200));
		Entry entry = entry99999.copy().content(new Content("Updated"))
				.frontMatter(frontMatter).build();
		entryMapper.save(entry);

		Entry one = entryMapper.findOne(new EntryId(99999L), false);
		assertThat(one).isEqualTo(entry);
		assertThat(one.isPremium()).isTrue();
		assertThat(one.frontMatter.point).isEqualTo(new PremiumPoint(200));
	}

	@Test
	public void updateFromPremiumToNonPremium() throws Exception {
		Entry entry99997 = entryMapper.findOne(new EntryId(99997L), false);
		assertThat(entry99997.isPremium()).isTrue();

		FrontMatter frontMatter99999 = entry99997.getFrontMatter();
		FrontMatter frontMatter = new FrontMatter(frontMatter99999.title,
				frontMatter99999.categories, frontMatter99999.tags, frontMatter99999.date,
				frontMatter99999.updated, PremiumPoint.UNSET);
		Entry entry = entry99997.copy().content(new Content("Updated"))
				.frontMatter(frontMatter).build();
		entryMapper.save(entry);

		Entry one = entryMapper.findOne(new EntryId(99997L), false);
		assertThat(one).isEqualTo(entry);
		assertThat(one.isPremium()).isFalse();
	}

	@Test
	public void updateChangePoint() throws Exception {
		Entry entry99997 = entryMapper.findOne(new EntryId(99997L), false);
		assertThat(entry99997.isPremium()).isTrue();
		assertThat(entry99997.frontMatter.point).isEqualTo(new PremiumPoint(50));

		FrontMatter frontMatter99999 = entry99997.getFrontMatter();
		FrontMatter frontMatter = new FrontMatter(frontMatter99999.title,
				frontMatter99999.categories, frontMatter99999.tags, frontMatter99999.date,
				frontMatter99999.updated, new PremiumPoint(0));
		Entry entry = entry99997.copy().content(new Content("Updated"))
				.frontMatter(frontMatter).build();
		entryMapper.save(entry);

		Entry one = entryMapper.findOne(new EntryId(99997L), false);
		assertThat(one).isEqualTo(entry);
		assertThat(one.isPremium()).isTrue();
		assertThat(one.frontMatter.point).isEqualTo(new PremiumPoint(0));
	}

	@Test
	public void delete() throws Exception {
		int count = entryMapper.delete(new EntryId(99999L));
		assertThat(count).isEqualTo(1);
		assertThat(entryMapper.findOne(new EntryId(99999L), false)).isNull();
	}

	@Configuration
	static class Config {
	}
}