package com.aspectgaming.util.image;

import static java.awt.image.BufferedImage.*;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

    public static BufferedImage read(String path) {
        File f = new File(path);
        if (f.exists()) {
            try {
                return ImageIO.read(f);
            } catch (IOException e) {
                System.err.println("Failed reading image file: " + path);
                throw new RuntimeException(e.toString());
            }
        }
        return null;
    }

    public static void save(BufferedImage image, String path) {
        File outputfile = new File(path);
        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            System.err.println("Failed writting image file: " + path);
            throw new RuntimeException(e.toString());
        }
    }

    public static int[] getARGB(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        return image.getRGB(0, 0, w, h, null, 0, w);
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        if (image == null) return null;

        if (width == image.getWidth() && height == image.getHeight()) return image;

        LanczosResampler op = new LanczosResampler(image.getWidth(), image.getHeight(), width, height);
        image = op.filter(image, null);
        return image;
    }

    public static BufferedImage crop(BufferedImage image, int x, int y, int width, int height) {

        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        if (x + width > srcWidth) {
            width = srcWidth - x;
        }

        if (y + height > srcHeight) {
            height = srcHeight - y;
        }

        if (width < 0 || height < 0) return null;

        if (x == 0 && y == 0 && width == srcWidth && height == srcHeight) return image;

        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] argb = image.getRGB(x, y, width, height, null, 0, width);
        ret.setRGB(0, 0, width, height, argb, 0, width);

        return ret;
    }

    /**
     * returns one row (height == 1) of byte packed image data in BGR or AGBR form
     */
    public static byte[] getPixelsBGR(BufferedImage img, int y, int w, byte[] array, int[] temp) {
        final int x = 0;
        final int h = 1;

        int imageType = img.getType();
        Raster raster;
        switch (imageType) {
        case TYPE_3BYTE_BGR:
        case TYPE_4BYTE_ABGR:
        case TYPE_4BYTE_ABGR_PRE:
        case TYPE_BYTE_GRAY:
            raster = img.getRaster();
            raster.getDataElements(x, y, w, h, array);
            break;
        case TYPE_INT_BGR:
            raster = img.getRaster();
            raster.getDataElements(x, y, w, h, temp);
            ints2bytes(temp, array, 0, 1, 2); // bgr --> bgr
            break;
        case TYPE_INT_RGB:
            raster = img.getRaster();
            raster.getDataElements(x, y, w, h, temp);
            ints2bytes(temp, array, 2, 1, 0); // rgb --> bgr
            break;
        case TYPE_INT_ARGB:
        case TYPE_INT_ARGB_PRE:
            raster = img.getRaster();
            raster.getDataElements(x, y, w, h, temp);
            ints2bytes(temp, array, 2, 1, 0, 3); // argb --> abgr
            break;
        case TYPE_CUSTOM: // TODO: works for my icon image loader, but else ???
            img.getRGB(x, y, w, h, temp, 0, w);
            ints2bytes(temp, array, 2, 1, 0, 3); // argb --> abgr
            break;
        default:
            img.getRGB(x, y, w, h, temp, 0, w);
            ints2bytes(temp, array, 2, 1, 0); // rgb --> bgr
            break;
        }

        return array;
    }

    /**
     * converts and copies byte packed BGR or ABGR into the img buffer,
     * the img type may vary (e.g. RGB or BGR, int or byte packed)
     * but the number of components (w/o alpha, w alpha, gray) must match
     *
     * does not unmange the image for all (A)RGN and (A)BGR and gray imaged
     *
     */
    public static void setBGRPixels(byte[] bgrPixels, BufferedImage img, int x, int y, int w, int h) {
        int imageType = img.getType();
        WritableRaster raster = img.getRaster();

        if (imageType == TYPE_3BYTE_BGR || imageType == TYPE_4BYTE_ABGR || imageType == TYPE_4BYTE_ABGR_PRE || imageType == TYPE_BYTE_GRAY) {
            raster.setDataElements(x, y, w, h, bgrPixels);
        } else {
            int[] pixels;
            if (imageType == TYPE_INT_BGR) {
                pixels = bytes2int(bgrPixels, 2, 1, 0); // bgr --> bgr
            } else if (imageType == TYPE_INT_ARGB || imageType == TYPE_INT_ARGB_PRE) {
                pixels = bytes2int(bgrPixels, 3, 0, 1, 2); // abgr --> argb
            } else {
                pixels = bytes2int(bgrPixels, 0, 1, 2); // bgr --> rgb
            }
            if (w == 0 || h == 0) {
                return;
            } else if (pixels.length < w * h) {
                throw new IllegalArgumentException("pixels array must have a length" + " >= w*h");
            }
            if (imageType == TYPE_INT_ARGB || imageType == TYPE_INT_RGB || imageType == TYPE_INT_ARGB_PRE || imageType == TYPE_INT_BGR) {
                raster.setDataElements(x, y, w, h, pixels);
            } else {
                // Unmanages the image
                img.setRGB(x, y, w, h, pixels, 0, w);
            }
        }
    }

    public static void ints2bytes(int[] in, byte[] out, int index1, int index2, int index3) {
        for (int i = 0; i < in.length; i++) {
            int index = i * 3;
            int value = in[i];
            out[index + index1] = (byte) value;
            value = value >> 8;
            out[index + index2] = (byte) value;
            value = value >> 8;
            out[index + index3] = (byte) value;
        }
    }

    public static void ints2bytes(int[] in, byte[] out, int index1, int index2, int index3, int index4) {
        for (int i = 0; i < in.length; i++) {
            int index = i * 4;
            int value = in[i];
            out[index + index1] = (byte) value;
            value = value >> 8;
            out[index + index2] = (byte) value;
            value = value >> 8;
            out[index + index3] = (byte) value;
            value = value >> 8;
            out[index + index4] = (byte) value;
        }
    }

    public static int[] bytes2int(byte[] in, int index1, int index2, int index3) {
        int[] out = new int[in.length / 3];
        for (int i = 0; i < out.length; i++) {
            int index = i * 3;
            int b1 = (in[index + index1] & 0xff) << 16;
            int b2 = (in[index + index2] & 0xff) << 8;
            int b3 = in[index + index3] & 0xff;
            out[i] = b1 | b2 | b3;
        }
        return out;
    }

    public static int[] bytes2int(byte[] in, int index1, int index2, int index3, int index4) {
        int[] out = new int[in.length / 4];
        for (int i = 0; i < out.length; i++) {
            int index = i * 4;
            int b1 = (in[index + index1] & 0xff) << 24;
            int b2 = (in[index + index2] & 0xff) << 16;
            int b3 = (in[index + index3] & 0xff) << 8;
            int b4 = in[index + index4] & 0xff;
            out[i] = b1 | b2 | b3 | b4;
        }
        return out;
    }

    public static BufferedImage convert(BufferedImage src, int bufImgType) {
        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return img;
    }

    public static void setAlpha(byte[] rgba, float alpha) {
        for (int i = 3; i < rgba.length; i = i + 4) {
            rgba[i] = (byte) ((rgba[i] & 0xff) * alpha);
        }
    }

    public static Rectangle trimMargin(String file, int flags, int marginColor, int colorMask) {
        BufferedImage image = ImageUtil.read(file);

        if (image == null) return null;

        int w = image.getWidth();
        int h = image.getHeight();

        int[] data = image.getRGB(0, 0, w, h, null, 0, w);
        Rectangle rect = new Rectangle(0, 0, w, h);

        if ((flags & (1 << 0)) != 0) {
            leftLoop:
            for (int x = 0; x < rect.width; x++) {
                for (int y = 0; y < rect.height; y++) {
                    int color = data[(rect.y + y) * w + rect.x + x];
                    if ((color & colorMask) != marginColor) {
                        rect.x += x;
                        rect.width -= x;
                        break leftLoop;
                    }
                }
            }
        }

        if ((flags & (1 << 1)) != 0) {
            topLoop:
            for (int y = 0; y < rect.height; y++) {
                for (int x = 0; x < rect.width; x++) {
                    int color = data[(rect.y + y) * w + rect.x + x];
                    if ((color & colorMask) != marginColor) {
                        rect.y += y;
                        rect.height -= y;
                        break topLoop;
                    }
                }
            }
        }

        if ((flags & (1 << 2)) != 0) {
            rightLoop:
            for (int x = rect.width - 1; x >= 0; x--) {
                for (int y = 0; y < rect.height; y++) {
                    int color = data[(rect.y + y) * w + rect.x + x];
                    if ((color & colorMask) != marginColor) {
                        rect.width = x + 1;
                        break rightLoop;
                    }
                }
            }
        }

        if ((flags & (1 << 3)) != 0) {
            bottomLoop:
            for (int y = rect.height - 1; y >= 0; y--) {
                for (int x = 0; x < rect.width; x++) {
                    int color = data[(rect.y + y) * w + rect.x + x];
                    if ((color & colorMask) != marginColor) {
                        rect.height = y + 1;
                        break bottomLoop;
                    }
                }
            }
        }

        return rect;
    }
}
