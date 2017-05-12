package practica4;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

        ContenedorDeEnteros[] arbolesB = new ContenedorDeEnteros[11];
        int[] ordenes = {5, 7, 9, 11, 20, 25, 55, 75, 105, 201, 301};
        
        // Creacion de los arboles b con los ordenes que le corresponden.
        for (int i = 0; i < arbolesB.length; i++) {
            arbolesB[i] = new ContenedorDeEnteros();
            arbolesB[i].crear("arbolb" + ordenes[i], ordenes[i]);
        }

        System.out.println("[+] Iniciando pruebas de tiempos");
        // Abrimos el fichero datos.dat
        RandomAccessFile datosDat = new RandomAccessFile("datos.dat", "r");

        // Abrimos el fichero datos_no.dat
        RandomAccessFile datosNoDat = new RandomAccessFile("datos_no.dat", "r");

        // Abrimos el fichero salida3.txt
        BufferedWriter salida4 = new BufferedWriter(new FileWriter("salida3.txt"));

        double init = 0; // Tiempo inicial
        double stop = 0; // Tiempo final

        int[] datos = new int[100000];
        for (int m = 0; m < 100000; m++) datos[m] = datosDat.readInt();
        datosDat.close();

        int[] noDatos = new int[20000];
        for (int n = 0; n < 20000; n++) noDatos[n] = datosNoDat.readInt();
        datosNoDat.close();

        salida4.write("[+] PRUEBA INSERCIONES");
        salida4.newLine();
        for (int i = 0; i < arbolesB.length; i++) {
            salida4.write("Orden " + ordenes[i] + " : ");
            salida4.newLine();
            init = System.nanoTime();
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10000; k++) {
                    arbolesB[i].insertar(datos[k + (j * 10000)]);
                }

                stop = System.nanoTime();
                salida4.write(Double.toString((stop - init) / 10000000.) + " ms");
                salida4.newLine();
                init = System.nanoTime();
            }
        }

        salida4.newLine();
        salida4.write("[+] PRUEBA EXTRACCIONES");
        salida4.newLine();
        for (int i = 0; i < arbolesB.length; i++) {
            salida4.write("Orden " + ordenes[i] + " : ");
            salida4.newLine();
            init = System.nanoTime();
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10000; k++) {
                    arbolesB[i].extraer(datos[k + (j * 10000)]);
                }

                stop = System.nanoTime();
                salida4.write(Double.toString((stop - init) / 10000000.) + " ms");
                salida4.newLine();
                init = System.nanoTime();
            }
        }

        for (int i = 0; i < arbolesB.length; i++) arbolesB[i].vaciar();

        salida4.newLine();
        salida4.write("[+] PRUEBA BÚSQUEDA EXITOSA");
        salida4.newLine();
        for (int i = 0; i < arbolesB.length; i++) {
            salida4.write("Orden " + ordenes[i] + " : ");
            salida4.newLine();
            int j = 0, w = 10;
            while (j < datos.length) {

                for (int k = 0; k < 10000; k++, j++) {
                    arbolesB[i].insertar(datos[j]);
                }

                init = System.nanoTime();

                for (int k = 0; k < 500; k++) {
                    for (int l = 0; l < j; l++) {
                        arbolesB[i].buscar(datos[l]);
                    }
                }

                stop = System.nanoTime();
                salida4.write(Double.toString((stop - init) / (w * 1000000. * 500.)) + " ms");
                w += 10;
                salida4.newLine();
            }
        }

        for (int i = 0; i < arbolesB.length; i++) arbolesB[i].vaciar();

        salida4.newLine();
        salida4.write("[+] PRUEBA BÚSQUEDA NO EXITOSA");
        salida4.newLine();
        for (int t = 0; t < arbolesB.length; t++) {
            salida4.write("Orden " + ordenes[t] + " : ");
            salida4.newLine();
            for (int i = 0; i < 10; i++) {

                for (int j = 0; j < 10000; j++) {
                    arbolesB[t].insertar(datos[j + (i * 10000)]);
                }

                init = System.nanoTime();

                for (int k = 0; k < 500; k++) {
                    for (int l = 0; l < 20000; l++) {
                        arbolesB[t].buscar(noDatos[l]);
                    }
                }

                stop = System.nanoTime();
                salida4.write(Double.toString((stop - init) / (20000000 * 500.)) + " ms");
                salida4.newLine();
            }
        }
        salida4.close();
        System.out.println("[!] FIN DE LA PRUEBA");
    }
}
