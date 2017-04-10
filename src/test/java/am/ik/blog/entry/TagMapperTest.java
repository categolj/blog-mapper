package am.ik.blog.entry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({ "classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql" })
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