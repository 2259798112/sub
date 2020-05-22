package top.duwd.common.domain.sub.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_keyword_user")
public class KeywordUser implements Serializable {
    @Id
    private Integer id;

    private String keywordMain;

    private String userId;

    private String plat;

    private String importList;

    private Date createTime;

    private Date updateTime;

}