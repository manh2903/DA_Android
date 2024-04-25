package com.ndm.da_test.Entities;

import java.io.Serializable;
import java.util.List;

public class Skill implements Serializable {
   private String name;

   private String source;

    public Skill(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public Skill() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
