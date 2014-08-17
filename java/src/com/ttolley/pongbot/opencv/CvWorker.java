/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.opencv;

import com.ttolley.pongbot.opencv.PublishObject.Target;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
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

    Scalar hsv_min = new Scalar(0, 50, 50, 0);
    Scalar hsv_max = new Scalar(6, 255, 255, 0);
    int objectSize = 20;
    int maxObjects = 2;
    int erodeSize = 4;
    int dilateSize = 5;

    List<CameraEventHandler> handlers = new ArrayList<>();

    public CvWorker() {
        try {
            // Load the native library.  
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            capture = new VideoCapture(0);

        } catch (Exception ex) {
            System.out.println(ex.getCause().getMessage());
        }

    }
    private VideoCapture capture;

    public void updateFilter(int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) {
        hsv_min = new Scalar(hMin, sMin, vMin, 0);
        hsv_max = new Scalar(hMax, sMax, vMax, 0);
    }

    public void setObjectSize(int size) {
        this.objectSize = size;
    }

    public void maxObjects(int objects) {
        this.maxObjects = objects;
    }

    private Target findTarget(List<MatOfPoint> contours, Mat webcam_image) {
        Target largestTarget = null;
        for (MatOfPoint matOfPoint : contours) {
            Moments moment = Imgproc.moments(matOfPoint);
            double area = moment.get_m00();

            if ((largestTarget == null && area > objectSize * objectSize) || (largestTarget != null && area > largestTarget.area)) {
                // Found object, do something about it
                largestTarget = new Target(moment.get_m10() / area, moment.get_m01() / area, area);
            }
        }
        if (largestTarget != null) {

            Core.circle(webcam_image, new Point(largestTarget.x, largestTarget.y), 10, new Scalar(0, 0, 255));
            Core.putText(webcam_image, "[" + largestTarget.x + " " + largestTarget.y + "]", new Point(largestTarget.x - 40, largestTarget.y + 25), 1, 1, new Scalar(0, 0, 255));
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
            Mat hsv_image = new Mat();
            Mat thresholded = new Mat();

            if (capture.isOpened()) {
                while (true) {
                    capture.read(webcam_image);
                    if (!webcam_image.empty()) {
                        // One way to select a range of colors by Hue  
                        Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
                        Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);
                        // Morph open
                        final Size erodeSizeObj = new Size(erodeSize, erodeSize);
                        final Size dilateSizeObj = new Size(dilateSize, dilateSize);
                        Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, erodeSizeObj));
                        Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, erodeSizeObj));
                        // Morph close
                        Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, dilateSizeObj));
                        Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, dilateSizeObj));

                        Mat temp = new Mat();
                        thresholded.copyTo(temp);
                        List<MatOfPoint> contours = new ArrayList();
                        Mat heirarchy = new Mat();
                        Imgproc.findContours(temp, contours, heirarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
                        PublishObject.Target largestTarget = findTarget(contours, webcam_image);
                        publish(new PublishObject(largestTarget, webcam_image, thresholded));

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

    public void setErodeSize(Integer erodeSize) {
        this.erodeSize = erodeSize;
    }

    public void setDilateSize(Integer dilateSize) {
        this.dilateSize = dilateSize;
    }

    public void registerCameraEventHandler(CameraEventHandler cameraEventHandler) {
        this.handlers.add(cameraEventHandler);
    }

}
