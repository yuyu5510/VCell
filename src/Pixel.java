import java.util.List; 
import java.util.ArrayList; 

public class Pixel implements Comparable<Pixel>{
  public final int x, y;
  public int data;
  public final Image image;
  public int id;
  private List<Pixel> neighbors;
  private List<Pixel> adjacents;

  public Pixel(int x, int y, int data, Image image){
    this.x = x;
    this.y = y;
    this.data = data;
    this.image = image;
  }

  public void setNeighbors(List<Pixel> pixels){
    neighbors = pixels;
  }

  public void setAdjacents(List<Pixel> pixels){
    adjacents = pixels;
  }

  public List<Pixel> getNeighbors(){
    return neighbors;
  }

  public List<Pixel> getAdjacents(){
    return adjacents;
  }

  public boolean isBorder(){
    for(Pixel tmp: adjacents){
      if(tmp.getID() != id){
        return true;
      }
    }
    return false;
  }
  
  public void setID(int id){
    this.id = id;
  }

  public int getID(){
    return id;
  }

  public float getGray(){
    int R = (data & 0x00ff0000)>>16;
    int G = (data & 0x0000ff00)>>8;
    int B = (data & 0x000000ff);
    return (float)(0.2989 * R + 0.5870 * G + 0.1140 * B);
  }

  @Override
  public int compareTo(Pixel p) {
      return x + y * image.width - p.x - p.y * image.width;
  }

}
