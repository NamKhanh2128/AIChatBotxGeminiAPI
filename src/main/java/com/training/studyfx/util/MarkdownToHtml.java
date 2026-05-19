package com.training.studyfx.util;

public class MarkdownToHtml {

    public static String convert(String md) {
        if (md == null)
            return "";
        return md.replace("**", "<b>")
                .replace("*", "<i>")
                .replace("\n", "<br>");
    }
}