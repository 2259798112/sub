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
@Table(name = "t_keyword")
public class Keyword implements Serializable {
    @Id
    private Integer id;

    private String keywordMain;

    private String keywordTail;

    private String plat;

    private Date createTime;

    private Date updateTime;

    private Integer counter;

    private Integer counterM;
}