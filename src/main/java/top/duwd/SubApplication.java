package top.duwd;

import com.cxytiandi.elasticjob.annotation.EnableElasticJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableScheduling
@MapperScan({"top.duwd.common.mapper"})
@EnableAsync
@EnableElasticJob
public class SubApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubApplication.class, args);
    }

}
