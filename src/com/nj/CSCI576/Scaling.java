package com.nj.CSCI576;

import java.awt.image.BufferedImage;

public class Scaling {
    public BufferedImage nearestNeighborImageScaling(int[] pixels, int width, int height, int newWidth, int newHeight) {
        if(newWidth == 0 || newHeight == 0) {
            return null;
        }
        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        int[] newImage = new int[newWidth * newHeight];
        double xRatio = width / (double) newWidth;
        double yRatio = height / (double) newHeight;
        double px, py;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                px = Math.floor(j * xRatio);
                py = Math.floor(i * yRatio);
                newImage[(i * newWidth) + j] = pixels[(int) ((py * width) + px)];
                img.setRGB(j, i, newImage[(i * newWidth) + j]);
            }
        }
        return img;
    }

    public BufferedImage bilinearImageScaling(int[] pixels, int width, int height, int newWidth, int newHeight) {
        int[] temp = new int[newWidth * newHeight];
        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        int A, B, C, D;
        int px, py, index;
        float xRatio = ((float) (width - 1)) / newWidth;
        float yRatio = ((float) (height - 1)) / newHeight;
        float xDiff, yDiff, r, g, b;
        int offset = 0 ;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                px = (int) (j * xRatio);
                py = (int) (i * yRatio);
                xDiff = (j * xRatio) - px;
                yDiff = (i * yRatio) - py;
                index = (py * width + px) ;
                A = pixels[index] ;
                B = pixels[index + 1] ;
                C = pixels[index + width] ;
                D = pixels[index + width + 1] ;

                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                r = ((A >> 16) & 0xff) * (1 - xDiff) * (1 - yDiff) + ((B >> 16) & 0xff) * (xDiff) * (1 - yDiff) +
                        ((C >> 16) & 0xff) * (yDiff) * (1 - xDiff) + ((D >> 16) & 0xff) * (xDiff * yDiff);

                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                g = ((A >> 8) & 0xff) * (1 - xDiff) * (1 - yDiff) + ((B >> 8) & 0xff) * (xDiff) * (1 - yDiff) +
                        ((C >> 8) & 0xff) * (yDiff) * (1 - xDiff) + ((D >> 8) & 0xff) * (xDiff * yDiff);

                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                b = (A & 0xff) * (1 - xDiff) * (1 - yDiff) + (B & 0xff) * (xDiff) * (1 - yDiff) +
                        (C & 0xff) * (yDiff) * (1 - xDiff) + (D & 0xff) * (xDiff * yDiff);

                temp[offset] = 0xff000000 |
                        ((((int) r) << 16) & 0xff0000) | ((((int) g) << 8) & 0xff00) | ((int) b) ;

                img.setRGB(j, i, temp[offset]);
                offset++;
            }
        }
        return img;
    }
}
