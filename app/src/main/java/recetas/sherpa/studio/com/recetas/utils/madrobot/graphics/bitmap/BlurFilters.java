package recetas.sherpa.studio.com.recetas.utils.madrobot.graphics.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Collection of various blur filters
 * <p>
 * <b>Gaussian Blur</b><br/>
 * Gaussian using the <code>radius</code> of <code>2</code><br/>
 * <img src="../../../../resources/gaussian.png"><br/>
 * 
 * <b>Motion Blur</b><br/>
 * Motion blur with <code>angle</code> 1.0f and <code>distance</code> 5.0f.<br/>
 * <img src="../../../../resources/motionBlur.png"><br/>
 * <b>Box Blur</b><br/>
 * Box blur with <code>hRadius</code> and <code>vRadius</code> of 5.0f. with 1
 * <code>iteration</code><br/>
 * <img src="../../../../resources/boxblur.png"><br/>
 * <b>Maximum</b><br/>
 * <img src="../../../../resources/maximum.png"><br/>
 * <b>Minimum</b><br/>
 * <img src="../../../../resources/minimum.png"><br/>
 * <b>Median</b><br/>
 * <img src="../../../../resources/median.png"><br/>
 * </p>
 * 
 * @author elton.stephen.kent
 * 
 */
public class BlurFilters {

    /**
     * performs a box blur on an image.
     * <p>
     * The horizontal and vertical blurs can be specified separately and a
     * number of iterations can be given which allows an approximation to
     * Gaussian blur.
     * </p>
     *
     * @param view
     * @param hRadius          min:0 max:100
     * @param vRadius          min:0 max:100
     * @param iterations       the number of iterations the blur is performed. min:0 max:10.
     *                         Recommended:1.
     * @param premultiplyAlpha whether to premultiply the alpha channel. Recommended:true
     * @return
     */
    public static Bitmap boxBlur(View view, float hRadius, float vRadius, int iterations, boolean premultiplyAlpha) {

        Bitmap bitmap = getBitmapFromView(view);
        Bitmap.Config outputConfig = bitmap.getConfig();


        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] inPixels = BlurFilters.getPixels(bitmap);
        int[] outPixels = new int[width * height];

        if (premultiplyAlpha)
            premultiply(inPixels, 0, inPixels.length);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        if (premultiplyAlpha)
            unpremultiply(inPixels, 0, inPixels.length);

        return Bitmap.createBitmap(inPixels, width, height, outputConfig);
    }

    /**
     * Blur and transpose a block of ARGB pixels.
     *
     * @param in     the input pixels
     * @param out    the output pixels
     * @param width  the width of the pixel array
     * @param height the height of the pixel array
     * @param radius the radius of blur
     */
    public static void blur(int[] in, int[] out, int width, int height,
                            float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public static void blurFractional(int[] in, int[] out, int width,
                                      int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    /**
     * Premultiply a block of pixels
     */
    public static void premultiply(int[] p, int offset, int length) {
        length += offset;
        for (int i = offset; i < length; i++) {
            int rgb = p[i];
            int a = (rgb >> 24) & 0xff;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            float f = a * (1.0f / 255.0f);
            r *= f;
            g *= f;
            b *= f;
            p[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    /**
     * Premultiply a block of pixels
     */
    public static void unpremultiply(int[] p, int offset, int length) {
        length += offset;
        for (int i = offset; i < length; i++) {
            int rgb = p[i];
            int a = (rgb >> 24) & 0xff;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            if (a != 0 && a != 255) {
                float f = 255.0f / a;
                r *= f;
                g *= f;
                b *= f;
                if (r > 255)
                    r = 255;
                if (g > 255)
                    g = 255;
                if (b > 255)
                    b = 255;
                p[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }

    /**
     * Clamp a value to an interval.
     *
     * @param a the lower clamp threshold
     * @param b the upper clamp threshold
     * @param x the input parameter
     * @return the clamped value
     */
    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    /**
     * Get the pixels for the given bitmap
     *
     * @param bitmap
     * @return
     */
    public static int[] getPixels(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        return pixels;
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}
