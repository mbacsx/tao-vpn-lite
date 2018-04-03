package org.tao.vpn.lite.crypt;
public class CryptUtils {
     public static byte encrypt(int b){
        return (byte) ((byte)1+b);
    }
   
    public static byte decrypt(int b){
        return  (byte) (b-1);
    } 
    

}
