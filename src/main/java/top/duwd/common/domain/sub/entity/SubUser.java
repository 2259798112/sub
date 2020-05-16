package top.duwd.common.domain.sub.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
@Table(name = "t_sub_user")
@Data
public class SubUser implements Serializable {
    @Id
    private String userId;

    private Integer type;

    private String tel;

    private String weixin;

    private String password;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

}