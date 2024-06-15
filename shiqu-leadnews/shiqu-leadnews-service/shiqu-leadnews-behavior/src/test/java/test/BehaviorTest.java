package test;

import com.shiqu.behavior.BehaviorApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest(classes = BehaviorApplication.class)
@RunWith(SpringRunner.class)
public class BehaviorTest {
    @Autowired
    private DataSource dataSource;

    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;

    @Test
    public void test1() throws SQLException {
        System.out.println(mapperLocations);
    }
}
