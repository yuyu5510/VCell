import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageUtil{
  private ImageUtil(){
  }

  /**
     load a file and make an image data.
     @param filename 
   */
  public static BufferedImage load(String filename) throws IOException{
    if (filename == null){
      return null;
    }
    File f = new File(filename);
    return ImageIO.read(f);
  }

  public static void save(BufferedImage image, String filename)
                                                     throws IOException{ 
    File file = new File(filename);
    ImageIO.write(image, getSuffix(filename), file);
  }

  private static String getSuffix(String fileName) {
    if (fileName == null)
      return null;
    int point = fileName.lastIndexOf(".");
    if (point != -1) {
      return fileName.substring(point+1);
    }
    return "png";
  }
}
