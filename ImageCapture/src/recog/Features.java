/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recog;

/**
 *
 * @author xwc981
 */
public class Features {
    // features for the key
    
    public double distance_feature_vector(double[] piece, double[] input){
        int size = piece.length;
        double distance = 0.0;
        double[] result_vector = new double[size];
        for(int i = 0; i < size; i++){
            result_vector[i] = input[i] - piece[i];
        }
        for(int i = 0; i < size; i++){
            distance += (result_vector[i] * result_vector[i]);
        }
        distance = Math.sqrt(distance);
        return distance;
    }
    
    public int classification(double[] distances){
        Database d = new Database();
        int class_num = 0;
        double min = distances[0];
        for(int i = 1; i < distances.length; i++){
            if(distances[i] < min){
                min = distances[i];
                class_num = i;
            }
        }
        return (class_num+1);
    }
    
}
