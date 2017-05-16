package afd;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AFD {
    
    public static char letras[] = new char[26];
    public static int aceptacion[]=null;
    public  static String transiciones[][]=null;
    public static int num_Estados = 5;
    //Para convertir a determinista
    public static int[] edosini;
    public static String trans[][]; 
    public static int acep[] = new int [num_Estados*2];
    public static int cont=0; //Variable que me indica en que valor de trans voy.

    public static void leer(){
        File archivo = new File ("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\" + "archivo.txt");
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
                String edo1=str.nextToken();
                String acp1=str.nextToken();
                String edo2=str.nextToken();
                String acp2=str.nextToken();
                
                //con 0
                if(edo1.contains(","))
                    edo1=edo1.replace(",", "");
                
                transiciones[k][0]=edo1;
                //ver si es estado de aceptación
                for(int h=0;h<edo1.length();h++){
                    for (int p=0;p<num_Estados;p++){
                        if(Character.toString(letras[p]).equals(Character.toString(transiciones[k][0].charAt(h))))
                            aceptacion[p]=Integer.parseInt(acp1);
                    }
                }
                //con 1
                if(edo2.contains(","))
                    edo2=edo2.replace(",", "");
                
                transiciones[k][1]=edo2;
                //ver si es estado de aceptación
                for(int h=0;h<edo2.length();h++){
                    for (int p=0;p<num_Estados;p++){
                        if(Character.toString(letras[p]).equals(Character.toString(transiciones[k][1].charAt(h))))
                            aceptacion[p]=Integer.parseInt(acp2);
                    }
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
    
    public static String[][] iniciar(String[][] arreglo, int[] inicia){
        trans = new String [num_Estados*2][3];
        transiciones = arreglo;
        edosini = inicia;    
        return convertirDeterminista();
    }
    public static boolean revisarDeterminismo(){
        boolean noDeterminista = false; 
        int i=0;
        int j=0;
        
        while(!noDeterminista || i<num_Estados){
            if(transiciones[i][j].length() >= 2 || transiciones[i][j+1].length() >= 2)
                noDeterminista = true;
            else{
                i++;
                j=0;
              }
        }
        
        return noDeterminista;
    }
    
    
    
     public static String[][] convertirDeterminista(){
      
     
      
     
     int num;
     
        StringBuilder builder = new StringBuilder();
        char letra;
        for(int i=0; i<edosini.length; i++){
            if(edosini[i] == 1){
                letra = NumeroALetra(i);
                builder.append(letra);
            }
        }
            
        if(cont<trans.length){
               trans[cont][0] = builder.toString();
               cont++;
         }else{
              trans = agrandarArreglo(trans);
              trans[cont][0] =  builder.toString();
              cont++;
        }
                
        
      
        
        
        
    
        llenarTransiciones();
        for(int r=0;r<trans.length; r++)
            for(int w=0; w<3; w++)
                System.out.println(trans[r][w]);
        //estado de aceptación
        return renombrarEstados();
 }
     public static String[][] renombrarEstados(){
         //Agregar estado vacío
         int ultimo = 0;
         int i=0;
         String ult;
         int j= 0;
         
         
         //Convertir estados finales originales por nuevo estado final 
         while(trans[i][0] != null && i<trans.length)
             i++;
         if(trans[i][0] == null)
             ultimo = i;
         else{
             trans = agregarUno(trans);
             ultimo = i+1;
         }
         trans[ultimo][0] = trans[ultimo][1]= trans[ultimo][2] = Character.toString(NumeroALetra(ultimo));
         
         
         
         String estadoFinal = Character.toString(NumeroALetra(num_Estados-1));
         
         sustituir(estadoFinal,trans[ultimo][0], ultimo);
         
         
         
         while(trans[j][0] != null && j<trans.length){
             
         sustituir(trans[j][0],Character.toString(NumeroALetra(j))+Character.toString(NumeroALetra(j)), ultimo);
         
         j++;
         }
         
          
         j=0;
         while(trans[j][0] != null && j<trans.length){
             
         sustituir(Character.toString(NumeroALetra(j))+Character.toString(NumeroALetra(j)),Character.toString(NumeroALetra(j)), ultimo);
          
         j++;
         }
     
         
         
        String determinista[][] = new String[ultimo+1][2];
        
         
                
        for(int h=0; h<=ultimo; h++){
            for (int r=1; r<3 ;r++){
              determinista[h][r-1]=trans[h][r];
              
          }
        }
        
         return determinista;
     
     
     
     }
     
     
     public static void sustituir(String estado, String letra, int ult){
         //Estado: la letra que voy a cambiar por "letra"
         
         for(int i=0; i<=ult; i++){
             
             for (int j=1; j<3; j++){
                 
                 if(trans[i][j].equals(estado)){
                     trans[i][j] = letra;
                     
                 }
                    
             }
         }
     }
     
     
     public static String[][] agregarUno(String[][] arre){
     String[][] arreg = new String[arre.length+1][3];
    for(int i=0; i<arre.length; i++){
         arreg[i][0] = arre [i][0];
         arreg[i][1] = arre [i][1];
         arreg[i][2] = arre[i][2];
    }
    return arre;
     
  }
     
     
    public static void llenarTransiciones(){
         
        int num; 
        int j=0;
        for(int i=0; i<trans.length; i++){
            
            if(trans[i][0] == null){
            }else{
               
            if(trans[i][0].length() == 1){
                num = letraANumero(trans[i][0]);
               
                trans[i][1] = transiciones[num][0];
                if(trans[i][1].equals(Character.toString(NumeroALetra(num_Estados-1)))){}
                else{
                if(revisarExistencia(trans[i][1]) == false){
                    if(cont<trans.length){
                        
                       trans[cont][0] = trans[i][1];
                      
                       cont++;
                    }else{     
                        trans = agrandarArreglo(trans);
                        trans[cont][0] =  trans[i][1];
                         cont++;
                       }
                }}
                trans[i][2] = transiciones[num][1];
                if(trans[i][2].equals(Character.toString(NumeroALetra(num_Estados-1)))){}
                else{
                if(revisarExistencia(trans[i][2]) == false){
                    if(cont<trans.length){
                       trans[cont][0] = trans[i][2];
                       cont++;
                    }else{     
                        trans = agrandarArreglo(trans);
                        trans[cont][0] =  trans[i][2];
                         cont++;
                       }
                }}
                
                
            }else{
                
                //Separar las trasniciones multiples
                
                 char[] separado = trans[i][0].toCharArray();
                 
                 
                 
                 String tra0[] = new String[separado.length]; //Transiciones  de los estados en separado bajo cero
                 String tra1[] = new String[separado.length];//Transiciones  de los estados en separado bajo uno
                 char movi[][] = new char[separado.length][2]; // Transiciones con letra de los estados separados
                 //Copiar transiciones de elementos en separado a tra
                 int r=0;
                 for(int k=0; k<separado.length; k++){
                    
                     
                     tra0[k] = transiciones[letraANumero(Character.toString(separado[k]))][0];
                    
                     
                     tra1[k] = transiciones[letraANumero(Character.toString(separado[k]))][1];
                     
                     
                     
                 }
                 //Ordenar tra y eliminar repetidos. 
                 
                 tra0 = OrdenayDuplicados(tra0);
                 tra1 = OrdenayDuplicados(tra1);
                 
                   
                 
                 //Juntar letras y crear nuevo estado
                 String e0 = crearEstado(tra0);
                 String e1 = crearEstado(tra1);
                   
                
                
                
                 if(cont<trans.length){
                     if(e0.equals(Character.toString(NumeroALetra(num_Estados-1)))){}
                     else{
                     if(revisarExistencia(e0) == false){
                         trans[cont][0] = e0;
                         cont++;
                     }
                     }
                     
                     if(e1.equals(Character.toString(NumeroALetra(num_Estados-1)))){}
                     else{
                     if(revisarExistencia(e1) == false){
                         trans[cont][0] = e1;
                         cont++;
                     }
                     }
                 }else{
                     trans = agrandarArreglo(trans);
                     if(e0.equals(Character.toString(NumeroALetra(num_Estados-1)))){}
                     else{
                     if(revisarExistencia(e0) == false){
                        trans[cont][0] = e0;
                        cont++;
                     }
                     }
                     if(e0.equals(Character.toString(NumeroALetra(num_Estados-1)))){}
                     else{
                     if(revisarExistencia(e1) == false){
                        trans[cont][0] = e1;
                        cont++;
                     }
                     }
                 }
                
                     
                 trans[i][1] = e0;
                 trans[i][2] = e1;
                 
               
            }
                 
            
            }
      
     }
    } 
     
     
 
     
    public static String crearEstado(String[] tra){
           char transSeparadas[] = new char[100];  //revisar tamaño
                 int e = 0;
                 for(int n=0; n<tra.length; n++){
                     int y = tra[n].length();
                     if( y == 1){
                         transSeparadas[e] = tra[n].charAt(0);
                         
                         
                         e++;
                     }else{
                         char letras[] = new char[y];
                         letras = tra[n].toCharArray();
                         for(int t=0; t<y; t++){
                             transSeparadas[e]=letras[t];
                             
                             e++;
                         }
                     }
                 }
       
         
        String[] estado;
        
        estado = duplicadosCharArray(transSeparadas);
        String a = new String();
        StringBuilder builder = new StringBuilder();
        if(revisarFinal(estado)){
            a = Character.toString(NumeroALetra(num_Estados-1));
        }else{
        for(int j=0; j<estado.length; j++){
            if(letraANumero(estado[j])+1 != num_Estados)
                builder.append(estado[j]);
        }
        a = builder.toString();
        }
        return a;
        
         
     }
     
    public static boolean revisarFinal(String[] arre){
        boolean fin = true;
        
        int i=0;
        while(fin && i<arre.length){
            if(!arre[i].equals(Character.toString(NumeroALetra(num_Estados-1))))
                fin= false;
            i++;
        }
        return fin;
    }
    
    public static String[][] agrandarArreglo(String[][] arreglo){
    String[][] arre = new String[arreglo.length*2][3];
    for(int i=0; i<arreglo.length; i++){
         arre[i][0] = arreglo [i][0];
         arre[i][1] = arreglo [i][1];
         arre[i][2] = arreglo [i][2];
    }
    return arre;
  } 
    
    public static String[] duplicadosCharArray(char[] array) {
    
        String[] A = new String[array.length];
        for(int n=0; n<array.length; n++){
            A[n]=Character.toString(array[n]);
        }
        Set<String> set = new HashSet<String>(Arrays.asList(A));
        A = set.toArray(new String[set.size()]);
         Arrays.sort(A);
         
         
         
         
             
         String[] B = new String[A.length-1];
         
         
         
         for(int i=0; i<B.length; i++)
             B[i]=A[i+1];
             
         
         
          return B;
            
           
     }
    
    public static boolean revisarExistencia(String letra){
         int i=0;
         boolean encontre =false;
         
         while(i<trans.length && !encontre ){
             
            if(letra.equals(trans[i][0]))
                encontre=true;
            else
                i++;
            
         }
         return encontre;
     }
     
    public static String[] OrdenayDuplicados(String[] A) {
         Set<String> set = new HashSet<String>(Arrays.asList(A));
         A = set.toArray(new String[set.size()]); //prints [Audi, Mercedes, BMW]
         
         Arrays.sort(A);
         return A;
     }
     
    public static int letraANumero(String letra){
         switch (letra){
             case "A":
                 return 0;
             case "B":
                 return 1; 
             case "C":
                 return 2; 
             case "D":
                 return 3;
             case "E":
                 return 4; 
             case "F":
                 return 5;  
             case "G":
                 return 6;
             case "H":
                 return 7; 
             case "I":
                 return 8; 
             case "J":
                 return 9;
             case "K":
                 return 10; 
             case "L":
                 return 11;
             case "M":
                 return 12;
             case "N":
                 return 13; 
             case "O":
                 return 14; 
              case "P":
                 return 15;
             case "Q":
                 return 16; 
             case "R":
                 return 17;  
             case "S":
                 return 18;
             case "T":
                 return 19; 
             case "U":
                 return 20; 
             case "V":
                 return 21;
             case "W":
                 return 22; 
             case "X":
                 return 23;  
             case "Y":
                 return 24; 
             case "Z":
                 return 25;      
         }
         return 10000;
     }
     
    public static char NumeroALetra(int num){
         switch (num){
             case 0:
                 return 'A';
             case 1:
                 return'B'; 
             case 2:
                 return'C'; 
             case 3:
                 return'D';
             case 4:
                 return'E'; 
             case 5:
                 return'F';  
             case 6:
                 return'G';
             case 7:
                 return'H'; 
             case 8:
                 return'I'; 
             case 9:
                 return'J';
             case 10:
                 return 'K'; 
             case 11:
                 return 'L';
             case 12:
                 return 'M';
             case 13:
                 return 'N'; 
             case 14:
                 return 'O'; 
              case 15:
                 return 'P';
             case 16:
                 return 'Q'; 
             case 17:
                 return 'R';  
             case 18:
                 return 'S';
             case 19:
                 return 'T'; 
             case 20:
                 return 'U'; 
             case 21:
                 return 'V';
             case 22:
                 return 'W'; 
             case 23:
                 return 'X';  
             case 24:
                 return 'Y'; 
             case 25:
                 return 'Z';      
         }
         return 'x';
     }
    
    public static void main(String[] args){
       // leer();
        //if(revisarDeterminismo())
          //  convertirDeterminista();
        //Encontrar mínimo
        //Encontrar expresión regular
        
        leer();
        String[][] prueba = new String[5][2];
        prueba[0][0]="E";
        prueba[0][1]="BD";
        prueba[1][0]="E";
        prueba[1][1]="A";
        prueba[2][0]="E";
        prueba[2][1]="BD";
        prueba[3][0]="BC";
        prueba[3][1]="E";
        prueba[4][0]="E";
        prueba[4][1]="E";
        
        
        
        int[] iniciales = new int[] {1,1,0,0};
       
        
        String[][] resultado;
        resultado = iniciar(prueba, iniciales);
        for(int i=0; i<resultado.length;i++)
            for (int j=0; j<2; j++){
                System.out.println("Bajo "+j+"Estado: "+ resultado[i][j]);
            }
        
        
        
    }
}
















