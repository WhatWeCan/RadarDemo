package com.tjstudy.radardemo.widget;

/**
 * 雷达图实体封装
 * Created by tjstudy on 2017/3/7.
 */

public class Radar {
    //名字  数值
    private String name;
    private float value;

    public Radar(String name, float value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Radar{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
