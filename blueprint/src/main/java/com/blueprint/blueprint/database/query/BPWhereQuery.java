package com.blueprint.blueprint.database.query;

import android.util.Log;

import java.util.ArrayList;

/**
 * Creado Por jorgeacostaalvarado el 09-09-15.
 */
public class BPWhereQuery {

    public enum Type {
        AND,
        OR,
        EQUAL,
        GT,
        LT,
        LIKE
    }

    private ArrayList<PairValue> list = new ArrayList<>();
    //notEqualTo
    public void notEqualTo(String field, int value){

    }

    public void notEqualTo(String field, String value){

    }

    public void notEqualTo(String field, double value){

    }

    public void notEqualTo(String filed, boolean value){

    }
    //equalTo
    public BPWhereQuery equalTo(String field, int value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.EQUAL;

        list.add(pair);
        return this;
    }


    public BPWhereQuery likeTo(String field, String value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.LIKE;

        list.add(pair);

        return this;
    }

    public BPWhereQuery equalTo(String field, String value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.EQUAL;

        list.add(pair);

        return this;
    }

    public void equalTo(String field, double value){

    }

    public BPWhereQuery equalTo(String field, boolean value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.EQUAL;

        list.add(pair);
        return this;
    }
    //greatherThan
    public BPWhereQuery greatherThan(String field, int value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.GT;

        list.add(pair);

        return this;
    }

    public BPWhereQuery greatherThan(String field, String value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.GT;

        list.add(pair);

        return this;
    }

    public BPWhereQuery greatherThan(String field, double value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.GT;

        list.add(pair);

        return this;
    }

    public void greatherThan(String field, long value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.GT;

        list.add(pair);
    }

    public void greatherThan(String filed, boolean value){

    }
    //lessThan
    public BPWhereQuery lessThan(String field, int value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.LT;

        list.add(pair);

        return this;
    }

    public BPWhereQuery lessThan(String field, String value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.LT;

        list.add(pair);
        return this;
    }

    public void lessThan(String field, double value){

    }

    public BPWhereQuery lessThan(String field, long value){
        PairValue pair = new PairValue();
        pair.field = field;
        pair.value = value;
        pair.type = Type.LT;

        list.add(pair);

        return this;
    }

    public void lessThan(String filed, boolean value){

    }
    public void or(){

    }

    public void and(){

    }

    public ArrayList<String> getRawString(){

        ArrayList<String> result = new ArrayList<>();

        for (int i = 0 ; i < list.size() ; i++){
            PairValue pairValue = list.get(i);

            String sign = "";

            switch (pairValue.type){
                case EQUAL:
                    sign = "=";
                    break;
                case LT:
                    sign = "<";
                    break;

                case GT:
                    sign = ">";
                    break;

                case LIKE:
                    sign = "LIKE";
                    break;
            }


            if(i + 1 < list.size()) {
                PairValue nextPairValue = list.get(i + 1);

                if (nextPairValue != null) {

                    if (nextPairValue.type == Type.LIKE || nextPairValue.type == Type.EQUAL || nextPairValue.type == Type.LT || nextPairValue.type == Type.GT) {

                        if(pairValue.value instanceof Integer || pairValue.value instanceof Long){
                            result.add(pairValue.field + " "+sign+" " + String.valueOf(pairValue.value) + " AND ");
                        } else{
                            result.add(pairValue.field + " "+ sign + " '" + String.valueOf(pairValue.value) + "' AND ");
                        }

                    }else{
                        if(pairValue.value instanceof Integer || pairValue.value instanceof Long) {
                            result.add(pairValue.field + " "+sign+" " + String.valueOf(pairValue.value));
                        } else {
                            result.add(pairValue.field + " "+sign+" '" + String.valueOf(pairValue.value) + "'");
                        }
                    }

                }

            }else{
                if(pairValue.value instanceof Integer || pairValue.value instanceof Long) {
                    result.add(pairValue.field + " "+sign+" " + String.valueOf(pairValue.value));
                }else{
                    result.add(pairValue.field + " "+sign+" '" + String.valueOf(pairValue.value) + "'");
                }
            }


        }

        String where = "";
        for(int j = 0; j < result.size();j++){
            where += result.get(j);
        }

        Log.d("dynamo"," where >> " + where);

        return result;
    }

    private class PairValue {
        public String field;
        public Object value;
        public BPWhereQuery.Type type;
    }

    /*

      Some Examples:

      BPWhereQuery whereQuery = new BPWhereQuery();
      whereQuery.equalTo("firstName","Jorge")
                .or()
                .equalTo("lastName","Acosta");      // WHERE firstName = 'Jorge' OR lastName = 'Acosta'

      whereQuery.equalTo("firstName","Jorge")
                .equalTo("lastName","Acosta")
                .or()
                .equalTo("id",1);                 // WHERE (firstName = 'Jorge' AND lastName = 'Acosta') OR id = 1

      whereQuery.equalTo("firstName","Jorge")
                .or()
                .equalTo("lastName","Acosta")
                .and()
                .equalTo("id",1);                 // WHERE (firstName = 'Jorge' OR lastName = 'Acosta') AND id = 1

        whereQuery.notEqualTo("firstName","Jorge")
                .or()
                .equalTo("lastName","Acosta")
                .and()
                .equalTo("id",1);                 // WHERE (firstName != 'Jorge' OR lastName = 'Acosta') AND id = 1

     */
}
