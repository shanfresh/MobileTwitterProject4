/**
 * 
 */
package com.iie.twitter.analyse;

/**
 * @author Gingber
 *
 */

import java.lang.Character.UnicodeBlock;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseRecognizer {
	
	public static void main(String[] args) {
		String str = "[A양]한성주 3차 풀영상(47분짜리) 많은분들이 찾으시... RT @kukminilbo [北 김정은 시대] 동급생이 공개하는 김정은 학창 시절 “영어못하는 2류 학생… 농구·게임에만 몰두” #국민일보쿠키뉴스_ http://mku.ki/tdKUG7 ";
		str = str.replaceAll("\\pP", "");
		System.out.println(str);
		ChineseRecognizer tt=new ChineseRecognizer();
		if (tt.isContainChinese(str)) {
			System.out.println("1:a Chinese character");
		}
		String mixed = "ココ魔女! - Yahoo!モバゲー #ymbga http://yahoo-mbga.jp/game/12002819?_ref=aff%3Dypt002 …,Twitter / ツイートボタン http://twitter.com/goodies/tweetbutton … via @tereponisp";
		mixed = mixed.replaceAll("\\pP", "");
		if(tt.isContainJapanese(mixed)) {
			System.out.println("2:a Japanese character");
		}
	}

    public boolean isContainChinese(String str) {
    	str=str.replaceAll("\\pP", "");
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
    

    public boolean isContainJapanese(String str) {
    	str=str.replaceAll("\\pP", "");
        //Pattern p = Pattern.compile("[\u3040-\u309F]");
    	
    	Pattern p = Pattern.compile("[\u3040-\u309f\u30a0-\u30ff]");
    	Matcher m = p.matcher(str);
        if (m.find()) {
        	//System.out.println(m.group()+"**");
            return true;
        }
        return false;
    	
    	
    }
 
}
