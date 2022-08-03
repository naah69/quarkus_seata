package io.quarkiverse.seata.provider.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "account_tbl")
@DynamicUpdate
@DynamicInsert
public class Account extends PanacheEntity {
    public String userId;
    public BigDecimal money;
}
