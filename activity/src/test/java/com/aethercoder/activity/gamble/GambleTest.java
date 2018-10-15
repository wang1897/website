
package com.aethercoder.activity.gamble;

import com.aethercoder.TestApplication;
import com.aethercoder.activity.service.GuessGambleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * @auther Guo Feiyan
 * @date 2018/3/15 上午11:39
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class GambleTest {

    @Autowired
    private GuessGambleService guessGambleService;

    @Test
    public void test()  {
//        Map map = guessGambleService.getJoinGambleRank(8L);
//        System.out.println("en:"+guessGambleService.getGambleGameShareUrl(10l,"en"));
//        System.out.println("zh:"+ guessGambleService.getGambleGameShareUrl(10l,"zh"));
//        System.out.println("ko:"+guessGambleService.getGambleGameShareUrl(10l,"ko"));
//        System.out.println("ja:"+guessGambleService.getGambleGameShareUrl(10l,"ja"));

        try {
//
//            以下8个特殊字符URL编码没有对其进行转码	十六进制值
//            1.	+	URL 中+号表示空格	%2B
//            2.	空格	URL中的空格可以用+号或者编码	%20
//            3.	/	分隔目录和子目录	%2F
//            4.	?	分隔实际的 URL 和参数	%3F
//            5.	%	指定特殊字符	%25
//            6.	#	表示书签	%23
//            7.	&	URL 中指定的参数间的分隔符	%26
//            8.	=	URL 中指定参数的值	%3D

            String url = "= + / ? % # &";
            url =  url.replaceAll("\\%","%25");
            System.out.println( url);
            url =  url.replaceAll("\\+","%2B");
            System.out.println( url);
            url =  url.replaceAll("\\ ","%20");
            System.out.println( url);
            url =  url.replaceAll("\\/","%2F");
            System.out.println( url);
            url =  url.replaceAll("\\?","%3F");
            System.out.println( url);
            url =  url.replaceAll("\\#","%23");
            System.out.println( url);
            url =  url.replaceAll("\\&","%26");
            System.out.println( url);
            url =  url.replaceAll("\\=","%3D");
            System.out.println( url);

            System.out.println( url);
            System.out.println(URLEncoder.encode("= + / ? % # &","UTF-8"));

            System.out.println(URLDecoder.decode(URLDecoder.decode("%25E4%25BB%258A%25E5%25A4%25A9%25E6%2598%25AF%25E5%2590%25A6%25E4%25BC%259A%25E4%25B8%258B%25E9%259B%25A8++2&content=%25E4%25BB%258A%25E5%25A4%25A9%25E6%2598%25AF%25E5%2590%25A6%25E4%25BC%259A%25E4%25B8%258B%25E9%259B%25A8+2&option1=%25E4%25BC%259A&option2=%25E4%25B8%258D%25E4%25BC%259A&lang=zh&id=MNU9/Yf0iwhAvLrZ3o//7g==")));




        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("ok");
    }
}

