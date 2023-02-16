package io.quarkiverse.seata.provider.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * UndoLog
 *
 * @author nayan
 * @date 2023/2/16 20:02
 */

@Entity
@Table(name = "undo_log", indexes = {@Index(name = "ux_undo_log", columnList = "xid,branchId", unique = true)})
public class UndoLog extends PanacheEntity {

    @Column(nullable = false)
    public Long branchId;

    @Column(length = 100, nullable = false)
    public String xid;

    @Column(length = 128, nullable = false)
    public String context;

    @Column(nullable = false)
    public Byte[] rollbackInfo;

    @Column(nullable = false)
    public Long logStatus;

    @Column(nullable = false)
    public LocalDateTime logCreated;

    @Column(nullable = false)
    public LocalDateTime logModified;

}
