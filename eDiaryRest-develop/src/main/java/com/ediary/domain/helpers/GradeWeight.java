package com.ediary.domain.helpers;

public enum GradeWeight {
    FINAL_GRADE(100),
    BEHAVIOR_GRADE(200);

    private final Integer weight;

    GradeWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }
}
