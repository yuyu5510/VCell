import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.canvas.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;

public class Main extends Application{
  Image image;
  GraphicsContext gc;
  WritableImage wi;
  VCell vcell;
  int counter;
  
  public Main(){
    image = new Image("sample.bmp");
    image.initPixels();
    wi = new WritableImage(image.width, image.height);
    vcell = new VCell(image);
    counter = 0;
  }
  
  public void start(Stage stage){
    Button init = new Button("original");
    init.setOnAction((ev)->{
        SwingFXUtils.toFXImage(image.img, wi);
        gc.drawImage(wi,0,0);
        image.initPixels();
        counter = 0;
      }
      );
    Button step = new Button("step");
    step.setOnAction((ev)->{
        Thread thread = new Thread(()->{
            counter++;
            if(counter == 1){
                vcell.setGroupByHex(10);
            }
            if(counter == 2){
                vcell.EWCVT();
            }
            if(counter == 3){
                vcell.DSB();
            }
            System.out.println("Number of Cluster : " + vcell.clusters.size());
            Platform.runLater(()->{
                /* show boarder lines on the original image */
                SwingFXUtils.toFXImage(image.img, wi);
                gc.drawImage(wi,0,0);
                for(Pixel pix : image.pixels){
                    if(pix.isBorder()){
                        if(pix.getID() == -1){
                            System.out.println("invalid");
                        }
                        gc.setStroke(Color.GREEN);
                        gc.strokeRect(pix.x,pix.y,1,1);
                    }
                }
            });
          
        });
        thread.start();
        }
      );
    Button finish = new Button("finish");
    finish.setOnAction((ev)->{
            try{
                ImageUtil.save(image.img, "output.bmp");
            }catch(Exception ex){
                System.err.println(ex);
            }
            System.exit(0);
      }
      );
    Canvas can = new Canvas(image.width, image.height);
    HBox buttons = new HBox();
    VBox root = new VBox();
    root.getChildren().addAll(buttons, can);
    buttons.getChildren().addAll(init, step, finish);
    gc = can.getGraphicsContext2D();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void init(){
  }
  
}
