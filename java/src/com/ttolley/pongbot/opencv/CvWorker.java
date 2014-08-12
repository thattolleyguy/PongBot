/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.opencv;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

class Panel extends JPanel {

    private static final long serialVersionUID = 1L;
    private BufferedImage image;

    // Create a constructor method  
    public Panel() {
        super();
    }

    private BufferedImage getimage() {
        return image;
    }

    public void setimage(BufferedImage newimage) {
        image = newimage;
        return;
    }

    public void setimagewithMat(Mat newimage) {
        image = this.matToBufferedImage(newimage);
        return;
    }

    /**
     * Converts/writes a Mat into a BufferedImage.
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;
        matrix.get(0, 0, data);
        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                // bgr to rgb  
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                return null;
        }
        BufferedImage image2 = new BufferedImage(cols, rows, type);
        image2.getRaster().setDataElements(0, 0, cols, rows, data);
        return image2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //BufferedImage temp=new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);  
        BufferedImage temp = getimage();
        //Graphics2D g2 = (Graphics2D)g;
        if (temp != null) {
            g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);
        }
    }
}

public class CvWorker extends SwingWorker<Void, Object> {

    Scalar hsv_min = new Scalar(0, 50, 50, 0);
    Scalar hsv_max = new Scalar(6, 255, 255, 0);
    int objectSize = 20;
    int maxObjects = 2;
    int erodeSize = 4;
    int dilateSize = 5;

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

    public void main() {
        // Load the native library.  
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // It is better to group all frames together so cut and paste to  
        // create more frames is easier  
        JFrame frame1 = new JFrame("Camera");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(640, 480);
        frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());
        Panel panel1 = new Panel();
        frame1.setContentPane(panel1);
        frame1.setVisible(true);
        JFrame frame2 = new JFrame("HSV");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(640, 480);
        frame2.setBounds(300, 100, frame2.getWidth() + 300, 100 + frame2.getHeight());
        Panel panel2 = new Panel();
        frame2.setContentPane(panel2);
        frame2.setVisible(true);
        JFrame frame3 = new JFrame("Threshold");
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame3.setSize(640, 480);
        frame3.setBounds(600, 200, frame3.getWidth() + 600, 200 + frame3.getHeight());
        Panel panel3 = new Panel();
        frame3.setContentPane(panel3);
        frame3.setVisible(true);
        //-- 2. Read the video stream  
        VideoCapture capture = new VideoCapture(0);
        Mat webcam_image = new Mat();
        Mat hsv_image = new Mat();
        Mat thresholded = new Mat();
        capture.read(webcam_image);
        frame1.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
        frame2.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
        frame3.setSize(webcam_image.width() + 40, webcam_image.height() + 60);

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
                    Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, erodeSizeObj));
                    Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, dilateSizeObj));
                    // Morph close
                    Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, erodeSizeObj));
                    Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, dilateSizeObj));

                    Mat temp = new Mat();
                    thresholded.copyTo(temp);
                    List<MatOfPoint> contours = new ArrayList();
                    Mat heirarchy = new Mat();
                    Imgproc.findContours(temp, contours, heirarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
                    double refArea = 0;
                    boolean objectFound = false;
                    List<Target> targets = new ArrayList<>();

                    for (MatOfPoint matOfPoint : contours) {
                        targets.clear();
                        Moments moment = Imgproc.moments(matOfPoint);
                        double area = moment.get_m00();

                        if (area > objectSize * objectSize) {
                            // Found object, do something about it
                            Target t = new Target(moment.get_m10() / area, moment.get_m01() / area);
                            targets.add(t);
                            objectFound = true;
                        }
                    }
                    if (objectFound) {
                        for (Target target : targets) {
                            Core.circle(webcam_image, new Point(target.x, target.y), 10, new Scalar(0, 0, 255));
                            Core.putText(webcam_image, "[" + target.x + " " + target.y + "]", new Point(target.x - 40, target.y + 25), 1, 1, new Scalar(0, 0, 255));
                        }
                    }
                    panel1.setimagewithMat(webcam_image);
                    panel2.setimagewithMat(hsv_image);
                    panel3.setimagewithMat(thresholded);
                    frame1.repaint();
                    frame2.repaint();
                    frame3.repaint();
                } else {
                    System.out.println(" --(!) No captured frame -- Break!");
                    break;
                }
            }
        }
        return;
    }

    @Override
    protected Void doInBackground() throws Exception {
        main();
        return null;
    }

    public void setErodeSize(Integer erodeSize) {
        this.erodeSize = erodeSize;
    }

    public void setDilateSize(Integer dilateSize) {
        this.dilateSize = dilateSize;
    }

}

class Target {

    public final double x;
    public final double y;

    public Target(double x, double y) {
        this.x = x;
        this.y = y;
    }

}
