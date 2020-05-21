package top.duwd.common.domain.sub.entity;

import java.io.Serializable;
import java.util.Date;

public class KeywordUser implements Serializable {
    private Integer id;

    private String keywordMain;

    private String userId;

    private String plat;

    private String importList;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeywordMain() {
        return keywordMain;
    }

    public void setKeywordMain(String keywordMain) {
        this.keywordMain = keywordMain;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getImportList() {
        return importList;
    }

    public void setImportList(String importList) {
        this.importList = importList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", keywordMain=").append(keywordMain);
        sb.append(", userId=").append(userId);
        sb.append(", plat=").append(plat);
        sb.append(", importList=").append(importList);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}