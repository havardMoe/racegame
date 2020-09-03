import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageLoader
{
    public static BufferedImage loadImage(String path, int desiredWidth, int desiredHeight)
    {
        try
        {
            //Bilde i orginal dimensjon
            BufferedImage image = ImageIO.read(ImageLoader.class.getResource(path));
            int w = image.getWidth();
            int h = image.getHeight();

            BufferedImage dimg = new BufferedImage(desiredWidth, desiredHeight, image.getType());
            Graphics2D g = dimg.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, desiredWidth, desiredHeight, 0 ,0, w, h, null);
            g.dispose();

            //Bilde i ønsket dimensjon
            return dimg;

        } catch (IOException e)
        {
            System.out.println("klarte ikke hente bilde");
            e.printStackTrace();
        }
        return null;
    }
}
