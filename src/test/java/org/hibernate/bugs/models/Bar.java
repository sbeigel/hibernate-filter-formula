package org.hibernate.bugs.models;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@FilterDef(name="fooFilter", parameters=@ParamDef(name="id", type="integer"), defaultCondition = "foo_id = :id")
public class Bar {
    @Id
    private Integer id;
    
    @Formula("(select f.name from foo f where f.id = :fooFilter.id)")
    private String fooName;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getFooName() {
        return fooName;
    }
}
