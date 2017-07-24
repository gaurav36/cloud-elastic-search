package net.elastic.spring.recipe;


/**
 * Created by Gaurav Garg on 12/07/17.
 */

public class ElasticSearchRequest {
    String index;
    String type;
    String id; 

    public String getIndex() {
        return index;
    }   

    public void setIndex(String index) {
        this.index = index;
    }   

    public String getType() {
        return type;
    }   

    public void setType(String type) {
        this.type = type;
    }   

    public String getId() {
        return id; 
    }   

    public void setId(String id) {
        this.id = id; 
    }   
}
