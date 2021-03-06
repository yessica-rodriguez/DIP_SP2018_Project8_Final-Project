/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import opencv.OpenCVProcessor;
import static org.opencv.core.CvType.CV_8UC1;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import utilities.FXDIPUtils;
import utilities.ImageIoFX;


/**
 *
 * @author MQ0162246
 * @version 1.00
 * 
 */
//How to create a background image
//BackgroundImage myBI= new BackgroundImage(new Image("vaq.png",32,32,false,true),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
//BackgroundSize.DEFAULT);
//label1.setBackground(new Background(myBI));
public class KeyOperations extends BorderPane {
    
    private BufferedImage bufferedImage;

    //Frame Grabber from OpenCV
    private TabPane tabsPane;
    //Data Structure used for captured images
    
    private Slider thresholdSlider = new Slider();
    private Slider reconSlider = new Slider();
    private Slider featuresSlider = new Slider();

    //Captured Images pane
    private final TilePane tilePaneCapturedImages = new TilePane();

    //Live Image View
    private Image     originalImage;
    private ImageView originalImageview;
    
    Mat graylImageMat;
    private Image     graylImage;
    private ImageView graylImageView = new ImageView();
    
    Mat binarylImageMat;
    private Image     binarylImage;
    private ImageView binarylImageView = new ImageView();
    
    
    Mat contourImageMat;
    private Image     contourImage;
    private ImageView contourImageView = new ImageView();
    
    
    private ImageView currentReconstructedimageview= new ImageView();
    private Image     currentReconstructedimage=null;
    Mat currentReconstructedMat;
    
    
    Mat currentImageMat;
    Mat currentContourMat;
    
    FDGraph fdGraph = new FDGraph();

    // Capture/Cropping
    private final Image     processedimage=null;
    private final ImageView processedimageview= new ImageView();
    
    private double[] features;
    private int ID;
    
    private Image     DBImage;
    private ImageView DBImageView = new ImageView();
    Label classification;
    
    byte[][] currentImageBytes;
    public KeyOperations(TabPane tabsPane) 
    {
        this.tabsPane = tabsPane;  
        
        // everytime the tab is selected, check the databases for changes        
        tabsPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) {
                System.out.println("Tab Selection changed to: " + newTab.idProperty().get());
                if ((newTab.idProperty().get() != null) && newTab.idProperty().get().equalsIgnoreCase("Camera Capture")) {
                    // change where the live camera will go
                }
            }
        });        

        this.setPadding(new Insets(10, 10, 10, 10));
        tilePaneCapturedImages.setPrefColumns(6);

        // Live image
        originalImage = new Image("images/defaultLiveImage.jpg");
        originalImageview = new ImageView(originalImage);
        //currentimageview.setFitHeight(400);
        //currentimageview.setFitWidth(400);
        originalImageview.setPreserveRatio(true);

