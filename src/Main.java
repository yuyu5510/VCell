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
  
  public Main(){
    image = new Image("sample.bmp");
    image.initPixels();
    wi = new WritableImage(image.width, image.height);
  }
  
  public void start(Stage stage){
    Button init = new Button("original");
    init.setOnAction((ev)->{
        SwingFXUtils.toFXImage(image.img, wi);
        gc.drawImage(wi,0,0);
      }
      );
    Button step = new Button("step");
    step.setOnAction((ev)->{
        Platform.runLater(()->{
            /* show boarder lines on the original image */
            SwingFXUtils.toFXImage(image.img, wi);
            gc.drawImage(wi,0,0);
            image.setGroupByHex(10);
            for(Pixel pix : image.pixels){
            if(pix.isBorder()){
                if(pix.getID() == 0){
                    System.out.println("invalid");
                }
                gc.setStroke(Color.GREEN);
                gc.strokeRect(pix.x,pix.y,1,1);
              }
            }
          });
      }
      );
    Button finish = new Button("finish");
    finish.setOnAction((ev)->{
        
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
