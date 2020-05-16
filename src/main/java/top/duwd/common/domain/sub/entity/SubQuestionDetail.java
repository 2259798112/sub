package top.duwd.common.domain.sub.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "t_sub_question_detail")
@Data
public class SubQuestionDetail implements Serializable {
    @Id
    private Integer id;
    private Integer questionId;
    private String title;
    private String questionType;
    private Integer answerCount;
    private Integer followerCount;
    private Integer visitCount;
    private String authorId;
    private String authorName;
    private Integer voteupCount;
    private Integer commentCount;
    private Integer collapsedAnswerCount;
    private Date created;
    private Date updatedTime;
    private Date createTime;
    private Date updateTime;
    private String snapTime;


    private static final long serialVersionUID = 1L;

}