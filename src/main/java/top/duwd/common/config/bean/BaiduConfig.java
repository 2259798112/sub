package top.duwd.common.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.http.html.Baidu;

@Configuration
public class BaiduConfig {
    @Bean
    public Baidu baidu() {
        return new Baidu();
    }
}
