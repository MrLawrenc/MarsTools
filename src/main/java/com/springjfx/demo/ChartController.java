package com.springjfx.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author : LiuMingyao
 * @date : 2020/4/4 10:42
 * @description : TODO
 */
@Component
public class ChartController implements Initializable {
    Logger logger = LoggerFactory.getLogger(ChartController.class);
    @FXML
    public LineChart<String, Double> lineChart;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("init ChartController.............");
    }
}