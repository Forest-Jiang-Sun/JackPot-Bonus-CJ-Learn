package com.aspectgaming.util.image;

import static java.awt.image.BufferedImage.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class LanczosResampler {

    private final int MAX_CHANNEL_VALUE = 255;

    private final int numberOfThreads = Runtime.getRuntime().availableProcessors();
    private final LanczosFilter filter = new LanczosFilter(3);
    private SubSamplingData horizontalSubsamplingData;
    private SubSamplingData verticalSubsamplingData;

    private final int srcWidth;
    private final int srcHeight;
    private final int dstWidth;
    private final int dstHeight;

    private int numComponents;

    public LanczosResampler(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        if (dstWidth < 3 || dstHeight < 3) {
            throw new RuntimeException("Output image must be at least 3x3.");
        }

        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;

        horizontalSubsamplingData = createSubSampling(filter, srcWidth, dstWidth);
        verticalSubsamplingData = createSubSampling(filter, srcHeight, dstHeight);
    }

    public BufferedImage filter(BufferedImage srcImg, BufferedImage dest) {
        if (srcImg.getType() == TYPE_BYTE_BINARY || srcImg.getType() == TYPE_BYTE_INDEXED || srcImg.getType() == TYPE_CUSTOM) {
            srcImg = ImageUtil.convert(srcImg, srcImg.getColorModel().hasAlpha() ? TYPE_4BYTE_ABGR : TYPE_3BYTE_BGR);
        }

        this.numComponents = srcImg.getColorModel().getNumComponents();

        byte[][] workPixels = new byte[srcHeight][dstWidth * numComponents];

        final BufferedImage scrImgCopy = srcImg;
        final byte[][] workPixelsCopy = workPixels;
        Thread[] threads = new Thread[numberOfThreads - 1];
        for (int i = 1; i < numberOfThreads; i++) {
            final int finalI = i;
            threads[i - 1] = new Thread(() -> horizontallyFromSrcToWork(scrImgCopy, workPixelsCopy, finalI, numberOfThreads));
            threads[i - 1].start();
        }
        horizontallyFromSrcToWork(scrImgCopy, workPixelsCopy, 0, numberOfThreads);
        waitForAllThreads(threads);

        byte[] outPixels = new byte[dstWidth * dstHeight * numComponents];

        final byte[] outPixelsCopy = outPixels;
        for (int i = 1; i < numberOfThreads; i++) {
            final int finalI = i;
            threads[i - 1] = new Thread(() -> verticalFromWorkToDst(workPixelsCopy, outPixelsCopy, finalI, numberOfThreads));
            threads[i - 1].start();
        }
        verticalFromWorkToDst(workPixelsCopy, outPixelsCopy, 0, numberOfThreads);
        waitForAllThreads(threads);

        if (dest != null && dstWidth == dest.getWidth() && dstHeight == dest.getHeight()) {
            if (dest.getColorModel().getNumComponents() != numComponents) {
                throw new RuntimeException("Wrong output image format");
            }
        } else {
            dest = new BufferedImage(dstWidth, dstHeight, getResultBufferedImageType(srcImg));
        }

        ImageUtil.setBGRPixels(outPixels, dest, 0, 0, dstWidth, dstHeight);
        return dest;
    }

    private void waitForAllThreads(Thread[] threads) {
        try {
            for (Thread t : threads) {
                t.join(Long.MAX_VALUE);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    static SubSamplingData createSubSampling(LanczosFilter filter, int srcSize, int dstSize) {
        float scale = (float) dstSize / (float) srcSize;
        int[] arrN = new int[dstSize];
        int numContributors;
        float[] arrWeight;
        int[] arrPixel;

        final float fwidth = filter.getSamplingRadius();

        float centerOffset = 0.5f / scale - 0.5f;

        if (scale < 1.0f) { // shrink
            final float width = fwidth / scale;
            numContributors = (int) (width * 2.0f + 2); // Heinz: added 1 to be save with the ceilling
            arrWeight = new float[dstSize * numContributors];
            arrPixel = new int[dstSize * numContributors];

            final float fNormFac = (float) (1f / (Math.ceil(width) / fwidth));

            for (int i = 0; i < dstSize; i++) {
                final int subindex = i * numContributors;
                float center = i / scale + centerOffset;
                int left = (int) Math.floor(center - width);
                int right = (int) Math.ceil(center + width);
                for (int j = left; j <= right; j++) {
                    float weight;
                    weight = filter.apply((center - j) * fNormFac);

                    if (weight == 0.0f) {
                        continue;
                    }
                    int n;
                    if (j < 0) {
                        n = -j;
                    } else if (j >= srcSize) {
                        n = srcSize - j + srcSize - 1;
                    } else {
                        n = j;
                    }
                    int k = arrN[i];
                    // assert k == j-left:String.format("%s = %s %s", k,j,left);
                    arrN[i]++;
                    if (n < 0 || n >= srcSize) {
                        weight = 0.0f;// Flag that cell should not be used
                    }
                    arrPixel[subindex + k] = n;
                    arrWeight[subindex + k] = weight;
                }
                // normalize the filter's weight's so the sum equals to 1.0, very important for avoiding box type of artifacts
                final int max = arrN[i];
                float tot = 0;
                for (int k = 0; k < max; k++)
                    tot += arrWeight[subindex + k];
                if (tot != 0f) { // 0 should never happen except bug in filter
                    for (int k = 0; k < max; k++)
                        arrWeight[subindex + k] /= tot;
                }
            }
        } else { // enlarge
            numContributors = (int) (fwidth * 2.0f + 1);
            arrWeight = new float[dstSize * numContributors];
            arrPixel = new int[dstSize * numContributors];
            //
            for (int i = 0; i < dstSize; i++) {
                final int subindex = i * numContributors;
                float center = i / scale + centerOffset;
                int left = (int) Math.floor(center - fwidth);
                int right = (int) Math.ceil(center + fwidth);
                for (int j = left; j <= right; j++) {
                    float weight = filter.apply(center - j);
                    if (weight == 0.0f) {
                        continue;
                    }
                    int n;
                    if (j < 0) {
                        n = -j;
                    } else if (j >= srcSize) {
                        n = srcSize - j + srcSize - 1;
                    } else {
                        n = j;
                    }
                    int k = arrN[i];
                    arrN[i]++;
                    if (n < 0 || n >= srcSize) {
                        weight = 0.0f;// Flag that cell should not be used
                    }
                    arrPixel[subindex + k] = n;
                    arrWeight[subindex + k] = weight;
                }

                // normalize the filter's weight's so the sum equals to 1.0, very important for avoiding box type of artifacts
                final int max = arrN[i];
                float tot = 0;
                for (int k = 0; k < max; k++)
                    tot += arrWeight[subindex + k];
                assert tot != 0 : "should never happen except bug in filter";
                if (tot != 0f) {
                    for (int k = 0; k < max; k++)
                        arrWeight[subindex + k] /= tot;
                }
            }
        }
        return new SubSamplingData(arrN, arrPixel, arrWeight, numContributors);
    }

    private void verticalFromWorkToDst(byte[][] workPixels, byte[] outPixels, int start, int delta) {
        if (numComponents == 1) {
            verticalFromWorkToDstGray(workPixels, outPixels, start, numberOfThreads);
            return;
        }
        boolean useChannel3 = numComponents > 3;
        for (int x = start; x < dstWidth; x += delta) {
            final int xLocation = x * numComponents;
            for (int y = dstHeight - 1; y >= 0; y--) {
                final int yTimesNumContributors = y * verticalSubsamplingData.numContributors;
                final int max = verticalSubsamplingData.arrN[y];
                final int sampleLocation = (y * dstWidth + x) * numComponents;

                float sample0 = 0.0f;
                float sample1 = 0.0f;
                float sample2 = 0.0f;
                float sample3 = 0.0f;
                int index = yTimesNumContributors;
                for (int j = max - 1; j >= 0; j--) {
                    int valueLocation = verticalSubsamplingData.arrPixel[index];
                    float arrWeight = verticalSubsamplingData.arrWeight[index];
                    sample0 += (workPixels[valueLocation][xLocation] & 0xff) * arrWeight;
                    sample1 += (workPixels[valueLocation][xLocation + 1] & 0xff) * arrWeight;
                    sample2 += (workPixels[valueLocation][xLocation + 2] & 0xff) * arrWeight;
                    if (useChannel3) {
                        sample3 += (workPixels[valueLocation][xLocation + 3] & 0xff) * arrWeight;
                    }

                    index++;
                }

                outPixels[sampleLocation] = toByte(sample0);
                outPixels[sampleLocation + 1] = toByte(sample1);
                outPixels[sampleLocation + 2] = toByte(sample2);
                if (useChannel3) {
                    outPixels[sampleLocation + 3] = toByte(sample3);
                }

            }
        }
    }

    private void verticalFromWorkToDstGray(byte[][] workPixels, byte[] outPixels, int start, int delta) {
        for (int x = start; x < dstWidth; x += delta) {
            final int xLocation = x;
            for (int y = dstHeight - 1; y >= 0; y--) {
                final int yTimesNumContributors = y * verticalSubsamplingData.numContributors;
                final int max = verticalSubsamplingData.arrN[y];
                final int sampleLocation = (y * dstWidth + x);

                float sample0 = 0.0f;
                int index = yTimesNumContributors;
                for (int j = max - 1; j >= 0; j--) {
                    int valueLocation = verticalSubsamplingData.arrPixel[index];
                    float arrWeight = verticalSubsamplingData.arrWeight[index];
                    sample0 += (workPixels[valueLocation][xLocation] & 0xff) * arrWeight;

                    index++;
                }

                outPixels[sampleLocation] = toByte(sample0);
            }
        }
    }

    /**
     * Apply filter to sample horizontally from Src to Work
     * 
     * @param srcImg
     * @param workPixels
     */
    private void horizontallyFromSrcToWork(BufferedImage srcImg, byte[][] workPixels, int start, int delta) {
        if (numComponents == 1) {
            horizontallyFromSrcToWorkGray(srcImg, workPixels, start, delta);
            return;
        }
        final int[] tempPixels = new int[srcWidth]; // Used if we work on int based bitmaps, later used to keep channel values
        final byte[] srcPixels = new byte[srcWidth * numComponents]; // create reusable row to minimize memory overhead
        final boolean useChannel3 = numComponents > 3;

        for (int k = start; k < srcHeight; k = k + delta) {
            ImageUtil.getPixelsBGR(srcImg, k, srcWidth, srcPixels, tempPixels);

            for (int i = dstWidth - 1; i >= 0; i--) {
                int sampleLocation = i * numComponents;
                final int max = horizontalSubsamplingData.arrN[i];

                float sample0 = 0.0f;
                float sample1 = 0.0f;
                float sample2 = 0.0f;
                float sample3 = 0.0f;
                int index = i * horizontalSubsamplingData.numContributors;
                for (int j = max - 1; j >= 0; j--) {
                    float arrWeight = horizontalSubsamplingData.arrWeight[index];
                    int pixelIndex = horizontalSubsamplingData.arrPixel[index] * numComponents;

                    sample0 += (srcPixels[pixelIndex] & 0xff) * arrWeight;
                    sample1 += (srcPixels[pixelIndex + 1] & 0xff) * arrWeight;
                    sample2 += (srcPixels[pixelIndex + 2] & 0xff) * arrWeight;
                    if (useChannel3) {
                        sample3 += (srcPixels[pixelIndex + 3] & 0xff) * arrWeight;
                    }
                    index++;
                }

                workPixels[k][sampleLocation] = toByte(sample0);
                workPixels[k][sampleLocation + 1] = toByte(sample1);
                workPixels[k][sampleLocation + 2] = toByte(sample2);
                if (useChannel3) {
                    workPixels[k][sampleLocation + 3] = toByte(sample3);
                }
            }
        }
    }

    /**
     * Apply filter to sample horizontally from Src to Work
     * 
     * @param srcImg
     * @param workPixels
     */
    private void horizontallyFromSrcToWorkGray(BufferedImage srcImg, byte[][] workPixels, int start, int delta) {
        final int[] tempPixels = new int[srcWidth]; // Used if we work on int based bitmaps, later used to keep channel values
        final byte[] srcPixels = new byte[srcWidth]; // create reusable row to minimize memory overhead

        for (int k = start; k < srcHeight; k = k + delta) {
            ImageUtil.getPixelsBGR(srcImg, k, srcWidth, srcPixels, tempPixels);

            for (int i = dstWidth - 1; i >= 0; i--) {
                int sampleLocation = i;
                final int max = horizontalSubsamplingData.arrN[i];

                float sample0 = 0.0f;
                int index = i * horizontalSubsamplingData.numContributors;
                for (int j = max - 1; j >= 0; j--) {
                    float arrWeight = horizontalSubsamplingData.arrWeight[index];
                    int pixelIndex = horizontalSubsamplingData.arrPixel[index];

                    sample0 += (srcPixels[pixelIndex] & 0xff) * arrWeight;
                    index++;
                }

                workPixels[k][sampleLocation] = toByte(sample0);
            }
        }
    }

    private byte toByte(float f) {
        if (f < 0) return 0;
        if (f > MAX_CHANNEL_VALUE) return (byte) MAX_CHANNEL_VALUE;
        return (byte) (f + 0.5f); // add 0.5 same as Math.round
    }

    protected int getResultBufferedImageType(BufferedImage srcImg) {
        if (numComponents == 3) return TYPE_3BYTE_BGR;
        if (numComponents == 4) return TYPE_4BYTE_ABGR;
        if (srcImg.getSampleModel().getDataType() == DataBuffer.TYPE_USHORT) return TYPE_USHORT_GRAY;
        return TYPE_BYTE_GRAY;
    }

    static class SubSamplingData {
        private final int[] arrN; // individual - per row or per column - nr of contributions
        private final int[] arrPixel; // 2Dim: [wid or hei][contrib]
        private final float[] arrWeight; // 2Dim: [wid or hei][contrib]
        private final int numContributors; // the primary index length for the 2Dim arrays : arrPixel and arrWeight

        private SubSamplingData(int[] arrN, int[] arrPixel, float[] arrWeight, int numContributors) {
            this.arrN = arrN;
            this.arrPixel = arrPixel;
            this.arrWeight = arrWeight;
            this.numContributors = numContributors;
        }

        public int getNumContributors() {
            return numContributors;
        }

        public int[] getArrN() {
            return arrN;
        }

        public int[] getArrPixel() {
            return arrPixel;
        }

        public float[] getArrWeight() {
            return arrWeight;
        }
    }

    /**
     * Lanczos resampling filter
     * 
     * Algorithm reference: https://en.wikipedia.org/wiki/Lanczos_resampling
     * 
     * @author ligang.yao
     */
    public static class LanczosFilter {

        private final float PI = (float) Math.PI;
        private final float a;

        /**
         * @param size
         *            shrink image: typically 2 or 3
         *            enlarge image: should >= 6, suggested: 8
         */
        public LanczosFilter(int size) {
            this.a = size;
        }

        public final float apply(float x) {
            if (x < 0) {
                x = -x;
            }

            if (x < 1e-16) return 1;
            if (x >= a) return 0;

            float pix = x * PI;
            float pix_a = pix / a;
            return (float) (Math.sin(pix) * Math.sin(pix_a) / pix / pix_a);
        }

        public float getSamplingRadius() {
            return a;
        }
    }
}
