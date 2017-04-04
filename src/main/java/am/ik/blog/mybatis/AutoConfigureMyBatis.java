package am.ik.blog.mybatis;

import java.lang.annotation.*;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportAutoConfiguration(classes = MybatisAutoConfiguration.class)
public @interface AutoConfigureMyBatis {
}
