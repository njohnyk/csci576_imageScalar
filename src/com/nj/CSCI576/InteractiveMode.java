package com.nj.CSCI576;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.awt.image.RescaleOp;

public class InteractiveMode extends JPanel implements MouseMotionListener {
    BufferedImage image;
    BufferedImage lowerContrast;
    BufferedImage scaledClipImage;
    Dimension size;
    Rectangle clip;
    int clipX, clipY;
    float scaleFactor;
    int antialiasingFlag;

    public InteractiveMode(BufferedImage image, float scaleFactor, int antialiasingFlag) {
        this.image = image;
        this.lowerContrast = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        RescaleOp rescaleOp = new RescaleOp(1f, -50, null);
        rescaleOp.filter(image, lowerContrast);
        this.size = new Dimension(image.getWidth(), image.getHeight());
        this.scaleFactor = scaleFactor;
        addMouseMotionListener(this);
        this.antialiasingFlag = antialiasingFlag;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(scaledClipImage == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = (getWidth() - size.width)/2;
        int y = (getHeight() - size.height)/2;
        g2.drawImage(lowerContrast, x, y, this);

        if(clip == null)
            createClip();

        int radius = 100;
        g2.translate(clipX + 50, clipY + 50);
        Arc2D myArea = new Arc2D.Float(0 - radius, 0 - radius,
                2 * radius, 2 * radius, 0, -360, Arc2D.OPEN);
        g2.setClip(myArea);
        BufferedImage temp = scaledClipImage.getSubimage(0, 0, scaledClipImage.getWidth(), scaledClipImage.getHeight());
        g2.drawImage(temp, -radius, -radius, this);
    }

    public void setClip(int x, int y) {
        if(clip == null) {
            createClip();
            return;
        }
        int x0 = (getWidth() - size.width)/2;
        int y0 = (getHeight() - size.height)/2;
        if(x < x0 || x + clip.width  > x0 + size.width ||
                y < y0 || y + clip.height > y0 + size.height)
            return;
        clip.setLocation(x - 50, y - 50);
        repaint();
    }

    public Dimension getPreferredSize() {
        return size;
    }

    private void createClip() {
        clip = new Rectangle(200, 200);
        clip.x = (getWidth() - clip.width)/2;
        clip.y = (getHeight() - clip.height)/2;
    }

    private void clipImage() {
        BufferedImage clipped = null;
        try
        {
            int w = clip.width;
            int h = clip.height;
            int x0 = (getWidth()  - size.width)/2;
            int y0 = (getHeight() - size.height)/2;
            clipX = clip.x - x0;
            clipY = clip.y - y0;
            clipped = image.getSubimage(clipX, clipY, w, h);
        }
        catch(RasterFormatException rfe)
        {
            return;
        }

        int w = clipped.getWidth();
        int h = clipped.getHeight();

        Scaling scaling = new Scaling();
        int[] pix = new int[w * h * 3];
        int index = 0;
        int newWidth = (int) (scaleFactor * w);
        int newHeight = (int) (scaleFactor * h);
        for(int y = 0; y < w; y++) {
            for(int x = 0; x < h; x++) {
                pix[index++] = clipped.getRGB(x, y);
            }
        }
        scaledClipImage = scaling.bilinearImageScaling(pix, w, h, newWidth, newHeight);
        if(antialiasingFlag == 1) {
            Antialiasing antialiasing = new Antialiasing();

            BufferedImage blurred = antialiasing.gaussianBlur(scaledClipImage);
            scaledClipImage = blurred;
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        setClip(x, y);
        clipImage();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        setClip(x, y);
        clipImage();
    }
}
