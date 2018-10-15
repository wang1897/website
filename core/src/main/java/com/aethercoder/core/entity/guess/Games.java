package com.aethercoder.core.entity.guess;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Entity
@Table(name = "games")
@ApiModel(value="游戏列表配置",description="游戏列表配置")
public class Games extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Column(name = "zh_name")
    private String zhName;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "ko_name")
    private String koName;

    @Column(name = "method")
    private String method;

    @Column(name = "zh_url")
    private String zhUrl;

    @Column(name = "en_url")
    private String enUrl;

    @Column(name = "ko_url")
    private String koUrl;

    @Column(name = "is_show")
    private Boolean isShow = false;

    @Column(name = "banner")
    private String banner;

    @Column(name = "unit")
    private Long unit;

    @Transient
    private String url;

    @Transient
    private String name;

    @Transient
    private Boolean flag;

    @Transient
    private String bannerUrlEn;

    @Transient
    private String bannerUrlKo;

    @CreationTimestamp
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @ApiModelProperty(position = 1, value="游戏名称")
    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getKoName() {
        return koName;
    }

    public void setKoName(String koName) {
        this.koName = koName;
    }
    @ApiModelProperty(position = 2, value="访问method")
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @ApiModelProperty(position = 2, value="中文URL")
    public String getZhUrl() {
        return zhUrl;
    }

    public void setZhUrl(String zhUrl) {
        this.zhUrl = zhUrl;
    }

    @ApiModelProperty(position = 3, value="英文URL")
    public String getEnUrl() {
        return enUrl;
    }

    public void setEnUrl(String enUrl) {
        this.enUrl = enUrl;
    }

    @ApiModelProperty(position = 4, value="韩文URL")
    public String getKoUrl() {
        return koUrl;
    }

    public void setKoUrl(String koUrl) {
        this.koUrl = koUrl;
    }

    @ApiModelProperty(position = 5, value="是否显示 0：不显示／1：显示")
    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    @ApiModelProperty(position = 6, value="游戏banner图")
    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonIgnore(value = false)
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    @JsonIgnore(value = false)
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public String getBannerUrlEn() {
        return bannerUrlEn;
    }

    public void setBannerUrlEn(String bannerUrlEn) {
        this.bannerUrlEn = bannerUrlEn;
    }

    public String getBannerUrlKo() {
        return bannerUrlKo;
    }

    public void setBannerUrlKo(String bannerUrlKo) {
        this.bannerUrlKo = bannerUrlKo;
    }

    @Override
    public String toString() {
        return "Games{" +
                "zhName='" + zhName + '\'' +
                ", enName='" + enName + '\'' +
                ", koName='" + koName + '\'' +
                ", method='" + method + '\'' +
                ", zhUrl='" + zhUrl + '\'' +
                ", enUrl='" + enUrl + '\'' +
                ", koUrl='" + koUrl + '\'' +
                ", isShow=" + isShow +
                ", banner='" + banner + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
