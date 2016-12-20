package com.blueprint.blueprint.database.query;

/**
 * Creado Por jorgeacostaalvarado el 09-09-15.
 */
public class BPSqlSelectQuery extends BPSqlQuery {

    private String orderBy = "";

    public BPSqlSelectQuery(String tableName) {
        super(tableName);
    }

    public BPSqlSelectQuery AscendingOrderBy(String orderBy){
        this.orderBy = orderBy + " ASC";
        return this;
    }

    public BPSqlSelectQuery DescendingOrderBy(String orderBy){
        this.orderBy = orderBy + " DESC";
        return this;
    }

    public BPSqlSelectQuery innerJoin(String tableName, String onField){
        return this;
    }


}
