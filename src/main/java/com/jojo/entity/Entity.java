package com.jojo.entity;

import com.jojo.utils.ReflectionUtils;

public class Entity {
    @Override
    public String toString() {
        return ReflectionUtils.toString(this);
    }
}
