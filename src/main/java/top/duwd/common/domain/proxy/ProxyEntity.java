package top.duwd.common.domain.proxy;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_proxy")
public class ProxyEntity {
    private Integer id;
    private String ip;
    private String port;
    private String plat;
    private Date createTime;
}
