package kr.co.motive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.sql.DataSource;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration," +
        "org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration"
})
class MotiveServerApplicationTests {

    @MockitoBean
    DataSource dataSource;

    @MockitoBean
    RedisConnectionFactory redisConnectionFactory;

    @Test
    void contextLoads() {
    }
}
