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
@Table(name = "t_baidu_zhidao_basic")
public class BaiduZhidaoBasic implements Serializable {
    @Id
    private Integer id;

    private Long qid;

    private String title;

    private String tags;

    private Integer pv;

    private Date created;

    private Integer pvDayRate;

    private Integer answerCount;

    private Date createTime;

    private Date updateTime;

    private Integer count;

}