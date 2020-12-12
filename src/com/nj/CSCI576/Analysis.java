package com.nj.CSCI576;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Random;

public class Analysis {
    BufferedImage image;
    BufferedImage imageCopy;
    int width, height;

    public Analysis(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void findAnalysis() {
        imageCopy = deepCopy();
        int percent = 10;
        int numOfPixels = width * height;
        double maxPixelsToRemove = findXPercent(percent, numOfPixels);
        int index = 0;
        while(index < maxPixelsToRemove) {
            Random random = new Random();
            int i = random.nextInt(width);
            int j = random.nextInt(height);
            imageCopy.setRGB(i, j, 0);
            index++;
        }

        interpolate();
    }

    public BufferedImage deepCopy() {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPreMultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPreMultiplied, null);
    }

    public void interpolate() {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                int rgb = imageCopy.getRGB(i, j);
                if(rgb == -16777216) {
                    int a = 0, b = 0, c = 0, d = 0, x, y, index ;
                    float x_ratio = 1 ;
                    float y_ratio = 1 ;
                    float x_diff, y_diff, blue, red, green ;
                    int offset = 0 ;
                    x = (int)(x_ratio * j) ;
                    y = (int)(y_ratio * i) ;
                    x_diff = (x_ratio * j) - x ;
                    y_diff = (y_ratio * i) - y ;
                    index = (y * 1 + x) ;
                    try {
                        a = imageCopy.getRGB(i - 1, j - 1); ;
                    } catch(ArrayIndexOutOfBoundsException e) {
                        for(int m = i; m < width; m++) {
                            for(int n = j; n < height; n++) {
                                a = imageCopy.getRGB(m, n);
                                if(a == -16777216) break;
                            }
                        }
                    }
                    try {
                        b = imageCopy.getRGB(i - 1, j + 1); ;
                    } catch(ArrayIndexOutOfBoundsException e) {
                        for(int m = i; m < width; m++) {
                            for(int n = j; n < height; n++) {
                                b = imageCopy.getRGB(m, n);
                                if(b == -16777216) break;
                            }
                        }
                    }

                    try {
                        c = imageCopy.getRGB(i + 1, j - 1); ;
                    } catch(ArrayIndexOutOfBoundsException e) {
                        for(int m = i; m < width; m++) {
                            for(int n = j; n < height; n++) {
                                c = imageCopy.getRGB(m, n);
                                if(c == -16777216) break;
                            }
                        }
                    }

                    try {
                        d = imageCopy.getRGB(i + 1, j + 1); ;
                    } catch(ArrayIndexOutOfBoundsException e) {
                        for(int m = i; m < width; m++) {
                            for(int n = j; n < height; n++) {
                                d = imageCopy.getRGB(m, n);
                                if(d == -16777216) break;
                            }
                        }
                    }

                    // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                    blue = (a&0xff)*(1-x_diff)*(1-y_diff) + (b&0xff)*(x_diff)*(1-y_diff) +
                            (c&0xff)*(y_diff)*(1-x_diff)   + (d&0xff)*(x_diff*y_diff);

                    // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                    green = ((a>>8)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>8)&0xff)*(x_diff)*(1-y_diff) +
                            ((c>>8)&0xff)*(y_diff)*(1-x_diff)   + ((d>>8)&0xff)*(x_diff*y_diff);

                    // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                    red = ((a>>16)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>16)&0xff)*(x_diff)*(1-y_diff) +
                            ((c>>16)&0xff)*(y_diff)*(1-x_diff)   + ((d>>16)&0xff)*(x_diff*y_diff);

                    int newValue  =
                            0xff000000 | // hardcode alpha
                                    ((((int)red)<<16)&0xff0000) |
                                    ((((int)green)<<8)&0xff00) |
                                    ((int)blue) ;
                    imageCopy.setRGB(i, j, newValue);
                }
            }
        }

        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int newRGB = 0;
                int rgb = imageCopy.getRGB(i, j);
                if (rgb == -16777216) {
                    for(int m = i + 1; m < width; m++) {
                        for(int n = j + 1; n < height; n++) {
                            newRGB = imageCopy.getRGB(m, n);
                            if(newRGB != -16777216) {
                                imageCopy.setRGB(i, j, newRGB);
                                break;
                            }
                        }
                    }
                }
            }
        }

        double sum = 0;
        try {
            sum = findSDD();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sum / 1000000000000000L);
    }

    private double findSDD() throws IOException {

        double sum = 0;
        double diff = 0;
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                double img1 = getRGBValue(image, i, j);
                double img2 = getRGBValue(imageCopy, i, j);

                diff =  img1 - img2;
                sum += diff * diff;
            }
        }
        return sum;
    }

    private int getRGBValue(BufferedImage img, int i, int j) {
        Color c = new Color(img.getRGB(i,j));
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int rgb = 65536 * r + 256 * g + b;
        return rgb;
    }

    private double findXPercent(double x, int numOfPixels) {
        return (x / 100) * numOfPixels;
    }

    public BufferedImage getImageCopy() {
        return imageCopy;
    }
}
