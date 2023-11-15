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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.beans.value.ObservableValue;

public class Main extends Application{
  Image image;
  GraphicsContext gc;
  WritableImage wi;
  VCell vcell;
  volatile int counter;
  volatile int hex_edge;
  volatile Thread thread;
  
  public Main(){
    image = new Image("sample.bmp");
    image.initPixels();
    wi = new WritableImage(image.width, image.height);
    vcell = new VCell(image);
    counter = 0;
    hex_edge = 20;
    thread = null;
  }
  
  public void start(Stage stage){
    Button init = new Button("original");
    init.setOnAction((ev)->{
        SwingFXUtils.toFXImage(image.img, wi);
        gc.drawImage(wi,0,0);
        image.initPixels();
        counter = 0;
        hex_edge = 10;
        stage.setTitle("original");
      }
      );
    Button step = new Button("step");
    step.setOnAction((ev)->{
        if (thread != null) {
            return;
        }
        thread = new Thread(()->{
        if(counter <= 0){
            Platform.runLater(()->{
                stage.setTitle("original");
            });
        }
        if(counter == 1){
            Platform.runLater(()->{
                stage.setTitle("group by hex");
            });
            vcell.setGroupByHex(hex_edge);
        }
        if(counter == 2){
            Platform.runLater(()->{
                stage.setTitle("EWCVT-LNN");
            });
            vcell.EWCVT();
        }
        if(counter == 3){
            Platform.runLater(()->{
                stage.setTitle("DSB");
            });
            vcell.DSB();
        }
        // counter を使うのは高々1スレッドなので、synchronized は不要
        counter++;
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
        Thread waitThread = new Thread(()->{
            try{
                thread.join();
                thread = null;
            }catch(Exception ex){
                System.err.println(ex);
            }
        });
        waitThread.start();
    });
    Button finish = new Button("finish");
    finish.setOnAction((ev)->{
            // partition を txt に書き出して、境界付きの画像を保存
            try{
                String fileTextPath = "output.txt";
                FileWriter fileWriter = new FileWriter(fileTextPath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                // ファイルに書き込むテキストデータ
                
                for(int y = 0;y < image.height;y++){
                    String textData = "";
                    for(int x = 0;x < image.width;x++){
                        int i = x + y * image.width;

                        textData += image.pixels[i].getID();
                        if(x != image.width - 1){
                            textData += " ";
                        }

                        if(image.pixels[i].isBorder()){
                            if(image.pixels[i].getID() == -1){
                                System.out.println("invalid");
                            }
                            image.img.setRGB(image.pixels[i].x, image.pixels[i].y, 0x0000ff00);
                            // write file
                        }
                    }
                    textData += '\n';
                    bufferedWriter.write(textData, 0, textData.length());
                    bufferedWriter.flush();
                }
                bufferedWriter.close();
                ImageUtil.save(image.img, "output.bmp");
            }catch(Exception ex){
                System.err.println(ex);
            }
            System.exit(0);
      }
      );

    Slider sliderhex = new Slider(5, 50, 10);
    sliderhex.valueProperty().addListener((ObservableValue<? extends Number> ov,
                                        Number oldv, Number nv)->{
        hex_edge = (int)sliderhex.getValue();
    });
    sliderhex.setValue(10);
    Canvas can = new Canvas(image.width, image.height);
    HBox buttons = new HBox();
    VBox root = new VBox();
    root.getChildren().addAll(buttons, sliderhex, can);
    buttons.getChildren().addAll(init, step, finish);
    gc = can.getGraphicsContext2D();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void init(){
  }
  
}
