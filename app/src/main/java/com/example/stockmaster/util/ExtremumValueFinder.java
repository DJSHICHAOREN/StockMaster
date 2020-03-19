package com.example.stockmaster.util;

/**
 * 判断是否为极值，且为极大值还是极小值
 */
public class ExtremumValueFinder {
    public enum State{NONE, EXMAX, EXMIN}
    public ExtremumValueFinder(){

    }

    /**
     * 判断第二个数在这三个数中是极大值还是极小值，还是都不是
     * @param first
     * @param second
     * @param third
     * @return NONE：都不是，EXMAX：极大值，EXMIN：极小值
     */
    public State judge(float first, float second, float third){
        if(second > first && second > third){
            return State.EXMAX;
        }
        if(second < first && second < third){
            return State.EXMIN;
        }
        return State.NONE;
    }
}
