/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.opencv;

import org.opencv.core.Mat;

/**
 *
 * @author tyler
 */
public class PublishObject {

    public final Target t;
    public final Mat webcamImage;
    public final Mat thresholdImage;

    public PublishObject(Target t, Mat webcamImage, Mat thresholdImage) {
        this.t = t;
        this.webcamImage = webcamImage;
        this.thresholdImage = thresholdImage;
    }

    public static class Target {

        public final double x;
        public final double y;
        public final double area;

        public Target(double x, double y, double area) {
            this.x = x;
            this.y = y;
            this.area = area;
        }

    }
}
