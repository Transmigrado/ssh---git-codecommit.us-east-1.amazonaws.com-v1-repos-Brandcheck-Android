package com.blueprint.blueprint.database.query;


/**
 * Creado Por jorgeacostaalvarado el 09-09-15.
 */
public class BPSqlQuery {

    private String tableName = null;
    private BPWhereQuery whereQuery;
    private String orderBy= "";
    private String groupBy = "";
    private int limit = -1;
    private int skip = -1;

    public BPSqlQuery(String tableName){
        this.tableName = tableName;
        whereQuery = new BPWhereQuery();
    }

    public String getTableName(){
        return tableName;
    }

    public BPWhereQuery where(){
        return whereQuery;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy){
        this.orderBy = orderBy;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }


}
