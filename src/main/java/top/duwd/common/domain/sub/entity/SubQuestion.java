package top.duwd.common.domain.sub.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "t_sub_question")
@Data
public class SubQuestion implements Serializable {
    @Id
    private Integer id;

    private String userId;

    private Integer questionId;

    private String questionTitle;

    private Integer valid;
//    @JSONField(format = "yyyy年MM月dd日")
    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

}