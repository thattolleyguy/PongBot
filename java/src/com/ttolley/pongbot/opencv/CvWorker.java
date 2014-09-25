/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.opencv;

import com.ttolley.pongbot.opencv.FilteredObject.Target;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class CvWorker extends SwingWorker<Void, PublishObject> {

    Map<FilterType, Filter> filters = new EnumMap<>(FilterType.class);

    public static enum FilterType {

        BALL(0, 50, 50, 6, 255, 255, 20, 2, 4, 5),
        ROBOT(0, 50, 50, 50, 75, 100, 20, 2, 4, 5);
        public final int hMin;
        public final int sMin;
        public final int vMin;
        public final int hMax;
        public final int sMax;
        public final int vMax;
        public final int objSize;
        public final int numObj;
        public final int erode;
        public final int dilate;

        private FilterType(int hMin, int sMin, int vMin, int hMax, int sMax, int vMax, int objSize, int numObj, int erode, int dilate) {
            this.hMin = hMin;
            this.sMin = sMin;
            this.vMin = vMin;
            this.hMax = hMax;
            this.sMax = sMax;
            this.vMax = vMax;
            this.objSize = objSize;
            this.numObj = numObj;
            this.erode = erode;
            this.dilate = dilate;
        }

        public static FilterType fromString(String string) {
            for (FilterType filterType : FilterType.values()) {
                if (filterType.name().equals(string)) {
                    return filterType;
                }
            }
            return null;
        }
    }

    public static class Filter {

        public Scalar hsv_min = new Scalar(0, 50, 50, 0);
        public Scalar hsv_max = new Scalar(6, 255, 255, 0);
        public int objectSize = 20;
        public int maxObjects = 2;
        public int erodeSize = 4;
        public int dilateSize = 5;

        public Filter(int hMin, int sMin, int vMin, int hMax, int sMax, int vMax, int objSize, int numObj, int erode, int dilate) {
            hsv_min = new Scalar(hMin, sMin, vMin, 0);
            hsv_max = new Scalar(hMax, sMax, vMax, 0);
            this.objectSize = objSize;
            this.maxObjects = numObj;
            this.erodeSize = erode;
            this.dilateSize = dilate;
        }

        public void updateFilter(int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) {
            hsv_min = new Scalar(hMin, sMin, vMin, 0);
            hsv_max = new Scalar(hMax, sMax, vMax, 0);
        }
    }

    private DescriptiveStatistics yPos;
    private DescriptiveStatistics xPos;

    List<CameraEventHandler> handlers = new ArrayList<>();

    public CvWorker() {
        yPos = new DescriptiveStatistics();
        xPos = new DescriptiveStatistics();
        xPos.setWindowSize(10);
        yPos.setWindowSize(10);

        try {
            // Load the native library.  
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            capture = new VideoCapture(1);

        } catch (Exception ex) {
            System.out.println(ex.getCause().getMessage());
        }

    }
    private VideoCapture capture;

    public Filter getFilter(FilterType object) {
        return filters.get(object);
    }

    public void setFilter(FilterType type, Filter filter) {
        this.filters.put(type, filter);
    }

    private Target findTarget(List<MatOfPoint> contours, Mat webcam_image, Filter filter) {
        Target largestTarget = null;
        for (MatOfPoint matOfPoint : contours) {
            Moments moment = Imgproc.moments(matOfPoint);
            double area = moment.get_m00();

            if ((largestTarget == null && area > filter.objectSize * filter.objectSize) || (largestTarget != null && area > largestTarget.area)) {
                // Found object, do something about it
                largestTarget = new Target(moment.get_m10() / area, moment.get_m01() / area, area);
            }
        }
        if (largestTarget != null) {
            xPos.addValue(largestTarget.x);
            yPos.addValue(largestTarget.y);
            Core.circle(webcam_image, new Point(xPos.getMean(), yPos.getMean()), 10, new Scalar(0, 0, 255));
            Core.putText(webcam_image, "[" + xPos.getMean() + " " + yPos.getMean() + "]", new Point(xPos.getMean() - 40, yPos.getMean() + 25), 1, 1, new Scalar(0, 0, 255));
        }
        return largestTarget;
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            //-- 2. Read the video stream  
            Mat webcam_image = new Mat();

            if (capture.isOpened()) {
                while (true) {
                    capture.read(webcam_image);
                    if (!webcam_image.empty()) {
                        PublishObject publishObject = new PublishObject(webcam_image);
                        for (Map.Entry<FilterType, Filter> entry : filters.entrySet()) {
                            Mat hsv_image = new Mat();
                            Mat thresholded = new Mat();
                            Filter filter = entry.getValue();
                            // One way to select a range of colors by Hue  
                            Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
                            Core.inRange(hsv_image, filter.hsv_min, filter.hsv_max, thresholded);
                            // Morph open
                            final Size erodeSizeObj = new Size(filter.erodeSize, filter.erodeSize);
                            final Size dilateSizeObj = new Size(filter.dilateSize, filter.dilateSize);
                            Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, erodeSizeObj));
                            Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, erodeSizeObj));
                            // Morph close
                            Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, dilateSizeObj));
                            Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, dilateSizeObj));

                            Mat temp = new Mat();
                            thresholded.copyTo(temp);
                            List<MatOfPoint> contours = new ArrayList();
                            Mat heirarchy = new Mat();
                            Imgproc.findContours(temp, contours, heirarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                            FilteredObject.Target largestTarget = findTarget(contours, webcam_image, filter);
                            publishObject.addObject(entry.getKey(), new FilteredObject(largestTarget, thresholded));
                        }
                        publish(publishObject);

                    } else {
                        System.out.println(" --(!) No captured frame -- Break!");
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Unable to loop");
            System.out.println(getStackTrace(ex));
        }
        return null;
    }

    @Override
    protected void process(List<PublishObject> chunks) {
        PublishObject latestPublish = chunks.get(0);
        for (CameraEventHandler cameraEventHandler : handlers) {
            cameraEventHandler.handleLatestCameraResult(latestPublish);
        }

    }

    public void registerCameraEventHandler(CameraEventHandler cameraEventHandler) {
        this.handlers.add(cameraEventHandler);
    }

}
