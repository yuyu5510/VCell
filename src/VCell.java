import java.util.List;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import java.util.Collections;
import java.util.Comparator;

public class VCell {
    public final Image image;
    public final List<Cluster> clusters;
    public final SortedSet<Pixel> border_pixels;
    public float M;
    public float LAMBDA;
    public float ALPHA;

    public VCell(Image image){
        this.image = image;
        clusters = new ArrayList<Cluster>();
        border_pixels = new TreeSet<Pixel>();
    }

    synchronized public void clear(){
        image.initPixels();
        clusters.clear();
        border_pixels.clear();
    }

    public float dist(Pixel p, Cluster c){
        float count = 0;
        for(Pixel npix: p.getNeighbors()){
            if(npix.getID() != c.getID()){
                count += 1;
            }
        }
        return (float)Math.sqrt(
                (p.getGray() - c.getColorCentoroid()) * (p.getGray() - c.getColorCentoroid())
                + (2 * LAMBDA * count)
                );
        // return (float)Math.sqrt(
        //         (p.data - c.getMeanRGB()) * (p.data - c.getMeanRGB())
        //         + (2 * LAMBDA * count)
        //         );
    }

    // calculate after setGroupByHex
    public void setM(){
        M = Math.max(Math.min((float)(Math.sqrt(clusters.size() / image.length) * 25), (float)10), (float)1);
    }   
            
    public float EnergyCVT(){
        float energy = 0;
        for(Cluster c: clusters){
            for(Pixel p: c.pixels){
                energy += (p.getGray() - c.getColorCentoroid()) * (p.getGray() - c.getColorCentoroid());
                //energy += (p.data - c.getMeanRGB()) * (p.data - c.getMeanRGB());
            }
        }
        return energy;
    }
    
    public float EnergyL_div_lambda(){
        float energy = 0;
        for(Pixel p: image.pixels){
            for(Pixel npix: p.getNeighbors()){
                if(p.getID() != npix.getID()){
                    energy += 1;
                }
            }
        }
        return energy;
    }

    public void calculate_lambda(){
        LAMBDA = M * EnergyCVT() / EnergyL_div_lambda();
    }

