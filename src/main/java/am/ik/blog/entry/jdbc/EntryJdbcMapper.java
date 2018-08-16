package am.ik.blog.entry.jdbc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import am.ik.blog.entry.*;
import am.ik.blog.entry.criteria.SearchCriteria;
import am.ik.blog.entry.criteria.SearchCriteria.ClauseAndParams;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static am.ik.blog.entry.jdbc.EntryExtractors.zoneOffset;
import static java.util.stream.Collectors.*;

@Repository
public class EntryJdbcMapper implements EntryMapper {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public EntryJdbcMapper(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@NewSpan
	public long count(SearchCriteria searchCriteria) {
		ClauseAndParams clauseAndParams = searchCriteria.toWhereClause();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValues(clauseAndParams.params());
		Long count = this.jdbcTemplate
				.queryForObject("SELECT count(e.entry_id) FROM entry AS e "
						+ searchCriteria.toJoinClause() + " WHERE 1=1 "
						+ clauseAndParams.clauseForEntryId(), source, Long.class);
		return count;
	}

	@Override
	@NewSpan
	public Entry findOne(@SpanTag("entryId") EntryId entryId,
			@SpanTag("excludeContent") boolean excludeContent) {
		MapSqlParameterSource source = new MapSqlParameterSource() //
				.addValue("entry_id", entryId.getValue());
		return this.jdbcTemplate.query("SELECT e.entry_id, e.title"
				+ (excludeContent ? "" : ", e.content")
				+ ", e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
				+ " FROM entry AS e LEFT OUTER JOIN category AS c ON e.entry_id = c.entry_id"
				+ " WHERE e.entry_id = :entry_id" //
				+ " ORDER BY c.category_order ASC", source,
				EntryExtractors.forEntry(excludeContent)) //
				.map(e -> {
					List<Tag> tags = this.jdbcTemplate.query(
							"SELECT tag_name FROM entry_tag WHERE entry_id = :entry_id",
							source, (rs, i) -> new Tag(rs.getString("tag_name")));
					FrontMatter fm = e.getFrontMatter();
					return e.copy().frontMatter(new FrontMatter(fm.title(),
							fm.categories(), new Tags(tags), fm.date(), fm.updated()))
							.build();
				}).orElse(null);
	}

	Map<EntryId, Tags> tagsMap(List<Long> ids) {
		MapSqlParameterSource source = new MapSqlParameterSource() //
				.addValue("entry_ids", ids);
		return this.jdbcTemplate.query(
				"SELECT entry_id, tag_name FROM entry_tag WHERE entry_id IN (:entry_ids)",
				source,
				(rs, i) -> Tuples.of(new EntryId(rs.getLong("entry_id")),
						new Tag(rs.getString("tag_name"))))
				.stream() //
				.collect(groupingBy(Tuple2::getT1)) //
				.entrySet() //
				.stream() //
				.map(e -> Tuples.of(e.getKey(), new Tags(e.getValue() //
						.stream() //
						.map(Tuple2::getT2) //
						.collect(toList()))))
				.collect(toMap(Tuple2::getT1, Tuple2::getT2));
	}

	List<Long> entryIds(SearchCriteria searchCriteria, Pageable pageable,
			ClauseAndParams clauseAndParams, MapSqlParameterSource source) {
		return this.jdbcTemplate.query(
				"SELECT e.entry_id FROM entry AS e " + searchCriteria.toJoinClause()
						+ " WHERE 1=1 " + clauseAndParams.clauseForEntryId()
						+ " ORDER BY e.last_modified_date DESC LIMIT "
						+ pageable.getPageSize() + " OFFSET " + pageable.getOffset(),
				source, (rs, i) -> rs.getLong("entry_id"));
	}

	String sqlForEntries(boolean excludeContent) {
		return "SELECT e.entry_id, e.title" + (excludeContent ? "" : ", e.content")
				+ ", e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
				+ " FROM entry AS e LEFT JOIN category AS c ON e.entry_id = c.entry_id "
				+ " WHERE e.entry_id IN (:entry_ids)"
				+ " ORDER BY e.last_modified_date DESC, c.category_order ASC";
	}

	@Override
	@NewSpan
	public List<Entry> findAll(SearchCriteria searchCriteria, Pageable pageable) {
		ClauseAndParams clauseAndParams = searchCriteria.toWhereClause();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValues(clauseAndParams.params());
		List<Long> ids = this.entryIds(searchCriteria, pageable, clauseAndParams, source);
		source.addValue("entry_ids", ids);
		boolean excludeContent = searchCriteria.isExcludeContent();
		Map<EntryId, Tags> tagsMap = this.tagsMap(ids);
		List<Entry> entries = this.jdbcTemplate.query(this.sqlForEntries(excludeContent),
				source, EntryExtractors.forEntries(excludeContent));
		return entries.stream() //
				.map(e -> {
					FrontMatter fm = e.getFrontMatter();
					EntryId entryId = e.getEntryId();
					Categories categories = fm.categories();
					Tags tags = tagsMap.get(entryId);
					return e.copy() //
							.frontMatter(new FrontMatter(fm.title(), categories, tags,
									fm.date(), fm.updated()))
							.build();
				}) //
				.collect(toList());
	}

	@Override
	@NewSpan
	public Flux<Entry> collectAll(SearchCriteria searchCriteria, Pageable pageable) {
		ClauseAndParams clauseAndParams = searchCriteria.toWhereClause();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValues(clauseAndParams.params());
		List<Long> ids = this.entryIds(searchCriteria, pageable, clauseAndParams, source);
		source.addValue("entry_ids", ids);
		boolean excludeContent = searchCriteria.isExcludeContent();
		Map<EntryId, Tags> tagsMap = this.tagsMap(ids);
		return Flux.create(sink -> {
			this.jdbcTemplate.query(this.sqlForEntries(excludeContent), source, rs -> {
				EntryExtractors.withEntries(rs, e -> {
					FrontMatter fm = e.getFrontMatter();
					EntryId entryId = e.getEntryId();
					Categories categories = fm.categories();
					Tags tags = tagsMap.get(entryId);
					Entry entry = e.copy() //
							.frontMatter(new FrontMatter(fm.title(), categories, tags,
									fm.date(), fm.updated()))
							.build();
					sink.next(entry);
				}, excludeContent);
			});
			sink.complete();
		});
	}

	@Override
	@NewSpan
	@Transactional
	public void save(Entry entry) {
		FrontMatter frontMatter = entry.frontMatter();
		Author created = entry.getCreated();
		Author updated = entry.getUpdated();
		MapSqlParameterSource source = new MapSqlParameterSource() //
				.addValue("entry_id", entry.entryId().getValue()) //
				.addValue("title", frontMatter.title().getValue()) //
				.addValue("content", entry.content().getValue()) //
				.addValue("created_by", created.getName().getValue()) //
				.addValue("created_date", created.getDate().getValue()) //
				.addValue("last_modified_by", updated.getName().getValue()) //
				.addValue("last_modified_date", updated.getDate().getValue()) //
		;
		this.jdbcTemplate.update(
				"INSERT INTO entry (entry_id, title, content, created_by, created_date, last_modified_by, last_modified_date)"
						+ " VALUES (:entry_id, :title, :content, :created_by, :created_date, :last_modified_by, :last_modified_date)"
						+ " ON DUPLICATE KEY UPDATE" //
						+ " title = :title," //
						+ " content = :content," //
						+ " created_by = :created_by," //
						+ " created_date = :created_date," //
						+ " last_modified_by = :last_modified_by," //
						+ " last_modified_date = :last_modified_date",
				source);

		AtomicInteger order = new AtomicInteger(0);
		SqlParameterSource[] categories = frontMatter.getCategories().getValue().stream()
				.map(category -> new MapSqlParameterSource() //
						.addValue("category_name", category.getValue()) //
						.addValue("category_order", order.getAndIncrement()) //
						.addValue("entry_id", entry.entryId().getValue()))
				.toArray(SqlParameterSource[]::new);
		this.jdbcTemplate.update("DELETE FROM category WHERE entry_id = :entry_id",
				source);
		this.jdbcTemplate.batchUpdate(
				"INSERT INTO category (category_name, category_order, entry_id) VALUES (:category_name, :category_order, :entry_id)",
				categories);

		SqlParameterSource[] tags = frontMatter.getTags().getValue().stream()
				.map(tag -> new MapSqlParameterSource() //
						.addValue("tag", tag.getValue()) //
						.addValue("entry_id", entry.entryId().getValue()))
				.toArray(SqlParameterSource[]::new);
		this.jdbcTemplate.update("DELETE FROM entry_tag WHERE entry_id = :entry_id",
				source);
		this.jdbcTemplate.batchUpdate(
				"INSERT INTO tag (tag_name) VALUES (:tag) ON DUPLICATE KEY UPDATE tag_name = :tag",
				tags);
		this.jdbcTemplate.batchUpdate(
				"INSERT INTO entry_tag (entry_id, tag_name) VALUES (:entry_id, :tag)",
				tags);
	}

	@Override
	@NewSpan
	@Transactional
	public int delete(@SpanTag("entryId") EntryId entryId) {
		MapSqlParameterSource source = new MapSqlParameterSource().addValue("entry_id",
				entryId.getValue());
		return this.jdbcTemplate.update("DELETE FROM entry WHERE entry_id = :entry_id",
				source);
	}

	@Override
	public EventTime findLatestModifiedDate() {
		return this.jdbcTemplate.queryForObject(
				"SELECT last_modified_date FROM entry ORDER BY last_modified_date DESC LIMIT 1",
				Collections.emptyMap(),
				(rs, i) -> new EventTime(OffsetDateTime.of(
						rs.getTimestamp("last_modified_date").toLocalDateTime(),
						zoneOffset)));
	}

	@Override
	public EventTime findLastModifiedDate(EntryId entryId) {
		MapSqlParameterSource source = new MapSqlParameterSource() //
				.addValue("entry_id", entryId.getValue());
		return this.jdbcTemplate.queryForObject(
				"SELECT last_modified_date FROM entry WHERE entry_id = :entry_id", source,
				(rs, i) -> new EventTime(OffsetDateTime.of(
						rs.getTimestamp("last_modified_date").toLocalDateTime(),
						zoneOffset)));
	}
}
