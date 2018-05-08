/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recog;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import opencv.OpenCVProcessor;
import static org.opencv.core.CvType.CV_8UC1;
import org.opencv.core.Mat;
import utilities.FXDIPUtils;
import utilities.ImageIoFX;

/**
 *
 * @author xwc981
 */
public class Database 
{

    /**
     * @return the connection
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * @return the myStmt
     */
    public Statement getMyStmt()
    {
        return myStmt;
    }

    /**
     * @param myStmt the myStmt to set
     */
    public void setMyStmt(Statement myStmt)
    {
        this.myStmt = myStmt;
    }

    /**
     * @return the myRs
     */
    public ResultSet getMyRs()
    {
        return myRs;
    }

    /**
     * @param myRs the myRs to set
     */
    public void setMyRs(ResultSet myRs)
    {
        this.myRs = myRs;
    }
    private Connection connection; 
    private Statement myStmt; 
    private ResultSet myRs;
    
    public Database()
    {
        databaseConnect();
        //storeImageDescriptors("image1.jpg");
        //storeImageDescriptors("image2.jpg");
        //storeImageDescriptors("image3.jpg");
        //storeImageDescriptors("image4.jpg");
        //storeImageDescriptors("image5.jpg");
        //storeImageDescriptors("image6.jpg");
        getImageDescriptors();
    }
    private void databaseConnect()
    {
        try
        {
            setConnection(DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/keymatching", "root", "root"));
            setMyStmt(getConnection().createStatement());
        } 
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void storeImageDescriptors(String name, int keyid)
    {
        try
        {
            BufferedImage bufferedImage  = ImageIO.read(new File(name));
            BufferedImage bufferedImage2 = ImageIoFX.toGray(bufferedImage);
            Image image  = SwingFXUtils.toFXImage(bufferedImage, null);

            byte[][] currentImageBytes =  ImageIoFX.getGrayByteImageArray2DFromBufferedImage(bufferedImage2);
            Mat graylImageMat     =  FXDIPUtils.byteToGrayMat(currentImageBytes, CV_8UC1);

            Image graylImage  =  FXDIPUtils.mat2Image(graylImageMat);

            Mat binarylImageMat =  OpenCVProcessor.doThreshold(graylImageMat,100);
            Image resultImg = FXDIPUtils.mat2Image(binarylImageMat);

            // Extracting Features            

            double[] features = OpenCVProcessor.doFDDescriptorsComplexDistance(binarylImageMat,512); //getting the descriptors
            byte[] featurebytes = new byte[512*8];
            ByteBuffer buf = ByteBuffer.wrap(featurebytes);
            for (double l : features)
                buf.putDouble(l); //storing the descriptors as bytes (8 bytes per double)

            //storing the bytes into the database
            PreparedStatement statement = connection.prepareStatement("INSERT INTO features (content, keyid) VALUES (?,?)");
            statement.setBinaryStream(1,new ByteArrayInputStream(featurebytes),featurebytes.length);
            statement.setInt(2, keyid);
            statement.executeUpdate();
        } catch (IOException ex)
        {
            Logger.getLogger(KeyOperations.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (SQLException ex)
        {
            Logger.getLogger(KeyOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<double[]> getImageDescriptors()
    {
        ArrayList<double[]> imageDescriptors = new ArrayList<>();
        try
        {
            //retrieving the features from the database
            ResultSet myRs = myStmt.executeQuery("Select features.featuresid, features.content, features.keyid, keydata.keyid, keydata.ownerid"
                    + ", owner.ownerid, owner.firstname, owner.lastname from features,keydata, owner where features.keyid=keydata.keyid AND keydata.ownerid = owner.ownerid");
            while(myRs.next())
            {
                double[] features = new double[512]; //to retrieve the features (descriptors)
                Blob blob = myRs.getBlob("features.content"); //retrieving the blob
                byte[] bytes2 = blob.getBytes(1, (int)blob.length()); //getting al the bytes of the blob
                // Converting from byte to double
                ByteBuffer buf2 = ByteBuffer.wrap(bytes2);
                for (int i = 0; i < features.length; i++)
                {
                    features[i] = buf2.getDouble(i*8);
                }
                System.out.println("image with id="+myRs.getString("keydata.keyid")+" belongs to "+myRs.getString("owner.firstname")
                        +" "+myRs.getString("owner.lastname"));
                imageDescriptors.add(features);
            }
        } 
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageDescriptors;
        
    }
    //public 
    
    public String getKeyOwnerInfo(int id)
    {
        String info = "";
        try
        {
            //retrieving the features from the database
            ResultSet myRs = myStmt.executeQuery("Select features.featuresid, features.content, features.keyid, keydata.keyid, keydata.ownerid"
                    + ", owner.ownerid, owner.firstname, owner.lastname from features,keydata, owner where features.keyid=keydata.keyid AND keydata.ownerid = owner.ownerid");
            while(myRs.next())
            {
                if(myRs.getInt("features.featuresid") == id)
                    info = ("image with id="+myRs.getString("keydata.keyid")+" belongs to "+myRs.getString("owner.firstname")
                        +" "+myRs.getString("owner.lastname"));
            }
        } 
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;    
    }
    
}
