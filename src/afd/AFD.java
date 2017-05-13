package afd;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AFD {

    public static void leer(){
        File archivo = new File ("C:\\Users\\Leandro\\Desktop\\AFN\\archivo.txt");
        FileReader fr1 = null;
        FileReader fr2 = null;
        try {
            fr1 = new FileReader (archivo);
            fr2 = new FileReader (archivo);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AFD.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader in1 = new BufferedReader(fr1);
        BufferedReader in2 = new BufferedReader(fr2);
        
        String linea = null;
        StringTokenizer str;
        char letras[]=new char[26];
        int aceptacion[]=null;
        String transiciones[][]=null;
        int num_Estados = 0;
        
        for(int i= 65; i<= 90;i++)
            letras[i-65]=(char)i;

        //Checar número de estados
        try {  
            linea = in1.readLine();
            while (linea != null) {            
                num_Estados++;
                linea = in1.readLine();
            }
            linea=null;
            
            if (num_Estados==0)
                System.out.println("Archivo vacío...");
            in1.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(AFD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Lectura estados
        transiciones=new String[num_Estados][2];
        aceptacion=new int[num_Estados];
        int k=0;
         try {  
            linea = in2.readLine();
            while (linea != null) {      
                str = new StringTokenizer(linea);
                //con 0
                transiciones[k][0]=str.nextToken();
                //ver si es estado de aceptación
                for (int p=0;p<num_Estados;p++){
                    if(Character.toString(letras[p]).equals(transiciones[k][0]))
                        aceptacion[p]=Integer.parseInt(str.nextToken());
                }
                //con 1
                transiciones[k][1]=str.nextToken();
                //ver si es estado de aceptación
                for (int p=0;p<num_Estados;p++){
                    if(Character.toString(letras[p]).equals(transiciones[k][1]))
                        aceptacion[p]=Integer.parseInt(str.nextToken());
                }
                linea = in2.readLine();
                k++;
            }
            in2.close();
        }
        catch (IOException ex) {
            Logger.getLogger(AFD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        leer();
    }   
}
