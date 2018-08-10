package am.ik.blog.entry;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({ "classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql" })
@EnableAutoConfiguration
@ComponentScan(basePackages = "am.ik.blog.entry.jdbc")
public class TagMapperTest {
	@Autowired
	TagMapper tagMapper;

	@Test
	public void findTagStringOrderByTagNameAsc() throws Exception {
		List<String> tags = tagMapper.findTagStringOrderByTagNameAsc();
		assertThat(tags).containsExactly("test1", "test2", "test3");
	}

	@Test
	public void findOrderByTagNameAsc() throws Exception {
		List<Tag> tags = tagMapper.findOrderByTagNameAsc();
		assertThat(tags).containsExactly(new Tag("test1"), new Tag("test2"),
				new Tag("test3"));
	}

	@Configuration
	static class Config {
	}
}