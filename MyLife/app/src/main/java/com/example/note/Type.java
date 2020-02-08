package com.example.note;

public class Type {

    public enum MainType {
        SH("生活"),
        GZ("工作"),
        RYX("日语学习"),
        ITX("It学习"),
        RQSG("人情世故"),
        ZR("做人");

        private String type;

        MainType(String type) {
            this.type = type;
        }

        @Override public String toString() {
            return type;
        }
    }

    public enum SubJapaneseType {
        YF("语法"),
        CH("词汇"),
        TL("听力"),
        KY("口语"),
        YD("阅读"),
        SHCJ("生活场景");

        private String type;

        SubJapaneseType(String type) {
            this.type = type;
        }

        @Override public String toString() {
            return type;
        }
    }

}
