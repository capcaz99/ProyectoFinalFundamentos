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
    public static int num_Estados;
    //Para convertir de no determinsita a determinista a determinista.
    //Arreglo en el cual se introducirán todos los estados iniciales del autómata inicial para despuñes crear uno solo. 
    public static int[] edosini = new int[]{1};    
    //Arreglo en el que se introducirán los nuevos estados y sus transiciones al momento de convertir.
    public static String trans[][]; 
    //Contador que me indica en que valor de trans voy.
    public static int cont=0; 
    public static int aceptacionNueva[];
    public static int ultimo;
    
    
    

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
            trans = new String [num_Estados*3][3];
        }
        catch (IOException ex) {
            Logger.getLogger(AFD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean revisarDeterminismo(){
        //Este método revisa si el autómata inicial es determinista o no determinista. 
        //Regresa una variable booleana llamada noDeterminista.
        //Si es verdadera significa que el autómata es no determinista. 
        
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
      //Este método inicia con el proceso de conversión de no determinista a determinista.
     
        //Juntar todos los estados iniciales en un string. 
         
         StringBuilder builder = new StringBuilder();
        char letra;
        for(int i=0; i<edosini.length; i++){
            if(edosini[i] == 1){
                letra = NumeroALetra(i);
                builder.append(letra);
            }
        }
         
        //Introducir el nuevo estado inicial en trans.
        if(cont<trans.length){
               trans[cont][0] = builder.toString();
               cont++;
         }else{
              trans = agrandarArreglo(trans);
              trans[cont][0] =  builder.toString();
              cont++;
        }
        
        llenarTransiciones();
        
        aceptacionNueva = aceptacion();
        
        return renombrarEstados();
 }
     
    public static void llenarTransiciones() {
        //Este arreglo llena el arreglo trans con las trasnciiones de los nuevos estados generados a partir de el o los de inicio. 
        int num;
        //Recorrer todo el arreglo de trans para llenar transiciones de estado inicial y de nuevos estados generados. 
        
        for (int i = 0; i < trans.length; i++) {
            //Si el elemento es vacío no hagas nada. 
            if (trans[i][0] == null) {
            } else {
                //Si el elemento es simple. 
                if (trans[i][0].length() == 1) {
                    //Llenar transiciones de este elemento con transiciones del autómata original 
                    num = letraANumero(trans[i][0]);
                    trans[i][1] = transiciones[num][0];
                    //Verificar si el estado obtenido es el estado sumidero para no introducirlo en el arreglo trans. 
                    if (trans[i][1].equals(Character.toString(NumeroALetra(num_Estados - 1)))) {
                    } else {
                        //si no es el estado sumidero verificar si ya está en el arreglo
                        //Si no está se introduce. 
                        if (revisarExistencia(trans[i][1]) == false) {
                            //Verificar que en el arreglo haya aun espacio
                            if (cont < trans.length) {

                                trans[cont][0] = trans[i][1];

                                cont++;
                            } else {
                                //Si no hay espacio se agranda el arreglo y después se introduce el nuevo estado. 
                                trans = agrandarArreglo(trans);
                                trans[cont][0] = trans[i][1];
                                cont++;
                            }
                        }
                    }
                    trans[i][2] = transiciones[num][1];
                    if (trans[i][2].equals(Character.toString(NumeroALetra(num_Estados - 1)))) {
                    } else {
                        if (revisarExistencia(trans[i][2]) == false) {
                            if (cont < trans.length) {
                                trans[cont][0] = trans[i][2];
                                cont++;
                            } else {
                                trans = agrandarArreglo(trans);
                                trans[cont][0] = trans[i][2];
                                cont++;
                            }
                        }
                    }
                //Si el estado es compuesto
                } else {
                    
                    //Separar el estado compuesto en estados separados. 
                    char[] separado = trans[i][0].toCharArray();

                    String tra0[] = new String[separado.length]; //Transiciones  de los estados en separado bajo cero
                    String tra1[] = new String[separado.length];//Transiciones  de los estados en separado bajo uno
                    char movi[][] = new char[separado.length][2]; // Transiciones con letra de los estados separados
                    
                    //Copiar transiciones de elementos en separado a tra0 y tra1.
                    int r = 0;
                    for (int k = 0; k < separado.length; k++) {

                        tra0[k] = transiciones[letraANumero(Character.toString(separado[k]))][0];

                        tra1[k] = transiciones[letraANumero(Character.toString(separado[k]))][1];

                    }
                 //Ordenar tra0 y tra1 y eliminar repetidos. 

                    tra0 = OrdenayDuplicados(tra0);
                    tra1 = OrdenayDuplicados(tra1);

                    //Juntar letras y crear nuevo estado
                    String e0 = crearEstado(tra0);
                    String e1 = crearEstado(tra1);

                    
                    
                    if (cont < trans.length) {
                        //si no es el estado sumidero verificar si ya está en el arreglo
                        if (e0.equals(Character.toString(NumeroALetra(num_Estados - 1)))) {
                        } else {
                            //Si no está se introduce.
                            if (revisarExistencia(e0) == false) {
                                trans[cont][0] = e0;
                                cont++;
                            }
                        }

                        if (e1.equals(Character.toString(NumeroALetra(num_Estados - 1)))) {
                        } else {
                            if (revisarExistencia(e1) == false) {
                                trans[cont][0] = e1;
                                cont++;
                            }
                        }
                    } else {
                        trans = agrandarArreglo(trans);
                        if (e0.equals(Character.toString(NumeroALetra(num_Estados - 1)))) {
                        } else {
                            if (revisarExistencia(e0) == false) {
                                trans[cont][0] = e0;
                                cont++;
                            }
                        }
                        if (e0.equals(Character.toString(NumeroALetra(num_Estados - 1)))) {
                        } else {
                            if (revisarExistencia(e1) == false) {
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
    
    public static String[][] renombrarEstados(){
         //Agregar estado vacío
         
         int i=0;
         String ult;
         int j= 0;
         
         
         //Convertir estados finales originales por nuevo estado final 
         
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
         //Este método cambia el nombre original del estado por la letra que le corresponde segúin su índice. 
         //Revisa en los tres renglones de trans para cambiar todas las ocurrencias del mismo.
         //Estado: El nombre del estado que voy a cambiar por "letra"
         
         for(int i=0; i<=ult; i++){
             
             for (int j=1; j<3; j++){
                 
                 if(trans[i][j].equals(estado)){
                     trans[i][j] = letra;
                     
                 }
                    
             }
         }
     }
     
    public static String[][] agregarUno(String[][] arre){
        //Este método agrega crea un arreglo más grande por uno del arreglo que se le envía y copia todo el arreglo del argumento en el nuevo.
     String[][] arreg = new String[arre.length+1][3];
    for(int i=0; i<arre.length; i++){
         arreg[i][0] = arre [i][0];
         arreg[i][1] = arre [i][1];
         arreg[i][2] = arre[i][2];
    }
    return arre;
     
  }
     
    public static int[] aceptacion(){
        //Este método llena el nuevo arreglo de acpetaciones del nuevo autómata determinista.
        int i=0;
        //Encontrar el último elemento de trans ya, por su tamaño, tiene varios espacios vacíos. 
        //Se utiliza el índice del útlimo elemento para determinar el tamaño del nuevo arreglo de aceptación. 
        while(trans[i][0] != null && i<trans.length)
             i++;
         if(trans[i][0] == null)
             ultimo = i;
         else{
             trans = agregarUno(trans);
             ultimo = i+1;
         }
     int[] acep = new int[ultimo+1];
     int cont = 0;
     for(int j=0; j<trans.length; j++){
           //LLenar el nuevo arreglo de aceptación 
            if(trans[j][0] == null){
            }else{
                if(trans[j][0].length() == 1){
                    //Si es simple el estado se copia la aceptación original 
                    acep[cont] = aceptacion[letraANumero(trans[j][0])];
                    cont++;
                }else{
                    //Si es compuesto el estado se hace un or de las acpetaciones de los estado por los que está compuesto.
                    //Si se encuentra uno sabemos que ya será todo uno por lo que se detiene la búsqueda y se coloca uno
                    //SI no se encuentra se coloca cero. 
                    char[] sep = trans[j][0].toCharArray();
                    int nueace = 0;
                    int n = 0;
                    while(nueace != 1 && n<sep.length){
                        nueace = aceptacion[letraANumero(Character.toString(sep[n]))];
                        n++;
                    }
                    acep[cont] = nueace;
                    cont++;
                }
             }
        }
     return acep;
    
    } 
    
    public static String crearEstado(String[] tra){
        //Este método crea la transición de los estados compuestos.
        
        
        //Se colocan las transiciones recibidas en tra en un arreglo separadas todas en simples. 
           char transSeparadas[] = new char[100];  
                 int e = 0;
                 for(int n=0; n<tra.length; n++){
                     int y = tra[n].length();
                     if( y == 1){
                         //Si es simple se copia directamente.
                         transSeparadas[e] = tra[n].charAt(0);
                         
                         e++;
                     }else{
                         //Si es compuesta se separa y se copian simples. 
                         char letras[] = new char[y];
                         letras = tra[n].toCharArray();
                         for(int t=0; t<y; t++){
                             transSeparadas[e]=letras[t];
                             
                             e++;
                         }
                     }
                 }
       
         
        String[] estado;
        //Se eliminan los estados repteidos y se ordenan por orden alfabético.
        estado = duplicadosCharArray(transSeparadas);
        
        //Se juntan en un String todos los estados resultantes.
        String a = new String();
        StringBuilder builder = new StringBuilder();
        //Se eliminan las letras correspondientes al estado sumidero. 
        if(revisarFinal(estado)){
            //Si el estado está solamente compuesto por el sumidero se coloca la letra del sumidero. 
            a = Character.toString(NumeroALetra(num_Estados-1));
        }else{
            //Si tienen otros estados se elimina el estado sumidero. 
        for(int j=0; j<estado.length; j++){
            if(letraANumero(estado[j])+1 != num_Estados)
                builder.append(estado[j]);
        }
        a = builder.toString();
        }
        return a;
        
         
     }
     
    public static boolean revisarFinal(String[] arre){
        //Este estado verifica si los estados enviados en arre están solamente compuestos por el estado sumidero
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
        //Este método agranda el arreglo que se envía en los parámetros ya que se acabó el espacio para trabajar con él. 
    String[][] arre = new String[arreglo.length*2][3];
    for(int i=0; i<arreglo.length; i++){
         arre[i][0] = arreglo [i][0];
         arre[i][1] = arreglo [i][1];
         arre[i][2] = arreglo [i][2];
    }
    return arre;
  } 
    
    public static String[] duplicadosCharArray(char[] array) {
        //Este método elimina los elementos duplicados en array por medio de un Set. 
        String[] A = new String[array.length];
        for(int n=0; n<array.length; n++){
            A[n]=Character.toString(array[n]);
        }
        Set<String> set = new HashSet<String>(Arrays.asList(A));
        A = set.toArray(new String[set.size()]);
         Arrays.sort(A);
         
         
         
         
           //Al introducirlos en un set y despueés convertilos en un arreglo de strings se produce un ezpacio vacío en el primer elemento del 
          // arreglo por lo que tenemos que crear un nuevo arreglo y copiar el arreglo original eliminando ese espacio vacío. 
         String[] B = new String[A.length-1];
         
         
         
         for(int i=0; i<B.length; i++)
             B[i]=A[i+1];
             
         
         
          return B;
            
           
     }
    
    public static boolean revisarExistencia(String letra){
        //Este método verifica si el estado que se está enviadno en letra exista ya en el primer rengón de trans. 
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
        //Este método elimina los elementos duplicados en array por medio de un Set.
         Set<String> set = new HashSet<String>(Arrays.asList(A));
         A = set.toArray(new String[set.size()]); 
         
         Arrays.sort(A);
         return A;
     }
     
    public static int letraANumero(String letra){
        //Este método nos regresa el valor númerico de la localidad del arreglo dada una letra. 
        //Si incertamos, por ejemplo, la letra A nos regrea 0 porque la paosición cero de los arreglos de trans y de acpetación 
        //se refieren a la A
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
        //Este método nos regresa la letra dado un número
        //Si incertamos, por ejemplo, el 0 nos regresa una A porque en los arreglos el estado A es la posición 0.
        
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
       
        String[][] determinista;
        leer();
        if(revisarDeterminismo())
            determinista = convertirDeterminista(); //Arreglo de transiciones. Aceptación nueva está en aceptacionNueva[];
        else{
            //Como ya es determinista reducir.
        }
    }
}
















