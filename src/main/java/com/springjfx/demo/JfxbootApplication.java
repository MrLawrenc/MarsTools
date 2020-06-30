package com.springjfx.demo;

import application.config.Configuration;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JfxbootApplication {

    public static void main(String[] args) {
        Configuration.initProp();
        //启动jfx
        Application.launch(ChartApplication.class, args);
    }

}
