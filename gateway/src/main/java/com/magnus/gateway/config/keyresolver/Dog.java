package com.magnus.gateway.config.keyresolver;

public class Dog implements Animal{
    public String name;

    @Override
    public void live() {

    }

    public void setName(String name) {
        this.name = name;
    }
}
