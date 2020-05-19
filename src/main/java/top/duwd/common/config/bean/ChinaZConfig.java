package top.duwd.common.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.http.html.ChinaZ;

@Configuration
public class ChinaZConfig {
    @Bean
    public ChinaZ chinaZ(){
        return new ChinaZ();
    }
}
