package com.xiaoshuai.handsomeweather.gson;

public class Suggestion {

    /*舒适度指数*/
    public Comf comf;
    /*运动指数*/
    public Sport sport;
    /*洗车指数*/
    public CW cw;

    public class Comf {
        /*生活指数类型*/
        public String type;
        /*简介*/
        public String brf;
        /*详情*/
        public String txt;
    }
    public class Sport {
        /*生活指数类型*/
        public String type;
        /*简介*/
        public String brf;
        /*详情*/
        public String txt;
    }
    public class CW {
        /*生活指数类型*/
        public String type;
        /*简介*/
        public String brf;
        /*详情*/
        public String txt;
    }

}
