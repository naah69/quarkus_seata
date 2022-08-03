package io.quarkiverse.seata.provider.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "stock_tbl")
@DynamicUpdate
@DynamicInsert
public class Stock extends PanacheEntity {

    public String commodityCode;
    public Integer count;

}
