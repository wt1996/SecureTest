package cn.wangtao.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName Run
 * @Auth 桃子
 * @Date 2019-6-26 17:12
 * @Version 1.0
 * @Description
 **/
@SpringBootApplication
@ComponentScan("cn.wangtao")
public class Run {
    public static void main(String[] args) {
        SpringApplication.run(Run.class,args);
    }
}
