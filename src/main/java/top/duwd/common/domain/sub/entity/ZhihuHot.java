package top.duwd.common.domain.sub.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_zhihu_hot")
@AllArgsConstructor
@NoArgsConstructor
public class ZhihuHot implements Serializable {
    @Id
    private Integer id;

    private Integer qid;

    private Date createTime;

    private Date updateTime;

    private String questionType;

}