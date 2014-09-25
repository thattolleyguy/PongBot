/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.opencv;

import com.ttolley.pongbot.opencv.CvWorker.FilterType;
import java.util.EnumMap;
import java.util.Map;
import org.opencv.core.Mat;

/**
 *
 * @author tyler
 */
public class PublishObject {

    Map<FilterType, FilteredObject> objects = new EnumMap<>(FilterType.class);
    public final Mat webcamImage;

    public PublishObject(Mat webcamImage) {
        this.webcamImage = webcamImage;
    }

    public void addObject(FilterType filterType, FilteredObject object) {
        objects.put(filterType, object);
    }

    public FilteredObject getObject(FilterType type) {
        return objects.get(type);
    }
}