    synchronized public void setGroupByHex(int edge_length){
        clusters.clear();
        for(Pixel p: image.pixels){
            p.setID(-1);
        }
        int edge_root3_div2 = (int)(Math.sin(Math.PI / 3) * edge_length);
        int counter = 0;
        int start = 0;
        Vec2 vector[] = new Vec2[6];
        for(int y = 0;y < image.height + edge_root3_div2; y += edge_root3_div2){
            for(int x = start;x < image.width + 3*edge_length; x += (3*edge_length)){
               
               clusters.add(new Cluster(counter));
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
                        if(nx < 0 || nx >= image.width || ny < 0 || ny >= image.height) continue;
                        for(int i = 0;i < 6;i++){
                            Vec2 P = new Vec2(nx, ny);
                            Vec2 AB = vector[i].subtract(centor);
                            Vec2 BC = vector[(i + 1) % 6].subtract(vector[i]);
                            Vec2 CA = centor.subtract(vector[(i + 1) % 6]);
                            Vec2 AP = P.subtract(centor);
                            Vec2 BP = P.subtract(vector[i]);
                            Vec2 CP = P.subtract(vector[(i + 1) % 6]);
                            
                            if(((AB.cross(AP) >= 0 && BC.cross(BP) >= 0 && CA.cross(CP) >= 0) 
                                || (AB.cross(AP) <= 0 && BC.cross(BP) <= 0 && CA.cross(CP) <= 0))
                                && image.pixels[nx + ny * image.width].getID() == -1){
                                    image.pixels[nx + ny * image.width].setID(counter);
                                    clusters.get(counter).addPixel(image.pixels[nx + ny * image.width]);
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
    
    
        for(int y = 0;y < image.height;y++){
            for(int x = 0;x < image.width;x++){
                if(image.pixels[x + y * image.width].getID() == -1){
                    int dist = 1<<30, nearest = -1;
                    for(int dy = -edge_length;dy <= edge_length;dy++){
                        for(int dx = -edge_length;dx <= edge_length;dx++){
                            int nx = x + dx, ny = y + dy;
                            if(nx < 0 || nx >= image.width || ny < 0 || ny >= image.height) continue;
                            if(image.pixels[nx + ny * image.width].getID() != -1 && dist > (dx * dx + dy * dy)){
                                dist = dx * dx + dy * dy;
                                nearest = image.pixels[nx + ny * image.width].getID();
                                break;
                            }
                        }
                    }
                    image.pixels[x + y * image.width].setID(nearest);
                    clusters.get(nearest).addPixel(image.pixels[x + y * image.width]);
                }
            }
        }

        for(Pixel p: image.pixels){
            if(p.isBorder()) {
                border_pixels.add(p);
            }
        }
        setM();
        calculate_lambda();
    }

    synchronized public void EWCVT(){
        float energy_cvt = EnergyCVT();
        float energy_l_div_lambda = EnergyL_div_lambda();
        boolean bPixelMoved = true;
        boolean bPixelNext = false;
        Queue<Pixel> que = new ArrayDeque<Pixel>(image.length);
        while(bPixelMoved){
            bPixelMoved = false;
            Iterator<Pixel> it = border_pixels.iterator();
            while(it.hasNext()){
                Pixel p = it.next();
                if(!p.isBorder()) {
                    it.remove();
                    continue;
                }

                float min_dist = -1;
                boolean first = true;
                int index = -1;
                
                for(Pixel npix: p.getNeighbors()){
                    float dist_pc = dist(p, clusters.get(npix.getID()));
                    if(first){
                        min_dist = dist_pc;
                        index = npix.getID();
                        first = false;
                    }
                    if(min_dist > dist_pc){
                        min_dist = dist_pc;
                        index = npix.getID();
                    }
                    //System.out.println("p: " + dist_pc + " " + p.getID() + " " + npix.getID());
                }
                
                if(index != p.getID()){
                    bPixelMoved = true;
                    //System.out.println("Changed");

                    energy_cvt -= (p.getGray() - clusters.get(p.getID()).getColorCentoroid()) * (p.getGray() - clusters.get(p.getID()).getColorCentoroid());
                    energy_cvt += (p.getGray() - clusters.get(index).getColorCentoroid()) * (p.getGray() - clusters.get(index).getColorCentoroid());
                    for(Pixel npix: p.getNeighbors()){
                        // 自分から見た時と、他のピクセルから見た時の両方を考える
                        if(p.getID() != npix.getID()){
                            energy_l_div_lambda -= 2;
                        } 
                        if (index != npix.getID()){
                            energy_l_div_lambda += 2;
                        }
                    }
                    LAMBDA = M * energy_cvt / energy_l_div_lambda;
                    clusters.get(p.getID()).removePixel(p);
                    clusters.get(index).addPixel(p);
                    p.setID(index);
                    for(Pixel adp: p.getAdjacents()){
                        if(adp.isBorder() && !border_pixels.contains(adp)){
                            que.add(adp);
                        }
                    }
                }
            }

            while(!que.isEmpty()){
                Pixel p = que.poll();
                border_pixels.add(p);
            }

            System.out.println(border_pixels.size());
        }
    }


    synchronized public void DSB(){
        boolean is_visit_pixel[] = new boolean[image.length];
        boolean is_visit_cluster[] = new boolean[clusters.size()];
        for(int i = 0;i < image.length;i++){
            if(!is_visit_pixel[i]){
                is_visit_pixel[i] = true;
                if(!is_visit_cluster[image.pixels[i].getID()]){
                    is_visit_cluster[image.pixels[i].getID()] = true;
                    int ncc = FloodFill(
                                        image.pixels[i],
                                        image.pixels[i].getID(),
                                        image.pixels[i].getID(),
                                        is_visit_pixel
                                        ) ;
                    if (ncc != clusters.get(image.pixels[i].getID()).pixels.size()){
                        System.out.println("Partition " + image.pixels[i].getID() + " is broken.");
                    }
                }
                else{
                    clusters.add(new Cluster(clusters.size()));
                    FloodFill(
                              image.pixels[i],
                              image.pixels[i].getID(),
                              clusters.size() - 1,
                              is_visit_pixel
                            );
                }
            }
        }
    }

    synchronized public int FloodFill(Pixel p, int id, int new_id, boolean[] is_visit_pixel){
        int ncc = 0;
        ArrayDeque<Pixel> que = new ArrayDeque<Pixel>(image.length);
        que.addLast(p);
        while(!que.isEmpty()){
            Pixel vp = que.pollFirst();
            for(Pixel apix: vp.getAdjacents()){
                if(is_visit_pixel[apix.x + apix.y * image.width]) continue;
                is_visit_pixel[apix.x + apix.y * image.width] = true;
                if(apix.getID() == p.getID()){
                    clusters.get(apix.getID()).removePixel(apix);
                    clusters.get(new_id).addPixel(apix);
                    apix.setID(new_id);
                    ncc++;
                    que.addLast(apix);
                }
            }
        }
        clusters.get(id).removePixel(p);
        clusters.get(new_id).addPixel(p);
        p.setID(new_id);
        return ncc;
    }

    synchronized public float distCluster(int x, int y){
        return (clusters.get(x).getColorCentoroid() - clusters.get(y).getColorCentoroid()) 
                * (clusters.get(x).getColorCentoroid() - clusters.get(y).getColorCentoroid())
                + ALPHA * (clusters.get(x).getSize() + clusters.get(y).getSize()) * (clusters.get(x).getSize() + clusters.get(y).getSize());
    }


    synchronized public void MergeCells(int num){
        //　クラスタをソート
        // clusters.sort(new Comparator<Cluster>(){
        //     @Override
        //     public int compare(Cluster c1, Cluster c2){
        //         return -c1.getSize() + c2.getSize();
        //     }
        // });

        // int last = clusters.size() - 1;
        // int sum = 0;
        // for(int i = last; i >= 0;i--){
        //     if(clusters.get(i).getSize() == 0){
        //         clusters.remove(i);
        //         last--;
        //         continue;
        //     }
            
        //     clusters.get(i).setID(i);
        //     for(Pixel p: clusters.get(i).pixels){
        //         p.setID(i);
        //     }
        // }
        //
        ALPHA = (float)(num * 10) / (float)(image.length);

        while(clusters.size() > num){
            clusters.sort(new Comparator<Cluster>(){
                @Override
                public int compare(Cluster c1, Cluster c2){
                    return -c1.getSize() + c2.getSize();
                }
            });

            int last = clusters.size() - 1;
            int sum = 0;
            for(int i = last; i >= 0;i--){
                if(clusters.get(i).getSize() == 0){
                    clusters.remove(i);
                    last--;
                    continue;
                }
                
                clusters.get(i).setID(i);
                for(Pixel p: clusters.get(i).pixels){
                    p.setID(i);
                }
            }

            float min_dist = 1<<30;
            int new_id = -1;
            for(Pixel p: clusters.get(last).pixels){
                for(Pixel ap: p.getAdjacents()){
                    if(ap.getID() != p.getID()){
                        if(distCluster(p.getID(), ap.getID()) < min_dist){
                            min_dist = distCluster(p.getID(), ap.getID());
                            new_id = ap.getID();
                        }
                    }
                }
            }

            System.out.println("Merge " + last + " " + new_id);
            for (Pixel p: clusters.get(last).pixels){
                clusters.get(new_id).addPixel(p);
                p.setID(new_id);
            }
            clusters.remove(last);
            last--;
        }
    }
}
