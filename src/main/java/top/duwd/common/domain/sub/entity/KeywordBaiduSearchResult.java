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
@Table(name = "t_keyword_baidu_search_result")
public class KeywordBaiduSearchResult implements Serializable {
    @Id
    private Integer id;

    private String keyword;

    private String title;

    private String urlSource;

    private String urlReal;

    private Date createTime;

    private Date updateTime;

    private String baiduType;

    private Integer baiduPage;

    private Integer resultOrder;

    private String targetSite;
    private String rs;

}