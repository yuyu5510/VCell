import java.util.List; 
import java.util.ArrayList; 

public class Pixel{
  public final int x, y;
  public final int data;
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

}
