package com.crunchiest.util;

public class StringUtil {

    public static String removeUnderscores(String string){
      string = string.replace("_", " ");
      return string;
    }
}
