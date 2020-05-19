package top.duwd.common.domain.sub.entity;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_keyword")
public class Keyword implements Serializable {
    private Integer id;
    private Integer counter;
    private Integer counterM;

    private String keywordMain;

    private String keywordTail;

    private String plat;

    private Date createTime;

    private Date updateTime;
}