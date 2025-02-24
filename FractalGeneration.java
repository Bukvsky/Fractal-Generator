package zad1;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class FractalGeneration implements Callable<BufferedImage> {
    private final int size;
    private final String fractalType;

    public FractalGeneration(int size, String fractalType) {
        this.size = size;
        this.fractalType = fractalType;
    }
    @Override
    public BufferedImage call() {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        switch (fractalType) {
            case "Mandelbrot":
                generateMandelbrot(image);
                break;
            case "Julia":
                generateJulia(image);
                break;
            case "Burning Ship":
                generateBurningShip(image);
                break;
            default:
                throw new IllegalArgumentException("Unknown fractal type: " + fractalType);

        }
        return image;
    }

    private void generateMandelbrot(BufferedImage image) {
        int size = image.getWidth();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double zx = (x - size / 2.0) * 4.0 / size;
                double zy = (y - size / 2.0) * 4.0 / size;

                int iter = mandelbrot(zx, zy);

                int color = iter | (iter << 8);
                image.setRGB(x, y, color);
            }
        }
    }

    private int mandelbrot(double zx, double zy) {
        double cX = zx;
        double cY = zy;
        int maxIter = 255;
        int iter;

        for (iter = 0; iter < maxIter; iter++) {
            double tmp = zx * zx - zy * zy + cX;
            zy = 2.0 * zx * zy + cY;
            zx = tmp;

            if (zx * zx + zy * zy > 4.0) break;
        }
        return iter;
    }


    private void generateJulia(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        double zoom = 200;
        double moveX = width / 2;
        double moveY = height / 2;
        double cRe = -0.7;
        double cIm = 0.27015;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double zx = 1.5 * (x - moveX) / zoom;
                double zy = (y - moveY) / zoom;
                int iteration = 0;
                int maxIterations = 500;

                while (zx * zx + zy * zy < 4 && iteration < maxIterations) {
                    double tmp = zx * zx - zy * zy + cRe;
                    zy = 2.0 * zx * zy + cIm;
                    zx = tmp;
                    iteration++;
                }

                int color = iteration == maxIterations ? 0 : (iteration | (iteration << 8));
                image.setRGB(x, y, color);
            }
        }
    }

    private void generateBurningShip(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        double xMin = -2.0, xMax = 1.5;
        double yMin = -2.0, yMax = 1.0;
        int maxIterations = 1000;

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                double x0 = xMin + px * (xMax - xMin) / width;
                double y0 = yMin + py * (yMax - yMin) / height;

                double x = 0.0, y = 0.0;
                int iteration = 0;

                while (x * x + y * y <= 4 && iteration < maxIterations) {
                    double tempX = x * x - y * y + x0;
                    y = Math.abs(2 * x * y) + y0;
                    x = Math.abs(tempX);
                    iteration++;
                }

                int color = iteration == maxIterations ? 0x000000 : Color.HSBtoRGB((float) iteration / maxIterations, 1, 1);
                image.setRGB(px, py, color);
            }
        }
    }



}


