import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class Demo1 extends Component implements ActionListener {

    // ************************************
    // List of the options(Original, Negative); correspond to the cases:
    // ************************************

    String descs[] = { "Original", "Negative", "Rescale", "Random Rescale", "Addition", "Subtraction", "Multiplication",
            "Division", "Not", "And", "Or", "Xor", "Logarithmic Function", "Power Law", "Random Look-Up",
            "Bit-Plane Slicing", "Histogram", "Masks", "Salt and Pepper", "Minimum Filter", "Maximum Filtering",
            "Midpoint Filtering", "Median Filtering", "Mean and Standard Deviation", "Thresholding" };
    private ArrayList<BufferedImage> UndoList = new ArrayList<>();
    int opIndex;
    int lastOp;

    private BufferedImage bi, biFiltered, lab3Img; // the input image saved as bi;
    int w, h;

    public Demo1() {
        try {
            bi = ImageIO.read(new File("BaboonRGB.bmp"));
            lab3Img = ImageIO.read(new File("Baboon.bmp"));
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
                UndoList.add(biFiltered);
            }
        } catch (IOException e) { // deal with the situation that the image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }

    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = { "bmp", "gif", "jpeg", "jpg", "png" };
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) {
        opIndex = i;
    }

    public void paint(Graphics g) { // Repaint will call this function so the image will change.
        filterImage();

        g.drawImage(biFiltered, 0, 0, null);
    }

    // ************************************
    // Convert the Buffered Image to Array
    // ************************************
    private static int[][][] convertToArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                result[x][y][0] = a;
                result[x][y][1] = r;
                result[x][y][2] = g;
                result[x][y][3] = b;
            }
        }
        return result;
    }

    // ************************************
    // Convert the Array to BufferedImage
    // ************************************
    public BufferedImage convertToBimage(int[][][] TmpArray) {

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                // set RGB value

                int p = (a << 24) | (r << 16) | (g << 8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }

    // ************************************
    // Example: Image Negative
    // ************************************
    public BufferedImage ImageNegative(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg); // Convert the image to array

        // Image Negative Operation:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray[x][y][1] = 255 - ImageArray[x][y][1]; // r
                ImageArray[x][y][2] = 255 - ImageArray[x][y][2]; // g
                ImageArray[x][y][3] = 255 - ImageArray[x][y][3]; // b
            }
        }

        UndoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray); // Convert the array to BufferedImage
    }

    // ************************************
    // Your turn now: Add more function below
    // ************************************

    // LAB 2 RESCALING
    public BufferedImage rescale(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = ImageArray1;

        JFrame frame = new JFrame();
        String rescaleText = JOptionPane.showInputDialog(frame, "Enter the rescale value"); // get values for
                                                                                            // rescaling
        String shiftText = JOptionPane.showInputDialog(frame, "Enter the shift value");

        Float rescale = Float.valueOf(rescaleText);
        int shift = Integer.parseInt(shiftText);
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        if (rescale > 2) { // validation
            rescale = (float) 2.0;
        } else if (rescale < 0) {
            rescale = (float) 0.0;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray2[x][y][1] = (int) (rescale * (ImageArray1[x][y][1] + shift)); // r scaling and shifting
                                                                                         // without finding the min and
                                                                                         // max values
                ImageArray2[x][y][2] = (int) (rescale * (ImageArray1[x][y][2] + shift)); // g
                ImageArray2[x][y][3] = (int) (rescale * (ImageArray1[x][y][3] + shift)); // b

                if (ImageArray2[x][y][1] < 0) { // assign min and max values
                    ImageArray2[x][y][1] = 0;
                }
                if (ImageArray2[x][y][2] < 0) {
                    ImageArray2[x][y][2] = 0;
                }
                if (ImageArray2[x][y][3] < 0) {
                    ImageArray2[x][y][3] = 0;
                }
                if (ImageArray2[x][y][1] > 255) {
                    ImageArray2[x][y][1] = 255;
                }
                if (ImageArray2[x][y][2] > 255) {
                    ImageArray2[x][y][2] = 255;
                }
                if (ImageArray2[x][y][3] > 255) {
                    ImageArray2[x][y][3] = 255;
                }
            }
        }
        timg = convertToBimage(ImageArray2);

        UndoList.add(timg);
        return timg;

    }

    // random rescaling and shifting

    public BufferedImage randomScaling(BufferedImage timg) {
        Random rand = new Random();
        int[][][] ImageArray = convertToArray(timg);
        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = (int) ImageArray[x][y][1] + rand.nextInt(255); // r randomising pixel values
                ImageArray[x][y][2] = (int) ImageArray[x][y][2] + rand.nextInt(255); // g
                ImageArray[x][y][3] = (int) ImageArray[x][y][3] + rand.nextInt(255); // b
            }
        }

        int rescale = 2;
        int shift = 3;

        int rmin = rescale * (ImageArray[0][0][1] + shift); // initilization of random rescaling
        int rmax = rmin;
        int gmin = rescale * (ImageArray[0][0][2] + shift);
        int gmax = gmin;
        int bmin = rescale * (ImageArray[0][0][3] + shift);
        int bmax = bmin;

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = rescale * (ImageArray[x][y][1] + shift); // r
                ImageArray[x][y][2] = rescale * (ImageArray[x][y][2] + shift); // g
                ImageArray[x][y][3] = rescale * (ImageArray[x][y][3] + shift); // b
                if (rmin > ImageArray[x][y][1]) {
                    rmin = ImageArray[x][y][1];
                }
                if (gmin > ImageArray[x][y][2]) { // find the min and max for all pixel values
                    gmin = ImageArray[x][y][2];
                }
                if (bmin > ImageArray[x][y][3]) {
                    bmin = ImageArray[x][y][3];
                }
                if (rmax < ImageArray[x][y][1]) {
                    rmax = ImageArray[x][y][1];
                }
                if (gmax < ImageArray[x][y][2]) {
                    gmax = ImageArray[x][y][2];
                }
                if (bmax < ImageArray[x][y][3]) {
                    bmax = ImageArray[x][y][3];
                }
            }
        }

        for (int y = 0; y < timg.getHeight(); y++) { // image subtraction
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = 255 * (ImageArray[x][y][1] - rmin) / (rmax - rmin);
                ImageArray[x][y][2] = 255 * (ImageArray[x][y][2] - gmin) / (gmax - gmin);
                ImageArray[x][y][3] = 255 * (ImageArray[x][y][3] - bmin) / (bmax - bmin);
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;

    }

    // LAB 3 ARITHMATIC AND BOOLEAN OPERATORS
    // Addition used for superimposition
    public BufferedImage Addition(BufferedImage timg, BufferedImage timg2) {
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[ImageArray.length][ImageArray[0].length][4];

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                outImg[x][y][1] = (ImageArray[x][y][1] + ImageArray2[x][y][1]); // addition of pixels
                outImg[x][y][2] = (ImageArray[x][y][2] + ImageArray2[x][y][2]);
                outImg[x][y][3] = (ImageArray[x][y][3] + ImageArray2[x][y][3]);
            }
        }
        timg = convertToBimage(outImg);
        UndoList.add(timg);
        return timg;
    }

    // Subtraction used for finding differences
    public BufferedImage Subtraction(BufferedImage timg, BufferedImage timg2) {
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[ImageArray.length][ImageArray[0].length][4];

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                outImg[x][y][1] = (ImageArray[x][y][1] - ImageArray2[x][y][1]); // subtraction of pixels
                outImg[x][y][2] = (ImageArray[x][y][2] - ImageArray2[x][y][2]);
                outImg[x][y][3] = (ImageArray[x][y][3] - ImageArray2[x][y][3]);
            }
        }

        timg = convertToBimage(outImg);
        UndoList.add(timg);
        return timg;
    }

    // Multiplication
    public BufferedImage Multiplication(BufferedImage timg, BufferedImage timg2) {
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[ImageArray.length][ImageArray[0].length][4];

        System.out.println("HERE" + ImageArray2[2][2][1]);

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {

                outImg[x][y][1] = ImageArray[x][y][1] * (ImageArray2[x][y][1]);
                outImg[x][y][2] = ImageArray[x][y][2] * (ImageArray2[x][y][2]);
                outImg[x][y][3] = ImageArray[x][y][3] * (ImageArray2[x][y][3]);
            }
        }

        timg = convertToBimage(outImg);
        UndoList.add(timg);
        return timg;
    }

    // Division fractional changes between images
    public BufferedImage Division(BufferedImage timg, BufferedImage timg2) {
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[ImageArray.length][ImageArray.length][4];

        System.out.println(ImageArray2[2][2][1]);

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                if (ImageArray[x][y][1] != 0 && ImageArray2[x][y][1] != 0) { // dividing by 0 gives a math error
                    outImg[x][y][1] = ImageArray[x][y][1] / (ImageArray2[x][y][1]);
                }
                if (ImageArray[x][y][2] != 0 && ImageArray2[x][y][2] != 0) {
                    outImg[x][y][2] = ImageArray[x][y][2] / (ImageArray2[x][y][2]);
                }
                if (ImageArray[x][y][3] != 0 && ImageArray2[x][y][3] != 0) {
                    outImg[x][y][3] = ImageArray[x][y][3] / (ImageArray2[x][y][3]);
                }
            }
        }

        timg = convertToBimage(outImg);
        UndoList.add(timg);
        return timg;
    }

    // Not
    public BufferedImage Not(BufferedImage timg) {
        int[][][] ImageArray = convertToArray(timg);

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                int r = ImageArray[x][y][1];
                int g = ImageArray[x][y][2];
                int b = ImageArray[x][y][3];
                ImageArray[x][y][1] = (~r) & 0xFF;
                ImageArray[x][y][2] = (~g) & 0xFF; // reverse of each pixel value result is kept in the lowest byte
                ImageArray[x][y][3] = (~b) & 0xFF;
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;
    }

    // And
    public BufferedImage And(BufferedImage timg, BufferedImage timg2) {
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg2);

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = (ImageArray[x][y][1] & ImageArray2[x][y][1]) & 0xFF;
                ImageArray[x][y][2] = (ImageArray[x][y][2] & ImageArray2[x][y][2]) & 0xFF;
                ImageArray[x][y][3] = (ImageArray[x][y][3] & ImageArray2[x][y][3]) & 0xFF;
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;
    }

    // Or
    public BufferedImage Or(BufferedImage timg, BufferedImage timg2) {
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg2);

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = (ImageArray[x][y][1] | ImageArray2[x][y][1]) & 0xFF;
                ImageArray[x][y][2] = (ImageArray[x][y][2] | ImageArray2[x][y][2]) & 0xFF;
                ImageArray[x][y][3] = (ImageArray[x][y][3] | ImageArray2[x][y][3]) & 0xFF;
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;
    }

    // XOR
    public BufferedImage Xor(BufferedImage timg, BufferedImage timg2) {
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg2);

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = (ImageArray[x][y][1] ^ ImageArray2[x][y][1]) & 0xFF;
                ImageArray[x][y][2] = (ImageArray[x][y][2] ^ ImageArray2[x][y][2]) & 0xFF;
                ImageArray[x][y][3] = (ImageArray[x][y][3] ^ ImageArray2[x][y][3]) & 0xFF;
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;
    }

    // LAB 4
    // Logarithmic Function used to stretch/compress image
    public BufferedImage Log(BufferedImage timg) {

        int[][][] ImageArray = convertToArray(timg);
        double c = 255 / (Math.log(256));

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {

                ImageArray[x][y][1] = (int) (c * Math.log(ImageArray[x][y][1])); // using log function
                ImageArray[x][y][2] = (int) (c * Math.log(ImageArray[x][y][2]));
                ImageArray[x][y][3] = (int) (c * Math.log(ImageArray[x][y][3]));
            }
        }

        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;
    }

    // Power Law Function
    public BufferedImage Power(BufferedImage timg) {
        int[][][] ImageArray = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r, g, b;
        double Y = 0.25;
        double c = 255 / Math.pow(255, Y);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = (int) (c * Math.pow(ImageArray[x][y][1], Y)); // power law formula
                g = (int) (c * Math.pow(ImageArray[x][y][2], Y));
                b = (int) (c * Math.pow(ImageArray[x][y][3], Y));

                ImageArray[x][y][1] = r;
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;

    }

    // Random Look-Up

    public BufferedImage RandomLookUp(BufferedImage timg) {
        int[][][] ImageArray = convertToArray(timg);

        int[] lut = new int[256];
        Random rnd = new Random();

        for (int i = 0; i < lut.length; i++) {
            lut[i] = rnd.nextInt(255);
        }

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = lut[ImageArray[x][y][1]];
                ImageArray[x][y][2] = lut[ImageArray[x][y][2]];
                ImageArray[x][y][3] = lut[ImageArray[x][y][3]];
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg; // display images
    }

    // Bit-Plane Slicing
    // using a bit plane of 3
    public BufferedImage BitPlane(BufferedImage timg) {
        int[][][] ImageArray = convertToArray(timg);
        int bit = 3;

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                ImageArray[x][y][1] = ((ImageArray[x][y][1] >> bit) & 1) * 255; // shift each pixel value to the right
                                                                                // by 3, "& 1" removes lowest bit
                ImageArray[x][y][2] = ((ImageArray[x][y][2] >> bit) & 1) * 255; // multiply value by 255 to rescale it
                                                                                // between 255 and 0.
                ImageArray[x][y][3] = ((ImageArray[x][y][3] >> bit) & 1) * 255;
            }
        }

        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg; // display images
    }

    // LAB 5
    // Histogram
    private BufferedImage Histogram(BufferedImage timg) {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        double pixel_count = width * height;

        double[] rHistogram = new double[256];
        double[] gHistogram = new double[256];
        double[] bHistogram = new double[256];

        double[] rNormHist = new double[256];
        double[] gNormHist = new double[256];
        double[] bNormHist = new double[256];

        double[] c_rNormHist = new double[256];
        double[] c_gNormHist = new double[256];
        double[] c_bNormHist = new double[256];

        double[] R = new double[256];
        double[] G = new double[256];
        double[] B = new double[256];

        double c_R = 0;
        double c_G = 0;
        double c_B = 0;

        for (int i = 0; i < 256; i++) { // initialisation
            rHistogram[i] = 0;
            gHistogram[i] = 0;
            bHistogram[i] = 0;

            rNormHist[i] = 0;
            gNormHist[i] = 0;
            bNormHist[i] = 0;

            c_rNormHist[i] = 0;
            c_gNormHist[i] = 0;
            c_bNormHist[i] = 0;

            R[i] = 0;
            G[i] = 0;
            B[i] = 0;
        }

        // binning
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = ImageArray[x][y][1];
                int g = ImageArray[x][y][2];
                int b = ImageArray[x][y][3];
                rHistogram[r]++;
                gHistogram[g]++;
                bHistogram[b]++;
            }
        }

        // normalize
        for (int i = 0; i < 256; i++) {
            rNormHist[i] = (rHistogram[i] / pixel_count);
            gNormHist[i] = (gHistogram[i] / pixel_count);
            bNormHist[i] = (bHistogram[i] / pixel_count);
        }

        // cumalate results (Equalization)
        for (int i = 0; i < 256; i++) {
            c_R += rNormHist[i];
            c_G += gNormHist[i];
            c_B += bNormHist[i];
            c_rNormHist[i] = c_R;
            c_gNormHist[i] = c_G;
            c_bNormHist[i] = c_B;
        }

        // multiply cumulative by 255 (new grey values)
        for (int i = 0; i < 256; i++) {
            R[i] = Math.round(c_rNormHist[i] * 255);
            G[i] = Math.round(c_gNormHist[i] * 255);
            B[i] = Math.round(c_bNormHist[i] * 255);
        }

        // map pixel values to the new grey values
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray[x][y][1] = (int) R[ImageArray[x][y][1]];
                ImageArray[x][y][2] = (int) G[ImageArray[x][y][2]];
                ImageArray[x][y][3] = (int) B[ImageArray[x][y][3]];
            }
        }

        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;
    }

    public BufferedImage Masks(BufferedImage timg) {
        Scanner sc = new Scanner(System.in);
        int input;
        float[][] mask = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };

        System.out.println("Which mask... " + "\n" + "1 - Average " + "\n" + "2 - Weighted Average " + "\n"
                + "3 - 4 Neighbour Laplacian " + "\n" + "4 - 8 Neighbour Laplacian " + "\n"
                + "5 - 4 Neighbour Laplacian Enhance" + "\n" + "6 - 8 Neighbour Laplacian Enhance" + "\n"
                + "7 - Roberts 1 (Correlation)" + "\n" + "8 - Roberts 1 (Convolution)" + "\n"
                + "9 - Roberts 2 (Correlation)" + "\n" + "10 - Roberts 2 (Convolution)" + "\n"
                + "11 - Sobel X (Correlation)" + "\n" + "12 - Sobel X (Convolution)" + "\n"
                + "13 - Sobel Y (Correlation)" + "\n" + "14 - Sobel Y (Convolution)");

        input = sc.nextInt();

        switch (input) {
            case 1: // Average
                System.out.println("Average...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        mask[i][j] = (float) 1 / 9; // matrix is symmetrical so convolution = correlation

                    }
                }

                return correlate(mask, timg, 0);

            case 2: // Weighted Average
                System.out.println("Weighted Average...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 1) {
                            mask[i][j] = 4;
                            mask[i][j] = mask[i][j] * 1 / 16; // matrix is symmetrical so convolution = correlation
                        } else if (i == 1 || j == 1) {
                            mask[i][j] = 2;
                            mask[i][j] = mask[i][j] * 1 / 16;
                        } else {
                            mask[i][j] = 1;
                            mask[i][j] = mask[i][j] * 1 / 16;
                        }
                    }
                }

                return correlate(mask, timg, 0);

            case 3: // 4-Neighbour Laplacian
                System.out.println("4-Neighbour Laplacian...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 1) {
                            mask[i][j] = 4;
                        } else if (i == 1 || j == 1) {
                            mask[i][j] = -1;
                        } else {
                            mask[i][j] = 0;
                        }
                    }
                }

                return correlate(mask, timg, 0); // matrix is symmetrical so convolution = correlation

            case 4: // 8-Neighbour Laplacian
                System.out.println("8-Neighbour Laplacian...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 1) {
                            mask[i][j] = 8;
                        } else {
                            mask[i][j] = -1;
                        }
                    }
                }
                return correlate(mask, timg, 0);

            case 5: // 4-Neighbour Laplacian Enhanced
                System.out.println("4-Neighbour Laplacian Enhanced...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 1) {
                            mask[i][j] = 5;
                        } else if (i == 1 || j == 1) {
                            mask[i][j] = -1;
                        } else {
                            mask[i][j] = 0;
                        }
                    }
                }
                return correlate(mask, timg, 0); // matrix is symmetrical so convolution = correlation

            case 6: // 8-Neighbour Laplacian Enhanced
                System.out.println("8-Neighbour Laplacian Enhanced...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 1) {
                            mask[i][j] = 9;
                        } else {
                            mask[i][j] = -1;
                        }
                    }
                }
                return correlate(mask, timg, 0); // matrix is symmetrical so convolution = correlation

            case 7: // Roberts Mask 1 (Correlation)
                System.out.println("Roberts Mask 1 (Correlation)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 2) {
                            mask[i][j] = -1;
                        }
                        if (i == 2 && j == 1) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return correlate(mask, timg, 1);

            case 8: // Roberts Mask 1 (Convolution)
                System.out.println("Roberts Mask 1 (Convolution)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 2) {
                            mask[i][j] = -1;
                        }
                        if (i == 2 && j == 1) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return convolution(mask, timg, 1);

            case 9: // Roberts Mask 2 (Correlation)
                System.out.println("Roberts Mask 2 (Correlation)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 1) {
                            mask[i][j] = -1;
                        }
                        if (i == 2 && j == 2) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return correlate(mask, timg, 1);

            case 10: // Roberts Mask 2 (Convolution)
                System.out.println("Roberts Mask 2 (Convolution)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 1 && j == 1) {
                            mask[i][j] = -1;
                        }
                        if (i == 2 && j == 2) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return convolution(mask, timg, 1);

            case 11: // Sobel X (Correlation)
                System.out.println("Sobel X (Correlation)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 0 && j == 0) {
                            mask[i][j] = -1;
                        }
                        if (i == 1 && j == 0) {
                            mask[i][j] = -2;
                        }
                        if (i == 2 && j == 0) {
                            mask[i][j] = -1;
                            mask[j][i] = 1;
                        }
                        if (i == 1 && j == 2) {
                            mask[i][j] = 2;
                        }
                        if (i == 2 && j == 2) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return correlate(mask, timg, 1);

            case 12: // Sobel X (Convolution)
                System.out.println("Sobel X (Convolution)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 0 && j == 0) {
                            mask[i][j] = -1;
                        }
                        if (i == 1 && j == 0) {
                            mask[i][j] = -2;
                        }
                        if (i == 2 && j == 0) {
                            mask[i][j] = -1;
                            mask[j][i] = 1;
                        }
                        if (i == 1 && j == 2) {
                            mask[i][j] = 2;
                        }
                        if (i == 2 && j == 2) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return convolution(mask, timg, 1);

            case 13: // Sobel Y (Correlation)
                System.out.println("Sobel Y (Correlation)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 0 && j == 0) {
                            mask[i][j] = -1;
                        }
                        if (i == 0 && j == 1) {
                            mask[i][j] = -2;
                        }
                        if (i == 0 && j == 2) {
                            mask[i][j] = -1;
                            mask[j][i] = 1;
                        }
                        if (i == 2 && j == 1) {
                            mask[i][j] = 2;
                        }
                        if (i == 2 && j == 2) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return correlate(mask, timg, 1);

            case 14: // Sobel Y (Convolution)
                System.out.println("Sobel Y (Convolution)...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (i == 0 && j == 0) {
                            mask[i][j] = -1;
                        }
                        if (i == 0 && j == 1) {
                            mask[i][j] = -2;
                        }
                        if (i == 0 && j == 2) {
                            mask[i][j] = -1;
                            mask[j][i] = 1;
                        }
                        if (i == 2 && j == 1) {
                            mask[i][j] = 2;
                        }
                        if (i == 2 && j == 2) {
                            mask[i][j] = 1;
                        }
                    }
                }
                return convolution(mask, timg, 1);

        }

        return timg;

    }

    private BufferedImage correlate(float[][] Mask, BufferedImage timg, int option) {
        Scanner sc = new Scanner(System.in);
        int r, g, b;
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
        int width = timg.getWidth();
        int height = timg.getHeight();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                r = 0;
                g = 0;
                b = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        r = (int) (r + Mask[1 + s][1 + t] * ImageArray[x + s][y + t][1]); // correlation formula
                        g = (int) (g + Mask[1 + s][1 + t] * ImageArray[x + s][y + t][2]);
                        b = (int) (b + Mask[1 + s][1 + t] * ImageArray[x + s][y + t][3]);

                    }
                }
                if (option == 0) {
                    ImageArray2[x][y][0] = ImageArray[x][y][0];
                    ImageArray2[x][y][1] = checkBoundary(r);
                    ImageArray2[x][y][2] = checkBoundary(g);
                    ImageArray2[x][y][3] = checkBoundary(b);
                } else { // absolute value conversion
                    ImageArray2[x][y][0] = ImageArray[x][y][0];
                    ImageArray2[x][y][1] = Math.abs(r);
                    ImageArray2[x][y][2] = Math.abs(g);
                    ImageArray2[x][y][3] = Math.abs(b);
                }
            }

        }
        timg = convertToBimage(ImageArray2);
        UndoList.add(timg);
        return timg;
    }

    private BufferedImage convolution(float[][] Mask, BufferedImage timg, int option) {
        Scanner sc = new Scanner(System.in);
        int r, g, b;
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
        int width = timg.getWidth();
        int height = timg.getHeight();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                r = 0;
                g = 0;
                b = 0;

                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        r = (int) (r + Mask[1 - s][1 - t] * ImageArray[x + s][y + t][1]); // correlation formula
                        g = (int) (g + Mask[1 - s][1 - t] * ImageArray[x + s][y + t][2]);
                        b = (int) (b + Mask[1 - s][1 - t] * ImageArray[x + s][y + t][3]);

                    }
                }

                if (option == 0) {
                    ImageArray2[x][y][0] = ImageArray[x][y][0];
                    ImageArray2[x][y][1] = checkBoundary(r);
                    ImageArray2[x][y][2] = checkBoundary(g);
                    ImageArray2[x][y][3] = checkBoundary(b);
                } else { // absolute value conversion
                    ImageArray2[x][y][0] = ImageArray[x][y][0];
                    ImageArray2[x][y][1] = Math.abs(r);
                    ImageArray2[x][y][2] = Math.abs(g);
                    ImageArray2[x][y][3] = Math.abs(b);
                }
            }
        }

        timg = convertToBimage(ImageArray2);
        UndoList.add(timg);
        return timg;
    }

    // Salt and Pepper Noise
    public BufferedImage SaltAndPepper(BufferedImage timg) {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double random = Math.random() * 1;

                if (random < 0.05) {
                    ImageArray[x][y][0] = 255;
                    ImageArray[x][y][1] = 0;
                    ImageArray[x][y][2] = 0;
                    ImageArray[x][y][3] = 0;
                } else if (random > 0.95) {
                    ImageArray[x][y][0] = 255;
                    ImageArray[x][y][1] = 255;
                    ImageArray[x][y][2] = 255;
                    ImageArray[x][y][3] = 255;
                }
            }
        }
        timg = convertToBimage(ImageArray);
        UndoList.add(timg);
        return timg;
    }

    // Minimum Filtering
    private BufferedImage MinFilter(BufferedImage timg) {
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k;
        for (int y = 1; y < timg.getHeight() - 1; y++) {
            for (int x = 1; x < timg.getWidth() - 1; x++) {
                k = 0; // index
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        rWindow[k] = ImageArray1[x + s][y + t][1]; // take out pixel values and place it in
                                                                   // corresponding vector
                        gWindow[k] = ImageArray1[x + s][y + t][2];
                        bWindow[k] = ImageArray1[x + s][y + t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow); // sort each vector
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
                ImageArray2[x][y][1] = checkBoundary(rWindow[0]); // uses the minimum pixel value when outputting image
                ImageArray2[x][y][2] = checkBoundary(gWindow[0]);
                ImageArray2[x][y][3] = checkBoundary(bWindow[0]);
            }
        }
        timg = convertToBimage(ImageArray2);
        UndoList.add(timg);
        return timg;
    }

    // Maximum Filtering
    private BufferedImage MaxFilter(BufferedImage timg) {
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k;
        for (int y = 1; y < timg.getHeight() - 1; y++) {
            for (int x = 1; x < timg.getWidth() - 1; x++) {
                k = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        rWindow[k] = ImageArray1[x + s][y + t][1];
                        gWindow[k] = ImageArray1[x + s][y + t][2];
                        bWindow[k] = ImageArray1[x + s][y + t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
                ImageArray2[x][y][1] = checkBoundary(rWindow[rWindow.length - 1]); // uses the max pixel value when
                                                                                   // outputting image
                ImageArray2[x][y][2] = checkBoundary(gWindow[rWindow.length - 1]);
                ImageArray2[x][y][3] = checkBoundary(bWindow[rWindow.length - 1]);
            }
        }
        timg = convertToBimage(ImageArray2);
        UndoList.add(timg);
        return timg; // display images
    }

    // Mid-Point Filtering
    private BufferedImage MidPointFiltering(BufferedImage timg) {
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k;
        for (int y = 1; y < timg.getHeight() - 1; y++) {
            for (int x = 1; x < timg.getWidth() - 1; x++) {
                k = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        rWindow[k] = ImageArray1[x + s][y + t][1];
                        gWindow[k] = ImageArray1[x + s][y + t][2];
                        bWindow[k] = ImageArray1[x + s][y + t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                int r_midpoint = (rWindow[0] + rWindow[(rWindow.length - 1) / 2]); // find the midpoint for each vector
                int g_midpoint = (gWindow[0] + gWindow[(gWindow.length - 1) / 2]);
                int b_midpoint = (bWindow[0] + bWindow[(bWindow.length - 1) / 2]);
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
                ImageArray2[x][y][1] = checkBoundary(r_midpoint); // use the midpoint value when outputting the image
                ImageArray2[x][y][2] = checkBoundary(g_midpoint);
                ImageArray2[x][y][3] = checkBoundary(b_midpoint);
            }
        }

        timg = convertToBimage(ImageArray2);
        UndoList.add(timg);
        return timg;
    }

    // Median Filtering
    private BufferedImage MedianFiltering(BufferedImage timg) {
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k;
        for (int y = 1; y < timg.getHeight() - 1; y++) {
            for (int x = 1; x < timg.getWidth() - 1; x++) {
                k = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        rWindow[k] = ImageArray1[x + s][y + t][1];
                        gWindow[k] = ImageArray1[x + s][y + t][2];
                        bWindow[k] = ImageArray1[x + s][y + t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
                ImageArray2[x][y][1] = checkBoundary(rWindow[4]); // use median value when outputting the image
                ImageArray2[x][y][2] = checkBoundary(gWindow[4]);
                ImageArray2[x][y][3] = checkBoundary(bWindow[4]);
            }
        }

        timg = convertToBimage(ImageArray2);
        UndoList.add(timg);
        return timg; // display images
    }

    // LAB 8
    // MEAN AND STANDARD DEVIATION
    private void MeanAndSD(BufferedImage timg) {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int pixelCount = width * height;

        double sumR = 0;
        double sumG = 0;
        double sumB = 0;

        double rVarSum = 0;
        double gVarSum = 0;
        double bVarSum = 0;

        double[] rHisto = new double[256];
        double[] gHisto = new double[256];
        double[] bHisto = new double[256];

        for (int i = 0; i < 256; i++) { // initialising
            rHisto[i] = 0;
            gHisto[i] = 0;
            bHisto[i] = 0;
        }

        for (int y = 0; y < height; y++) { // binning
            for (int x = 0; x < width; x++) {
                int r = ImageArray[x][y][1];
                int g = ImageArray[x][y][2];
                int b = ImageArray[x][y][3];
                rHisto[r]++;
                gHisto[g]++;
                bHisto[b]++;
            }
        }

        for (int i = 0; i < 256; i++) { // calculate the sum of R,G,B values
            sumR += rHisto[i] * i;
            sumG += gHisto[i] * i;
            sumB += bHisto[i] * i;
        }

        System.out.println("Mean of R: " + sumR / pixelCount); // display the mean
        System.out.println("Mean of G: " + sumG / pixelCount);
        System.out.println("Mean of B: " + sumB / pixelCount);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = ImageArray[x][y][1];
                double g = ImageArray[x][y][2];
                double b = ImageArray[x][y][3];

                rVarSum += Math.pow(r - (sumR / pixelCount), 2); // calculate the differnece between the mean and pixel
                                                                 // value
                gVarSum += Math.pow(g - (sumG / pixelCount), 2);
                bVarSum += Math.pow(b - (sumB / pixelCount), 2);
            }
        }

        double rVar = rVarSum / pixelCount; // varience
        double gVar = gVarSum / pixelCount;
        double bVar = bVarSum / pixelCount;

        System.out.println("Red SD: " + Math.sqrt(rVar));
        System.out.println("Blue SD: " + Math.sqrt(gVar));
        System.out.println("Green SD: " + Math.sqrt(bVar));

        return;

    }

    // LAB 8
    // THRESHOLDING makes the image easier to analyse
    public BufferedImage Thresholding(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

        int threshV = 120;// variation from 0-255

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int grey_scaled = (ImageArray[x][y][1] + ImageArray[x][y][2] + ImageArray[x][y][3]) / 3;

                if (grey_scaled > threshV) {
                    grey_scaled = 255;
                } else {
                    grey_scaled = 0;
                }

                ImageArray[x][y][1] = grey_scaled;
                ImageArray[x][y][2] = grey_scaled;
                ImageArray[x][y][3] = grey_scaled;

            }
        }

        return convertToBimage(ImageArray);
    }

    int checkBoundary(int colourValue) {
        if (colourValue > 255)
            return 255;
        if (colourValue < 0)
            return 0;
        else
            return colourValue;
    }

    // ************************************
    // You need to register your function here
    // ************************************
    public void filterImage() {

        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0:
                biFiltered = bi; /* original */
                return;
            case 1:
                biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            case 2:
                biFiltered = rescale(bi); // LAB 2
                return;
            case 3:
                biFiltered = randomScaling(bi); // LAB 2
                return;
            case 4:
                biFiltered = Addition(bi, lab3Img); // LAB 3
                return;
            case 5:
                biFiltered = Subtraction(bi, lab3Img); // LAB 3
                return;
            case 6:
                biFiltered = Multiplication(bi, lab3Img); // LAB 3
                return;
            case 7:
                biFiltered = Division(bi, lab3Img); // LAB 3
                return;
            case 8:
                biFiltered = Not(bi); // LAB 3
                return;
            case 9:
                biFiltered = And(bi, lab3Img); // LAB 3
                return;
            case 10:
                biFiltered = Or(bi, lab3Img); // LAB 3
                return;
            case 11:
                biFiltered = Xor(bi, lab3Img); // LAB 3
                return;
            case 12:
                biFiltered = Log(bi); // LAB 4
                return;
            case 13:
                biFiltered = Power(bi); // LAB 4
                return;
            case 14:
                biFiltered = RandomLookUp(bi); // LAB 4
                return;
            case 15:
                biFiltered = BitPlane(bi); // LAB 4
                return;
            case 16:
                biFiltered = Histogram(bi); // LAB 5
                return;
            case 17:
                biFiltered = Masks(bi); // Lab 6
                return;
            case 18:
                biFiltered = SaltAndPepper(bi); // LAB 7
                return;
            case 19:
                biFiltered = MinFilter(bi); // LAB 7
                return;
            case 20:
                biFiltered = MaxFilter(bi); // LAB 7
                return;
            case 21:
                biFiltered = MidPointFiltering(bi); // LAB 7
                return;
            case 22:
                biFiltered = MedianFiltering(bi); // LAB 7
                return;
            case 23:
                MeanAndSD(bi);
                return;
            case 24:
                biFiltered = Thresholding(bi);
                return;

            // ***********************************
            // case 2:
            // return;
            // ************************************

        }

    }

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        if (cb.getActionCommand().equals("SetFilter")) {
            setOpIndex(cb.getSelectedIndex());
            repaint();
        } else if (cb.getActionCommand().equals("Formats")) {
            String format = (String) cb.getSelectedItem();
            File saveFile = new File("savedimage." + format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {
                    ImageIO.write(biFiltered, format, saveFile);
                } catch (IOException ex) {
                }
            }
        }
    };

    // Region of Interest
    private void ROI() {

        int[][][] ImageArray = convertToArray(bi);
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                if ((x < 90 || x > 160 || y < 50 || y > 100)) {

                    ImageArray[x][y][0] = 0;
                    ImageArray[x][y][1] = 0;
                    ImageArray[x][y][2] = 0;
                    ImageArray[x][y][3] = 0;
                }
            }
        }

        bi = convertToBimage(ImageArray);
        return;
    }

    private void Remove() {
        if (UndoList.size() - 1 >= 1) {
            UndoList.remove(UndoList.size() - 1);
            BufferedImage newImage = UndoList.get(UndoList.size() - 1);
            Graphics big = newImage.getGraphics();
            big.drawImage(newImage, 0, 0, null);
            bi = newImage;
            biFiltered = newImage;
            repaint();
        } else {
            System.out.println("NO IMAGES IN LIST");
        }

        return;
    }

    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        Demo1 de = new Demo1();
        f.add("Center", de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();

        JButton ROI = new JButton("ROI"); // display ROI button
        ROI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                de.ROI();
            }
        });

        JButton undo = new JButton("Undo"); // display Undo button
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                de.Remove();
            }
        });

        f.add("North", panel);
        f.pack();
        f.setVisible(true);

        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(ROI);
        panel.add(undo);
    }
}
