import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageTreatment {

    public void receiveTreatedImage(DataInputStream in, String destFileName) {
        try {
            int sobelLength = in.readInt();
            byte[] sobel = new byte[sobelLength];
            in.readFully(sobel);

            FileOutputStream fos = new FileOutputStream(destFileName);
            fos.write(sobel);
            System.out.println("File is Received");

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFileClientSide(String filename, DataInputStream in, DataOutputStream out) {
        try {

            System.out.println("Sending the File to the Server");

            // Partie de code inspirée de https://www.geeksforgeeks.org/transfer-the-file-client-socket-to-server-socket-in-java/
            int bytes = 0;
            File file = new File(filename);
            FileInputStream fileInputStream = new FileInputStream(file);

            out.writeUTF(filename);
            // Ecrire la taille du fichier
            out.writeLong(file.length());
            byte[] buffer = new byte[4 * 1024];

            // Ecrire le buffer dans out jusqu'a la fin du fichier
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytes);
                out.flush();
            }
            fileInputStream.close();

//            receiveTreatedImage(in);


        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void treatAndResendImage(DataInputStream in, DataOutputStream out) throws Exception {
        try {
            int bytes = 0;
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); // Output stream ou on ecrit notre byte[]

            // Boucle inspirée de https://www.geeksforgeeks.org/transfer-the-file-client-socket-to-server-socket-in-java/
            long size = in.readLong();
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                byteOut.write(buffer, 0, bytes);
                size -= bytes;
            }

            // Recupere le byte[] du output stream et appliquer Sobel dessus
            byte[] image = byteOut.toByteArray();
            byte[] processedImage = sobelFilter(image);

            // Ecris la taille de l'image modifiée ainsi que l'image en byte[]
            out.writeInt(processedImage.length);
            out.write(processedImage);

            byteOut.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fonction qui prends une image en byte[] et qui renvoie l'image traitée(Sobel) en byte[]
    public byte[] sobelFilter(byte[] bytes) throws IOException {
        InputStream is = new ByteArrayInputStream(bytes);
        BufferedImage baseImage = ImageIO.read(is);
        BufferedImage processedImage = process(baseImage);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ImageIO.write(processedImage, "jpg", byteOut);
        return byteOut.toByteArray();
    }


    public static BufferedImage process(BufferedImage image) throws IOException
    {
        System.out.println("applying filter");

        int x = image.getWidth();
        int y = image.getHeight();

        int[][] edgeColors = new int[x][y];
        int maxGradient = -1;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {

                int val00 = getGrayScale(image.getRGB(i - 1, j - 1));
                int val01 = getGrayScale(image.getRGB(i - 1, j));
                int val02 = getGrayScale(image.getRGB(i - 1, j + 1));

                int val10 = getGrayScale(image.getRGB(i, j - 1));
                int val11 = getGrayScale(image.getRGB(i, j));
                int val12 = getGrayScale(image.getRGB(i, j + 1));

                int val20 = getGrayScale(image.getRGB(i + 1, j - 1));
                int val21 = getGrayScale(image.getRGB(i + 1, j));
                int val22 = getGrayScale(image.getRGB(i + 1, j + 1));

                int gx =  ((-1 * val00) + (0 * val01) + (1 * val02))
                        + ((-2 * val10) + (0 * val11) + (2 * val12))
                        + ((-1 * val20) + (0 * val21) + (1 * val22));

                int gy =  ((-1 * val00) + (-2 * val01) + (-1 * val02))
                        + ((0 * val10) + (0 * val11) + (0 * val12))
                        + ((1 * val20) + (2 * val21) + (1 * val22));

                double gval = Math.sqrt((gx * gx) + (gy * gy));
                int g = (int) gval;

                if(maxGradient < g)
                {
                    maxGradient = g;
                }
                edgeColors[i][j] = g;
            }
        }

        double scale = 255.0 / maxGradient;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
                int edgeColor = edgeColors[i][j];
                edgeColor = (int)(edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                image.setRGB(i, j, edgeColor);
            }
        }

        fillOutlineWithZeros(image, x, y);

        System.out.println("Finished");

        return image;
    }

    private static BufferedImage fillOutlineWithZeros(BufferedImage image, int x, int y)
    {
        for (int i = 0; i < x; i++)
        {
            image.setRGB(i, 0, 0);
            image.setRGB(i, y-1, 0);
        }

        for (int j = 0; j < y; j++)
        {
            image.setRGB(0, j, 0);
            image.setRGB(x-1, j, 0);
        }

        return image;
    }

    private static int getGrayScale(int rgb)
    {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        //from https://en.wikipedia.org/wiki/Grayscale, calculating luminance
        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);

        return gray;
    }
}
