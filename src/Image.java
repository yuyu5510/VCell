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

  public void setGroupByHex(int edge_length){
    int edge_root3_div2 = (int)(Math.sin(Math.PI / 3) * edge_length);
    int counter = 1;
    int start = 0;
    Vec2 vector[] = new Vec2[6];
    for(int y = 0;y < height + edge_root3_div2; y += edge_root3_div2){
        for(int x = start;x < width + 3*edge_length; x += (3*edge_length)){
           // center of hexagon
           Vec2 centor = new Vec2(x, y);
           vector[0] = new Vec2(edge_length, 0);
           for(int i = 1;i < 6;i++){
            vector[i] = centor.add(vector[0].rotate(Math.PI * i / 3));
           }
           vector[0] = centor.add(vector[0]);
           for(int dy = -edge_length;dy <= edge_length;dy++){
                for(int dx = -edge_length;dx <= edge_length;dx++){
                    int nx = x + dx, ny = y + dy;
                    if(nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                    for(int i = 0;i < 6;i++){
                        Vec2 P = new Vec2(nx, ny);
                        Vec2 AB = vector[i].subtract(centor);
                        Vec2 BC = vector[(i + 1) % 6].subtract(vector[i]);
                        Vec2 CA = centor.subtract(vector[(i + 1) % 6]);
                        Vec2 AP = P.subtract(centor);
                        Vec2 BP = P.subtract(vector[i]);
                        Vec2 CP = P.subtract(vector[(i + 1) % 6]);
                        
                        if((AB.cross(AP) >= 0 && BC.cross(BP) >= 0 && CA.cross(CP) >= 0) 
                            || (AB.cross(AP) <= 0 && BC.cross(BP) <= 0 && CA.cross(CP) <= 0)){
                            pixels[nx + ny * width].setID(counter);
                            break;
                        }
                    }
                }
           }
           counter++;
        }
        if(start == 0) start = 3 * edge_length / 2;
        else start = 0;
    }


    for(int y = 0;y < height;y++){
        for(int x = 0;x < width;x++){
            if(pixels[x + y * width].getID() == 0){
                int dist = 1<<30, nearest = -1;
                for(int dy = -edge_length;dy <= edge_length;dy++){
                    for(int dx = -edge_length;dx <= edge_length;dx++){
                        int nx = x + dx, ny = y + dy;
                        if(nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                        if(pixels[nx + ny * width].getID() != 0 && dist > (dx * dx + dy * dy)){
                            dist = dx * dx + dy * dy;
                            nearest = pixels[nx + ny * width].getID();
                            break;
                        }
                    }
                }
                pixels[x + y * width].setID(nearest);
            }
        }
    }

}

}
