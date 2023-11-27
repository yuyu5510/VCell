import java.util.List;
import java.util.ArrayList;

public class Cluster{
  public int id;
  public List<Pixel> pixels;
  private long sumR;
  private long sumG;
  private long sumB;
  public float meanR;
  public float meanG;
  public float meanB;
  public int num;

  public Cluster(int id){
    this.id = id;
    pixels = new ArrayList<Pixel>();
    sumR = 0;
    sumG = 0;
    sumB = 0;
    meanR = 0;
    meanG = 0;
    meanB = 0;
  }

  public int getID(){
    return id;
  }

  public void setID(int id){
    this.id = id;
  }

  public int getSize(){
    return num;
  }

  public void addPixel(Pixel pixel){
    pixels.add(pixel);
    int R = (pixel.data & 0x00ff0000)>>16;
    int G = (pixel.data & 0x0000ff00)>>8;
    int B = (pixel.data & 0x000000ff);
    sumR += R;
    sumG += G;
    sumB += B;
    /*
    meanR = (meanR*num+R)/(num+1);
    meanG = (meanG*num+G)/(num+1);
    meanB = (meanB*num+B)/(num+1);
    */
    num++;
    meanR = sumR/(float)num;
    meanG = sumG/(float)num;
    meanB = sumB/(float)num;
  }
  
  public void removePixel(Pixel pixel){
    if(pixels.remove(pixel)){
      int R = (pixel.data & 0x00ff0000)>>16;
      int G = (pixel.data & 0x0000ff00)>>8;
      int B = (pixel.data & 0x000000ff);
      sumR -= R;
      sumG -= G;
      sumB -= B;
      /*
      meanR = (meanR*num-R)/(num-1);
      meanG = (meanG*num-G)/(num-1);
      meanB = (meanB*num-B)/(num-1);
      */
      num--;
      meanR = sumR/(float)num;
      meanG = sumG/(float)num;
      meanB = sumB/(float)num;
    }
  }

  public int getMeanRGB(){
    return (((int)meanR)<<16)+(((int)meanG)<<8)+(((int)meanB));
  }

  public float getColorCentoroid(){
    return (float)(0.2989 * meanR + 0.5870 * meanG + 0.1140 * meanB);
  }

}
