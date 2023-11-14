import java.util.List; 
import java.util.ArrayList; 
import java.lang.Math;
import java.io.*;
import java.awt.image.BufferedImage;

public class Image{
  public final static int RADIUS = 20;
  public final static int POWRADIUS = RADIUS*RADIUS;
  public final BufferedImage img;
  public final Pixel[] pixels;
  public final int width;
  public final int height;
  public final int length;
  
  public Image(String filename){
    BufferedImage tmp=null;
    try{
      tmp = ImageUtil.load(filename);
    }catch(IOException ex){
      System.err.println(ex);
      System.exit(1);
    }
    img = tmp;
    width = img.getWidth();
    height = img.getHeight();
    length = width*height;
    pixels = new Pixel[length];
  }

  public void initPixels(){
    for(int i=0;i<length;i++){
      int data = img.getRGB(i%width, i/width);
      pixels[i] = new Pixel(i%width, i/width, data, this);
    }
    for(int i=0;i<length;i++){
      int cx=i%width;
      int cy=i/width;
      ArrayList<Pixel> neighbors = new ArrayList<Pixel>(); 
      for(int y=-RADIUS;y<=RADIUS;y++){
        for(int x=-RADIUS;x<=RADIUS;x++){
          if( x*x+y*y <= POWRADIUS &&
              0 <= cx && cx< width && 0 <= cy && cy < height ){
            neighbors.add(pixels[cx+cy*width]);
          }
        }
      }
      pixels[i].setNeighbors(neighbors);

      ArrayList<Pixel> adjacents = new ArrayList<Pixel>();
      Pixel tmp;
      if((tmp = getUpper(pixels[i]))!=null){
        adjacents.add(tmp);
      }
      if((tmp = getLower(pixels[i]))!=null){
        adjacents.add(tmp);
      }
      if((tmp = getLefter(pixels[i]))!=null){
        adjacents.add(tmp);
      }
      if((tmp = getRighter(pixels[i]))!=null){
        adjacents.add(tmp);
      }
      pixels[i].setAdjacents(adjacents);
    }
  }
  
  public Pixel getUpper(Pixel pixel){
    int pos = (pixel.y-1)*width+pixel.x;
    if(pos<0){
      return null;
    }
    return pixels[pos];
  }

  public Pixel getLower(Pixel pixel){
    int pos = (pixel.y+1)*width+pixel.x;
    if(length<=pos){
      return null;
    }
    return pixels[pos];
  }

  public Pixel getLefter(Pixel pixel){
    int x = pixel.x-1;
    if(x<0){
      return null;
    }
    return pixels[x+pixel.y*width];
  }

  public Pixel getRighter(Pixel pixel){
    int x = pixel.x+1;
    if(width <= x){
      return null;
    }
    return pixels[x+pixel.y*width];
  }


}
