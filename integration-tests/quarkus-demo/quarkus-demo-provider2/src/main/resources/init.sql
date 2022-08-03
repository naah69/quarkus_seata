-- DROP SCHEMA IF EXISTS db_stock;
-- CREATE SCHEMA db_stock;
-- USE db_stock;

CREATE TABLE "undo_log"
(
    "id"            BIGSERIAL    NOT NULL primary key,
    "branch_id"     int8         NOT NULL,
    "xid"           varchar(100) NOT NULL,
    "context"       varchar(128) NOT NULL,
    "rollback_info" bytea        NOT NULL,
    "log_status"    int8         NOT NULL,
    "log_created"   timestamp    NOT NULL,
    "log_modified"  timestamp    NOT NULL
);

CREATE UNIQUE INDEX "ux_undo_log" ON "public"."undo_log" (
                                                          "xid",
                                                          "branch_id"
    );

INSERT INTO stock_tbl (id, commodity_code, count)
VALUES (1, '2001', 1000);