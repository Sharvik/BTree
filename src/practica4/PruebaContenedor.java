package practica4;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PruebaContenedor {

    public static void main(String[] args) throws Exception {
        ContenedorDeEnteros a = new ContenedorDeEnteros();
        a.crear("temp", 10);
        int[] v;
        System.out.println("El contenedor a tiene " + a.cardinal() + " elementos.");
        for (int i = 0; i < 10; i++) {
            a.insertar(i);
            a.buscar(i);
        }
        v = a.elementos();
        for (int i = 0; i < a.cardinal(); i++) {
            System.out.println(v[i]);
        }
        a.vaciar();
        for (int i = 0; i < 100; i++) {
            a.insertar(i);
            a.extraer(i);
        }
        a.cerrar();
        a.abrir("temp");
        System.out.println("El contenedor a tiene " + a.cardinal() + " elementos.");
        a.cerrar();

        ContenedorDeEnteros[] arbolesb = new ContenedorDeEnteros[11];
        int[] Orden = {5, 7, 9, 11, 20, 25, 55, 75, 105, 201, 301};

        for (int x = 0; x < Orden.length; x++) {
            ContenedorDeEnteros b = new ContenedorDeEnteros();
            b.crear("pruebab", Orden[x]);
            // Creamos un array con 10 elementos ordenadors para comprobar las inserciones
            int[] ordenado = {0, 4, 6, 8, 10, 12, 15, 17, 20, 25};
            // Creamos un array con 10 elementos desordenados para insertar en el contenedor
            int[] desordenado = {20, 15, 10, 6, 0, 25, 17, 12, 8, 4};
            // Insertamos todos los elementos en el contenedor
            for (int i = 0; i < desordenado.length; i++) {
                if (!b.insertar(desordenado[i])) {
                    System.out.println("El elemento '" + desordenado[i] + "' no se ha insertado");
                }
            }
            // Creamos un array con los elementos del contenedor
            int[] array = b.elementos();
            for (int i = 0; i < array.length; i++) {
                // Comprobamos que esten todos los elementos insertados
                if (ordenado[i] != array[i]) {
                    System.out.println("No se ha creado el array de elementos correctamente");
                }
                // Buscamos todos los elementos del contenedor
                if (!b.buscar(desordenado[i])) {
                    System.out.println("No se ha encontrado el elemento '" + desordenado[i] + "' en el contenedor");
                }
                if (!b.extraer(desordenado[i])) {
                    System.out.println("No se ha extraido el elemento '" + desordenado[i] + "' del contenedor");
                }
            }

            // Numero de elementos del contenedor
            if (b.cardinal() != 0) {
                System.out.println("El numero de elementos del contenedor \"a\" es erroneo.");
            }
            // Vaciamos el contenedor
            b.vaciar();
            // Comprobamos que el contenedor este vacio
            if (b.cardinal() != 0) {
                System.out.println("El contenedor no se ha vaciado correctamente");
            }

            b.crear("pruebab", Orden[x]);

            b.insertar(0);
            // Creamos un array con los elementos del contenedor
            array = b.elementos();
            // Comprobamos que el contenedor tenga un elemento
            if (b.cardinal() != 1) {
                System.out.println("No se ha insertado el elemento '" + array[0] + "' en un contenedor vacio");
            }
            // Extraemos el '0' del contenedor
            b.extraer(0);
            // Creamos un array con los elementos del contenedor
            array = b.elementos();
            // Comprobamos que el contenedor este vacio
            if (b.cardinal() != 0) {
                System.out.println("No se ha extraido el elemento '" + array[0] + "' en un contenedor con un elemento");
            }
            // Insertamos el elemento '25' en la ultima posicion del contenedor
            b.insertar(25);
            // Comprobamos que inserta el ultimo elemento anteriormente extraido
            if (b.cardinal() != 1) {
                System.out.println("El elemento anteriormente extraido no lo inserta en la ultima posicion del contenedor");
            }
            // Buscar un elemento que no estÃ¡ en el contenedor
            if (a.buscar(11)) {
                System.out.println("Se encontrÃ³ un elemento que no estÃ¡ en el contenedor");
            }
            // Extraemos un elemento que no estÃ¡ en el contenedor
            if (a.extraer(19)) {
                System.out.println("Se extrajo un elemento que no estÃ¡ en el contenedor");
            }
            b.cerrar();
        }

        // Creacion de los arboles b con los ordenes que le corresponden.
        for (int i = 0; i < arbolesb.length; i++) {
            arbolesb[i] = new ContenedorDeEnteros();
            arbolesb[i].crear("arbolb" + Orden[i], Orden[i]);
        }

        // Comienzan las pruebas de tiempos.
        try {
            System.out.println("Ejecucion de pruebas:\n");
            RandomAccessFile datosDat = new RandomAccessFile("datos.dat", "r"); 	// Permite leer el fichero datos.dat
            RandomAccessFile datosNoDat = new RandomAccessFile("datos_no.dat", "r");	// Permitimos leer el fichero datos_no.dat
            BufferedWriter salida4 = new BufferedWriter(new FileWriter("salida4.txt"));	// Permite escribir en el fichero salida			// Inicializacion de variables

            double princ = 0;	// Almacena el tiempo inicial del sistema en milisegundos
            double fin = 0;	// Almacena el tiempo final del sistema en milisegundos
            double time = 0;	// Almacena el tiempo final (fin-princ)

            // Creamos dos vectores, uno con los elementos de datos.dat y otro con los elementos de datos_no.dat
            int[] datos = new int[100000];
            for (int m = 0; m < 100000; m++) {
                datos[m] = datosDat.readInt();
            }
            datosDat.close();

            int[] noDatos = new int[20000];
            for (int n = 0; n < 20000; n++) {
                noDatos[n] = datosNoDat.readInt();
            }
            datosNoDat.close();

            //Realizamos las inserciones y calculamos los tiempos promedios por cada 10000 inserciones
            salida4.write("PRUEBA INSERCIONES: ");
            salida4.write("Tiempos de las inserciones en el contenedor: \n");
            for (int t = 0; t < arbolesb.length; t++) {
                salida4.newLine();
                salida4.write("Orden " + Orden[t] + ":\n");
                salida4.newLine();
                princ = System.nanoTime();	//	Inicializamos con el tiempo del sistema
                for (int i = 0; i < 10; i++) {	//Tenemos en cuenta 100000 elementos
                    for (int j = 0; j < 10000; j++) {
                        arbolesb[t].insertar(datos[j + (i * 10000)]);
                    }
                    
                    fin = System.nanoTime();
                    time = (fin - princ) / 10000000;
                    salida4.write(Double.toString(time) + " ms");
                    salida4.newLine();
                    time = 0;
                    princ = System.nanoTime();	// Inicializamos con el tiempo del sistema
                }
            }

            //Comenzamos el algoritmo para las extracciones y el calculo de sus tiempos
            salida4.write("\n");
            salida4.write("PRUEBA EXTRACCIONES:");
            salida4.write("Tiempos de las extracciones del contenedor: \n");
            for (int t = 0; t < arbolesb.length; t++) {
                salida4.newLine();
                salida4.write("Orden " + Orden[t] + ":\n");
                salida4.newLine();
                princ = System.nanoTime();	//	Inicializamos con el tiempo del sistema
                for (int j = 0; j < datos.length; j++) {	// Leemos todo los elementos del fichero datos.dat
                    arbolesb[t].extraer(datos[j]);	// Extraemos el primer valor del contenedor
                    if (arbolesb[t].cardinal() % 10000 == 0) {	//Cada vez que insertemos 10000 palabras calculamos el tiempo medio
                        fin = System.nanoTime();
                        time = (fin - princ) / 10000000;
                        salida4.write(Double.toString(time) + " ms");
                        salida4.newLine();
                        time = 0;
                        princ = System.nanoTime();	//	Inicializamos con el tiempo del sistema
                    }
                }
            }

            // Vaciamos los contenedores con el fin de insertar de nuevo los elementos
            for (int t = 0; t < arbolesb.length; t++) {
                arbolesb[t].vaciar();
            }
            //	Comenzamos el algoritmo para las busquedas exitosas y el calculo de sus tiempos 
            salida4.write("\n");
            salida4.write("Tercera prueba: \nTiempos de las busquedas exitosas del contenedor: \n");

            for (int t = 0; t < arbolesb.length; t++) {
                salida4.write("Orden " + Orden[t] + ":\n");
                for (int k = 0; k < datos.length; k++) {	// Leemos todo los elementos del fichero datos.dat
                    arbolesb[t].insertar(datos[k]);
                    if (arbolesb[t].cardinal() % 10000 == 0 && k != 0) {
                        int[] z = arbolesb[t].elementos();	// Creamos un array con los elementos del contenedor
                        princ = System.currentTimeMillis();	// Inicializamos el tiempo de busqueda
                        for (int m = 0; m < k; m++) {
                            arbolesb[t].buscar(z[m]);
                        }
                        fin = System.currentTimeMillis();
                        time = (fin - princ) / (k / 1000);
                        salida4.write(Double.toString(time) + " ms");
                        salida4.newLine();
                        time = 0;
                    }
                }
            }

            System.out.println("Busquedas exitosas realizadas --> Proceso total: 75%");
            // Vaciamos los contenedores con el fin de insertar de nuevo los elementos
            for (int t = 0; t < arbolesb.length; t++) {
                arbolesb[t].vaciar();
            }

            // Comenzamos el algoritmo para las busquedas infructuosas y el cÃƒÂ¡lculo de sus tiempos 
            salida4.write("\n");
            salida4.write("Cuarta prueba: \nTiempos de las busquedas infructuosas del contenedor: \n");
            for (int t = 0; t < arbolesb.length; t++) {
                salida4.write("Orden " + Orden[t] + ":\n");
                for (int h = 0; h < datos.length; h++) {	// Leemos todo los elementos del fichero datos.dat
                    arbolesb[t].insertar(datos[h]);
                    if (arbolesb[t].cardinal() % 10000 == 0) {
                        princ = System.nanoTime();
                        for (int y = 0; y < 20000; y++) {	// Leemos los 20000 elementos del fichero datos_no.dat
                            arbolesb[t].buscar(noDatos[y]);
                        }
                        fin = System.nanoTime();
                        time = (fin - princ) / (20000000);
                        salida4.write(Double.toString(time) + " ms");
                        salida4.newLine();
                        time = 0;
                    }
                }
            }
            salida4.close();
            System.out.println("P4: Busquedas infructuosas realizadas --> Proceso total: 100%");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
