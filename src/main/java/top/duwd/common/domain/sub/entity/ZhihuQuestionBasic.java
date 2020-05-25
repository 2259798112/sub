package top.duwd.common.domain.sub.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_zhihu_question_basic")
public class ZhihuQuestionBasic {
    @Id
    private Integer id;

    private Integer qid;

    private String title;

    private String questionType;

    private Date created;

    private Date updatedTime;

    private String url;

    private Integer answerCount;

    private Integer visitCount;

    private Integer commentCount;

    private Integer followerCount;

    private Integer collapsedAnswerCount;

    private String authorId;

    private String authorName;

    private String authorAvatarUrl;

    private String authorUrlToken;

    private Boolean authorIsOrg;

    private String authorType;

    private String authorUserType;

    private String authorHeadline;

    private Integer authorGender;

    private Boolean authorIsAdvertiser;

    private Boolean authorIsPrivacy;

    private Integer voteupCount;

    private Date createTime;

    private Date updateTime;

    private Integer visitAnswerRate;

    private Integer visitFollowRate;

    private Integer followAnswerRate;

    private Integer visitDayRate;

}