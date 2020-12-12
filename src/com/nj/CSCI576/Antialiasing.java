package com.nj.CSCI576;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class Antialiasing {
    int[] filter = {1, 2, 1, 2, 4, 2, 1, 2, 1};
    int filterWidth = 3;

    public BufferedImage gaussianBlur(BufferedImage image) {
        if (filter.length % filterWidth != 0) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int sum = IntStream.of(filter).sum();

        int[] inputPixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[] outputPixels = new int[inputPixels.length];

        int pixelIndexOffset = width - filterWidth;
        int centerOffsetX = filterWidth / 2;
        int centerOffsetY = filter.length / filterWidth / 2;

        for (int h = height - filter.length / filterWidth + 1, w = width - filterWidth + 1, y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = 0;
                int g = 0;
                int b = 0;
                for (int filterIndex = 0, pixelIndex = y * width + x; filterIndex < filter.length; pixelIndex += pixelIndexOffset) {
                    for (int fx = 0; fx < filterWidth; fx++, pixelIndex++, filterIndex++) {
                        int col = inputPixels[pixelIndex];
                        int factor = filter[filterIndex];

                        r += ((col >>> 16) & 0xFF) * factor;
                        g += ((col >>> 8) & 0xFF) * factor;
                        b += (col & 0xFF) * factor;
                    }
                }
                r /= sum;
                g /= sum;
                b /= sum;
                outputPixels[x + centerOffsetX + (y + centerOffsetY) * width] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        result.setRGB(0, 0, width, height, outputPixels, 0, width);
        return result;
    }
}
