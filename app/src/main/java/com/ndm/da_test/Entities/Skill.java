package com.ndm.da_test.Entities;

import java.io.Serializable;

public class Skill implements Serializable {
   private String name;

    public Skill(String name) {
        this.name = name;
    }

    public Skill() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