//        // Cropped Image && Cropping operations
//        processedimage     = new Image("images/defaultcaptureImage.jpg");
//        processedimageview = new ImageView(processedimage);
//        //processedimageview.setFitHeight(400);
//        //processedimageview.setFitWidth(400);
//        processedimageview.setPreserveRatio(true);

        // Generate GUI
        getGUI();
    }

    /**
     * 
     */
    public void getGUI() {
        createUItopPanel();
        createUIleftPanel();
        createUIcenterPanel();
        createUIbottomPanel();
    }

    private void createUItopPanel() {
        this.setTop(generateTopPanel());
    }

    private void createUIleftPanel() {
        //get current operations
        this.setLeft(getCameraOperations());
    }

    private void createUIcenterPanel() {
        this.setCenter(generateCenterPanel());
    }

    private void createUIbottomPanel() {
        this.setBottom(generateBottomPanel());
    }

    private VBox generateCenterPanel() {
        originalImageview.setFitHeight(200);
        originalImageview.setFitWidth(400);
        graylImageView.setFitHeight(200);
        binarylImageView.setFitHeight(200);
        contourImageView.setFitHeight(200);
        currentReconstructedimageview.setFitHeight(200);
        graylImageView.setFitWidth(400);
        binarylImageView.setFitWidth(400);
        contourImageView.setFitWidth(400);
        currentReconstructedimageview.setFitWidth(400);
        processedimageview.setFitHeight(200);
        processedimageview.setFitWidth(400);
        
        Separator separator1 = new Separator();
        separator1.setMinSize(20, 20);
        separator1.setOrientation(Orientation.VERTICAL);
        
        Separator separator2 = new Separator();
        separator2.setMinSize(20, 20);
        separator2.setOrientation(Orientation.VERTICAL);
        
        Separator separator3 = new Separator();
        separator3.setMinSize(20, 20);
        separator3.setOrientation(Orientation.VERTICAL);
        
        Separator separator4 = new Separator();
        separator4.setMinSize(20, 20);
        separator4.setOrientation(Orientation.HORIZONTAL);
        
        Separator separator5 = new Separator();
        separator4.setMinSize(20, 20);
        separator4.setOrientation(Orientation.HORIZONTAL);
        
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        HBox hb  = new HBox();
        HBox hb2 = new HBox();
        hb.getChildren().addAll(originalImageview, separator1,graylImageView,separator2,binarylImageView );
        hb2.getChildren().addAll(contourImageView, separator3, currentReconstructedimageview );
        
        vb.getChildren().addAll(hb,separator4, hb2, separator5, fdGraph);
        BorderPane.setAlignment(vb, Pos.CENTER);
        return vb;
    }
    private VBox generateTopPanel() {
        //set insets
        this.setPadding(new Insets(20, 10, 10, 10));
        this.setBorder(new Border(new BorderStroke(Color.NAVY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(5))));

        // Top Border
        Label label1 = new Label("Key Operations ");
        Font myFont = Font.font("Verdana", FontPosture.ITALIC, 40);
        label1.setFont(myFont);
        label1.setTextFill(Color.web("#0870a3"));
        label1.setWrapText(true);  
        Image image = new Image("images/vaq.png");
        ImageView iv = new ImageView(image);
        iv.setFitHeight(32);
        iv.setFitWidth(32);
        label1.setGraphic(iv);

        label1.setBorder(new Border(new BorderStroke(Color.NAVY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2))));
        label1.setTextAlignment(TextAlignment.CENTER);
        //BorderPane.setAlignment(label1, Pos.CENTER);
        Separator separator1 = new Separator();
        separator1.setMinSize(20, 20);
        separator1.setOrientation(Orientation.HORIZONTAL);
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(label1, separator1);
        BorderPane.setAlignment(vb, Pos.CENTER);
        return vb;
    }

    private HBox generateBottomPanel() {
        DBImageView.setFitHeight(200);
        DBImageView.setFitWidth(400);
        
        Separator separator1 = new Separator();
        separator1.setMinSize(20, 20);
        separator1.setOrientation(Orientation.VERTICAL);
        
        classification = new Label();
        Font myFont = Font.font("Verdana", FontPosture.ITALIC, 20);
        classification.setFont(myFont);
        classification.setTextFill(Color.web("#ff0000"));
        

        HBox hb  = new HBox();
        hb.getChildren().addAll(DBImageView, separator1,classification);

        return hb;
    }
    
    public VBox getCameraOperations() {
        VBox dipOperations = new VBox(10);
        //Load
        Button selectKeyButton = new Button("Load Image...");
        selectKeyButton.setMaxWidth(Double.MAX_VALUE);
        selectKeyButton.setOnAction(new Operation1Handler());
        
        // Convert to grey-level
        Button GrayButton = new Button("Convert to Grayscale");
        GrayButton.setMaxWidth(Double.MAX_VALUE);
        GrayButton.setOnAction(new Operation4Handler());

        // Negative if needed
        Button NegateButton = new Button("Negative Image");
        NegateButton.setMaxWidth(Double.MAX_VALUE);
        NegateButton.setOnAction(new Operation2Handler());
        
        //Binarized the image
        Button BinarizeButton = new Button("Binarization");
        BinarizeButton.setMaxWidth(Double.MAX_VALUE);
        BinarizeButton.setOnAction(new Operation5Handler());
        
        thresholdSlider.setMin(0);
        thresholdSlider.setMax(255);
        thresholdSlider.setValue(00);
        thresholdSlider.setShowTickLabels(true);
        thresholdSlider.setShowTickMarks(true);
        thresholdSlider.setMajorTickUnit(50);
        thresholdSlider.setMinorTickCount(10);
        thresholdSlider.setBlockIncrement(10);
        
        reconSlider.setMin(0);
        reconSlider.setMax(512);
        reconSlider.setValue(512);
        reconSlider.setShowTickLabels(true);
        reconSlider.setShowTickMarks(true);
        reconSlider.setMajorTickUnit(100);
        reconSlider.setMinorTickCount(10);
        reconSlider.setBlockIncrement(10);
        
        featuresSlider.setMin(0);
        featuresSlider.setMax(512);
        featuresSlider.setValue(512);
        featuresSlider.setShowTickLabels(true);
        featuresSlider.setShowTickMarks(true);
        featuresSlider.setMajorTickUnit(100);
        featuresSlider.setMinorTickCount(10);
        featuresSlider.setBlockIncrement(10);
        
        // Contour
        Button ContourButton = new Button("Edge Detection-OpenCV");
        ContourButton.setMaxWidth(Double.MAX_VALUE);
        ContourButton.setOnAction(new Operation6Handler());
        
        // Contour
        Button ReconButton = new Button("Reconstruction");
        ReconButton.setMaxWidth(Double.MAX_VALUE);
        ReconButton.setOnAction(new Operation7Handler());
        
        // Process 
        Button processKeyButton = new Button("Extract Features");
        processKeyButton.setMaxWidth(Double.MAX_VALUE);
        processKeyButton.setOnAction(new Operation3Handler());
        
        // Process 
        Button ClassifyKeyButton = new Button("Classify");
        ClassifyKeyButton.setMaxWidth(Double.MAX_VALUE);
        ClassifyKeyButton.setOnAction(new Operation8Handler());

        // Final layout
        VBox vb = new VBox(10);
        vb.getChildren().addAll(selectKeyButton, GrayButton, NegateButton, thresholdSlider, BinarizeButton, ContourButton, reconSlider, ReconButton, featuresSlider, processKeyButton, ClassifyKeyButton);

        dipOperations.getChildren().addAll(vb);
        return dipOperations;
    }
    

       
    /**
     * @return the liveImage
     */
    public Image getQueryImage() {
        return originalImage;
    }

    /**
     * @param queryImage the liveImage to set
     */
    public void setQueryImage(Image queryImage) {
        this.originalImage = queryImage;
        originalImageview.imageProperty().set(queryImage);
    }

    /**
     * @return the liveImageView
     */
    public ImageView getQueryImageView() {
        return originalImageview;
    }

    /**
     * @param queryImageView the liveImageView to set
     */
    public void setQueryImageView(ImageView queryImageView) {
        this.originalImageview = queryImageView;
    }

    class Operation1Handler implements EventHandler<ActionEvent> {
        final FileChooser fileChooser = new FileChooser();
        @Override
        public void handle(ActionEvent event) {
                    //Clear current stuff
                    processedimageview.setImage(null);
                    // load an image....
                    File file = fileChooser.showOpenDialog(null);
                    FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("Image files", "*.JPG", "*.PNG");
                    fileChooser.getExtensionFilters().addAll(extFilterJPG);
                    try
                    {
                        bufferedImage  = ImageIO.read(file);
                        Image image  = SwingFXUtils.toFXImage(bufferedImage, null);
                        originalImageview.setImage(image);
                    }
                    catch (Exception e)
                    { }             
        }
    }
    
    //Grey-Level
    class Operation4Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            BufferedImage bufferedImage2 = ImageIoFX.toGray(bufferedImage);
            currentImageBytes =  ImageIoFX.getGrayByteImageArray2DFromBufferedImage(bufferedImage2);
            graylImageMat     =  FXDIPUtils.byteToGrayMat(currentImageBytes, CV_8UC1); 
            Image graylImage  =  FXDIPUtils.mat2Image(graylImageMat); 
            graylImageView.setImage(graylImage);
        }        
    }
    
    //Negative Image
    class Operation2Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            graylImageMat =   OpenCVProcessor.doNegative(graylImageMat);  
            Image resultImg = FXDIPUtils.mat2Image(graylImageMat); 
            graylImageView.setImage(resultImg);
        }        
    }
    
    //Binarization
    class Operation5Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // OpenCV Stuff... Mat in and Mat out
            binarylImageMat =  OpenCVProcessor.doThreshold(graylImageMat,((int) thresholdSlider.getValue()));
            Image resultImg = FXDIPUtils.mat2Image(binarylImageMat); 
            binarylImageView.setImage(resultImg);  
        }        
    }
    
    //Contour
    class Operation6Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // OpenCV Stuff... Mat in and Mat out
            MatOfPoint matOfPoint   = OpenCVProcessor.doContour(binarylImageMat);
            currentContourMat       = OpenCVProcessor.drawContourMat(matOfPoint, binarylImageMat);
            // Show contour
            Image resultImg2 = FXDIPUtils.mat2Image(currentContourMat); 
            contourImageView.setImage(resultImg2);
        }        
    }
    
    //Reconstruction
    class Operation7Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {  
            // For fun show the reconstructed based on a subset of the total features
            currentReconstructedMat       = OpenCVProcessor.doFDDescriptorsComplexDistanceReconstruction(binarylImageMat,((int) reconSlider.getValue())); 
            Image currentReconstructedimage = FXDIPUtils.mat2Image(currentReconstructedMat); 
            currentReconstructedimageview.setImage(currentReconstructedimage);  
        }        
    }
    
    //Extract Features
    class Operation3Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            features = OpenCVProcessor.doFDDescriptorsComplexDistance(binarylImageMat,((int) featuresSlider.getValue())); 
            //Graph the FDs... they are tiny in mgd?! first is always one (you can remove it if you want to).
            fdGraph = fdGraph.graphFD(features, ((int) featuresSlider.getValue()));  
        }        
    }
    
    //Classify
    class Operation8Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Database d = new Database();
            ArrayList<double[]> featuresDB = d.getImageDescriptors();
            int len = featuresDB.size();
            Features f = new Features();
            //int descriptors = (int) featuresSlider.getValue();
            double[] distFeatureV = new double[len];
            for(int i = 0; i < len; i++){
                distFeatureV[i] = f.distance_feature_vector(features, featuresDB.get(i));
            }
            for(int i = 0; i < len; i++){
                System.out.println("Distance of image " + i + ": " + distFeatureV[i]);
            }
            ID = f.classification(distFeatureV);
            classification.setText(d.getKeyOwnerInfo(ID));
            try{
            byte[] image_1D = d.getMatchingImage(ID);
            }catch(Exception e){
                System.out.println("Exception " + e);
            }
            BufferedImage dbimage = ImageIoFX.readImage("databaseImage.jpg");
            DBImage  = SwingFXUtils.toFXImage(dbimage, null);
            DBImageView.setImage(DBImage);
        }        
    }
}

