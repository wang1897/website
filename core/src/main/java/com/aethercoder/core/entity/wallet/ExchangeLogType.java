package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/9
 * @modified By:
 */
@Entity
@Table(name = "t_exchange_type")
public class ExchangeLogType extends BaseEntity2 {

    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "icon")
    private String icon;

    @Transient
    private String koName;

    @Transient
    private String enName;

    public String getKoName() {
        return koName;
    }

    public void setKoName(String koName) {
        this.koName = koName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
