package top.duwd.common.domain.sub.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "t_user_session")
@Data
public class UserSession implements Serializable {
    @Id
    private String token;

    private String uid;

    private Integer plat;

    private Date createTime;

    private Date updateTime;

}