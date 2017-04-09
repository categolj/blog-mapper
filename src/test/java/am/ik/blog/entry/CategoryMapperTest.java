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

	@Configuration
	static class Config {
	}
}