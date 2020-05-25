package top.duwd.common.domain.zhihu;


import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

@Data
public class ZhihuQuestionEntity {
    @Id
    private Integer id;// int(11) NOT NULL,
    private String title;// varchar(255) NOT NULL,
    private String questionType;// varchar(50) NOT NULL,
    private Date created;// datetime DEFAULT NULL,
    private Date updatedTime;// datetime DEFAULT NULL,
    private String url;// varchar(255) DEFAULT NULL,
    private Integer answerCount;// int(11) DEFAULT NULL,
    private Integer visitCount;// int(11) DEFAULT NULL,
    private Integer commentCount;// int(11) DEFAULT NULL,
    private Integer followerCount;// int(11) DEFAULT NULL,
    private Integer collapsedAnswerCount;// int(11) DEFAULT NULL,
    private String authorId;// varchar(100) DEFAULT NULL,
    private String authorName;// varchar(100) DEFAULT NULL,
    private String authorAvatarUrl;// varchar(100) DEFAULT NULL,
    private String authorUrlToken;// varchar(100) DEFAULT NULL,
    private Boolean authorIsOrg;// tinyint(1) DEFAULT NULL,
    private String authorType;// varchar(50) DEFAULT NULL,
    private String authorUserType;// varchar(50) DEFAULT NULL,
    private String authorHeadline;// varchar(50) DEFAULT NULL,
    private Integer authorGender;// int(11) DEFAULT NULL,
    private Boolean authorIsAdvertiser;// tinyint(1) DEFAULT NULL,
    private Boolean authorIsPrivacy;// tinyint(1) DEFAULT NULL,
    private Integer voteupCount;// int(11) DEFAULT NULL,
    private Date createTime;// int(11) DEFAULT NULL,
}
