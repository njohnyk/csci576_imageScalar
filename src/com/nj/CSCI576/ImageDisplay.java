package com.nj.CSCI576;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {

    JFrame frame;
    JLabel lbIm1;
    BufferedImage image;
    BufferedImage scaledImage;
    int width = 1920;
    int height = 1080;

    /** Read Image RGB
     *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    private void readImageRGB(int width, int height, String[] args) {
        try
        {
            int frameLength = width*height*3;

            String imgPath = args[0];
            float scaleFactor = Float.parseFloat(args[2]);
            int antialiasingFlag = Integer.parseInt(args[3]);

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            int[] pixel = new int[width * height * 3];
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    pixel[ind] = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    image.setRGB(x,y,pixel[ind]);
                    ind++;
                }
            }

            Scaling scaling = new Scaling();
            int newWidth = (int) Math.ceil(width * scaleFactor);
            int newHeight = (int) Math.ceil(height * scaleFactor);
            scaledImage = scaling.nearestNeighborImageScaling(pixel, width, height,
                    newWidth, newHeight);


            if(antialiasingFlag == 1) {
                Antialiasing antialiasing = new Antialiasing();

                BufferedImage blurred = antialiasing.gaussianBlur(scaledImage);
                scaledImage = blurred;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showIms(String[] args){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(width, height, args);

        int modeType = Integer.parseInt(args[1]);
        if(modeType == 1) {
            frame = new JFrame();
            GridBagLayout gLayout = new GridBagLayout();
            frame.getContentPane().setLayout(gLayout);
            frame.setResizable(false);

            lbIm1 = new JLabel(new ImageIcon(scaledImage));

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;
            c.weightx = 0.5;
            c.gridx = 0;
            c.gridy = 0;

            frame.getContentPane().add(lbIm1, c);

            frame.pack();
            frame.setVisible(true);
        }
        else if(modeType == 2) {
            frame = new JFrame();
            float scaleFactor = Float.parseFloat(args[2]);
            InteractiveMode interactiveMode = new InteractiveMode(image, scaleFactor, Integer.parseInt(args[3]));

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new JScrollPane(interactiveMode));
            frame.setSize(width, height);
            frame.setLocation(0,0);
            frame.setVisible(true);
        }
        else {
            Analysis analysis = new Analysis(image);
            analysis.findAnalysis();
            frame = new JFrame();
            GridBagLayout gLayout = new GridBagLayout();
            frame.getContentPane().setLayout(gLayout);
            frame.setResizable(false);

            lbIm1 = new JLabel(new ImageIcon(analysis.getImageCopy()));

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;
            c.weightx = 0.5;
            c.gridx = 0;
            c.gridy = 0;

            frame.getContentPane().add(lbIm1, c);

            frame.pack();
            frame.setVisible(true);
        }
    }

}
