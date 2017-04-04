package am.ik.blog.entry;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class Asserts {
	static void assertEntry99999(Entry entry, boolean excludeContent) {
		assertThat(entry).isNotNull();
		assertThat(entry.entryId).isEqualTo(new EntryId(99999L));
		assertThat(entry.frontMatter.title).isEqualTo(new Title("Hello World!!"));
		if (excludeContent) {
			assertThat(entry.content).isEqualTo(new Content(""));
		}
		else {
			assertThat(entry.content).isEqualTo(new Content("This is a test data."));
		}
		assertThat(entry.frontMatter.date).isEqualTo(new EventTime(
				OffsetDateTime.of(2017, 4, 1, 1, 0, 0, 0, ZoneOffset.ofHours(9))));
		assertThat(entry.frontMatter.updated).isEqualTo(new EventTime(
				OffsetDateTime.of(2017, 4, 1, 2, 0, 0, 0, ZoneOffset.ofHours(9))));
		assertThat(entry.frontMatter.tags.collect(toList()))
				.containsExactly(new Tag("test1"), new Tag("test2"), new Tag("test3"));
		assertThat(entry.frontMatter.categories.collect(toList()))
				.containsExactly(new Category("x"), new Category("y"), new Category("z"));
	}

	static void assertEntry99998(Entry entry, boolean excludeContent) {
		assertThat(entry).isNotNull();
		assertThat(entry.entryId).isEqualTo(new EntryId(99998L));
		assertThat(entry.frontMatter.title).isEqualTo(new Title("Test!!"));
		if (excludeContent) {
			assertThat(entry.content).isEqualTo(new Content(""));
		}
		else {
			assertThat(entry.content).isEqualTo(new Content("This is a test data."));
		}
		assertThat(entry.frontMatter.date).isEqualTo(new EventTime(
				OffsetDateTime.of(2017, 4, 1, 0, 0, 0, 0, ZoneOffset.ofHours(9))));
		assertThat(entry.frontMatter.updated).isEqualTo(new EventTime(
				OffsetDateTime.of(2017, 4, 1, 0, 0, 0, 0, ZoneOffset.ofHours(9))));
		assertThat(entry.frontMatter.tags.collect(toList()))
				.containsExactly(new Tag("test1"), new Tag("test2"));
		assertThat(entry.frontMatter.categories.collect(toList()))
				.containsExactly(new Category("a"), new Category("b"), new Category("c"));
	}

	static void assertEntry99997(Entry entry, boolean excludeContent) {
		assertThat(entry).isNotNull();
		assertThat(entry.entryId).isEqualTo(new EntryId(99997L));
		assertThat(entry.frontMatter.title).isEqualTo(new Title("CategoLJ 4"));
		if (excludeContent) {
			assertThat(entry.content).isEqualTo(new Content(""));
		}
		else {
			assertThat(entry.content).isEqualTo(new Content("This is a test data."));
		}
		assertThat(entry.frontMatter.date).isEqualTo(new EventTime(
				OffsetDateTime.of(2017, 3, 31, 0, 0, 0, 0, ZoneOffset.ofHours(9))));
		assertThat(entry.frontMatter.updated).isEqualTo(new EventTime(
				OffsetDateTime.of(2017, 3, 31, 0, 0, 0, 0, ZoneOffset.ofHours(9))));
		assertThat(entry.frontMatter.tags.collect(toList()))
				.containsExactly(new Tag("test1"), new Tag("test3"));
		assertThat(entry.frontMatter.categories.collect(toList()))
				.containsExactly(new Category("x"), new Category("y"));
	}
}
