package top.duwd.common.domain.sub.entity;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
@Table(name = "t_sub_user")
public class SubUser implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_user.user_id
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private String userId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_user.type
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private Integer type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_user.tel
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private String tel;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_user.weixin
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private String weixin;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_user.password
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_user.create_time
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_user.update_time
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_sub_user
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_user.user_id
     *
     * @return the value of t_sub_user.user_id
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public String getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_user.user_id
     *
     * @param userId the value for t_sub_user.user_id
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_user.type
     *
     * @return the value of t_sub_user.type
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_user.type
     *
     * @param type the value for t_sub_user.type
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_user.tel
     *
     * @return the value of t_sub_user.tel
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public String getTel() {
        return tel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_user.tel
     *
     * @param tel the value for t_sub_user.tel
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_user.weixin
     *
     * @return the value of t_sub_user.weixin
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public String getWeixin() {
        return weixin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_user.weixin
     *
     * @param weixin the value for t_sub_user.weixin
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_user.password
     *
     * @return the value of t_sub_user.password
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_user.password
     *
     * @param password the value for t_sub_user.password
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_user.create_time
     *
     * @return the value of t_sub_user.create_time
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_user.create_time
     *
     * @param createTime the value for t_sub_user.create_time
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_user.update_time
     *
     * @return the value of t_sub_user.update_time
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_user.update_time
     *
     * @param updateTime the value for t_sub_user.update_time
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_sub_user
     *
     * @mbggenerated Tue May 05 11:48:31 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", type=").append(type);
        sb.append(", tel=").append(tel);
        sb.append(", weixin=").append(weixin);
        sb.append(", password=").append(password);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}