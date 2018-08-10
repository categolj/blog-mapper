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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({ "classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql" })
@EnableAutoConfiguration
@ComponentScan(basePackages = "am.ik.blog.entry.jdbc")
public class CategoryMapperTest {
	@Autowired
	CategoryMapper categoryMapper;

	@Test
	public void findAllConcatenatedCategory() throws Exception {
		List<String> categories = categoryMapper.findAllConcatenatedCategory();
		assertThat(categories).hasSize(3);
		assertThat(categories).containsExactly("a,b,c", "x,y", "x,y,z");
	}

	@Test
	public void findConcatenatedCategoryLikeCategoryName() throws Exception {
		List<String> categories = categoryMapper
				.findConcatenatedCategoryLikeCategoryName("x,y");
		assertThat(categories).hasSize(2);
		assertThat(categories).containsExactly("x,y", "x,y,z");
	}

	@Test
	public void findAll() throws Exception {
		List<Categories> categories = categoryMapper.findAll();
		assertThat(categories).hasSize(3);
		assertThat(categories).containsExactly(
				new Categories(
						asList(new Category("a"), new Category("b"), new Category("c"))),
				new Categories(asList(new Category("x"), new Category("y"))),
				new Categories(
						asList(new Category("x"), new Category("y"), new Category("z"))));
	}

	@Test
	public void findLikeCategoryName() throws Exception {
		List<Categories> categories = categoryMapper.findLikeCategoryName("x,y");
		assertThat(categories).hasSize(2);
		assertThat(categories).containsExactly(
				new Categories(asList(new Category("x"), new Category("y"))),
				new Categories(
						asList(new Category("x"), new Category("y"), new Category("z"))));
	}

	@Configuration
	static class Config {
	}
}