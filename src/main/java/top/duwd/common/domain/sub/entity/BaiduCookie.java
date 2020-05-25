package top.duwd.common.domain.sub.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_baidu_cookie")
public class BaiduCookie implements Serializable {
    @Id
    private Integer id;

    private Date createTime;

    private String cookie;

    private String plat;
}