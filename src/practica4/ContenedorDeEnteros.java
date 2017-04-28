package practica4;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

public class ContenedorDeEnteros {
	private int raiz; // Direccion de la raiz en el fichero
	private int numElem; // Numero de elementos en el Multirrama
	private int Orden; // Orden del arbol B
	private int tamañoDatos; // Tamano de cada dato almacenado
	private String nombreFichero; // Fichero donde se almacenan los nodos
	private FicheroAyuda fichero; // Objeto para conversiones a tipo almacenado
	
	// Constructor
	public ContenedorDeEnteros() {
		fichero = new FicheroAyuda();
	}

	private class Nodo {
		private byte[][] clavei; // Claves [1..numEle]
		private int[] enlacei; // Enlaces [0..numEle]
		private int numElei; // Numero de datos en la pagina
		private int direccioni; // Direccion de la pagina en el fichero

		private Nodo() {
			direccioni = FicheroAyuda.dirNula;
			numElei = 0;
			// El tamaño depende del "Orden" del objeto donde se crea
			clavei = new byte[Orden][];
			enlacei = new int[Orden + 1];
		}

		private int tamaño() {
			int tam = 2 * Conversor.INTBYTES; // Cantidad base
			tam += (Orden - 1) * tamañoDatos; // Los datos
			tam += Orden * Conversor.INTBYTES; // Los enlaces
			return tam;
		}

		private byte[] clave(int i) { // Devuelve la clave
			return clavei[i - 1];
		}

		private void clave(int i, byte[] d) { // Establece la clave
			clavei[i - 1] = d;
		}

		private int enlace(int i) { // Devuelve el enlace
			return enlacei[i];
		}

		private void enlace(int i, int d) { // Establece el enlace
			enlacei[i] = d;
		}

		private int direccion() { // Devuelve la direccion donde se almacena
			return direccioni;
		}

		private void direccion(int d) { // Establece la direccion donde se almacena
			direccioni = d;
		}

		private int cardinal() { // Devuelve el numero de datos almacenados
			return numElei;
		}

		private void cardinal(int n) { // Establece el numero de datos almacenados
			numElei = n;
		}

		/**
		 * @return el nodo en forma de byte[]
		 */
		private byte[] aByte() {
			int tam = tamaño();
			byte[] res = new byte[tam];
			int pos = 0;
			pos = Conversor.añade(res, Conversor.aByte(direccioni), pos);
			pos = Conversor.añade(res, Conversor.aByte(numElei), pos);
			for ( int i = 0; i < numElei; i++) {
				pos = Conversor.añade(res, clavei[i], pos);
			}
			for ( int i = 0; i <= numElei; i++) {
				pos = Conversor.añade(res, Conversor.aByte(enlacei[i]), pos);
			}
			return res;
		}

		/**
		 * Inicializa el nodo partiendo de un byte[] previamente generado con aByte
		 * @param datos
		 */
		private void deByte(byte[] datos) {
			int leb = Conversor.INTBYTES; // Longitud de los enteros en bytes
			direccion(Conversor.aInt(Conversor.toma(datos, 0, leb)));
			numElei = Conversor.aInt(Conversor.toma(datos, leb, leb));
			int baseClaves = leb * 2;
			int baseEnlaces = baseClaves + (numElei) * tamañoDatos;
			for (int i = 0; i < numElei; i++) {
				clavei[i] = Conversor.toma(datos, baseClaves + i * tamañoDatos, tamañoDatos);
			}
			for (int i = 0; i <= numElei; i++) {
				byte[] dato = Conversor.toma(datos, baseEnlaces + i * leb, leb);
				enlacei[i] = Conversor.aInt(dato);
			}
		}

		/**
		 * 
		 * @param e dato a buscar en forma de byte[]
		 * @return la posicion de la clave menor que es mayor o igual a e
		 */
		private int buscar(int e) {
			int pos, prim, ulti, med;
			prim = 1;
			ulti = this.cardinal();
			while (prim <= ulti) {
				med = (prim + ulti) / 2;
				if (e == Conversor.aInt(clave(med))) {
					pos = med;
					return pos;
				}
				if (e < Conversor.aInt(clave(med))) {
					ulti = med - 1;
				} else {
					prim = med + 1;
				}
			}
			pos = prim - 1;
			return pos;
		}

		/**
		 * 
		 * @param e dato a buscar
		 * @return si el dato pasado esta o no en el nodo
		 */
		private boolean esta(int e) {
			int prim, ulti, med;
			prim = 1;
			ulti = this.cardinal();
			while (prim <= ulti) {
				med = (prim + ulti) / 2;
				if (e == Conversor.aInt(clave(med))) {
					return true;
				}
				if (e < Conversor.aInt(clave(med))) {
					ulti = med - 1;
				} else {
					prim = med + 1;
				}
			}
			return false;
		}

		private void insertar(byte[] e, int dir, int pos) {
			numElei++;
			for (int i = numElei - 1; i >= pos; i--) {
				clave(i + 1, clave(i));
				enlace(i + 1, enlace(i));
			}
			clave(pos, e);
			enlace(pos, dir);
		}

		private void extraer(int pos) {
			for (int i = pos; i < numElei; i++) {
				clave(i, clave(i + 1));
				enlace(i, enlace(i + 1));
			}
			numElei--;
		}
	}

	private class InfoPila {
		private Nodo nodo;
		private int pos;

		private InfoPila(Nodo n, int p) {
			nodo = n;
			pos = p;
		}
	}
	
	/**
	 * Numero de elementos del contenedor.
	 * 
	 * @return numero de elementos del contenedor.
	 */
	public int cardinal() {
		return numElem;
	}
	
	private Nodo leer(int dir) {
		Nodo n = new Nodo();
		n.deByte(fichero.leer(dir));
		return n;
	}

	private void escribir(Nodo n) {
		fichero.escribir(n.aByte(), n.direccion());
	}

	private boolean buscar(int e, Stack<InfoPila> pila) {
		int dirNodo, pos;
		Nodo nodo = new Nodo();
		dirNodo = raiz;
		pila.clear();
		while ( dirNodo != FicheroAyuda.dirNula ) {
			nodo = leer(dirNodo);
			pos = nodo.buscar(e);
			pila.add(new InfoPila(nodo, pos));
			if (nodo.esta(e)) return true;
			dirNodo = nodo.enlace(pos);
		}
		return false;
	}

	private boolean encuentra(int e) {
		Stack<InfoPila> pila = new Stack<InfoPila>();
		return buscar(e, pila);
	}

	/**
	 * Clase auxiliar que permite devolver una pareja formada por una clave y el enlace asociado
	 */
	private int minimoClaves;
	
	private class ParejaInsertar {
		private byte[] clave;
		private int direccion;
	}
	

	private ParejaInsertar particion_1_2(Nodo nodo) {
		ParejaInsertar pa = new ParejaInsertar();
		Nodo nuevoNodo = new Nodo();
		int ncnuevo = Orden / 2;
		int ncnodo = Orden - ncnuevo - 1;
		int dirNuevo = fichero.tomarPágina();
		nuevoNodo.direccion(dirNuevo);
		nuevoNodo.cardinal(ncnuevo);
		nuevoNodo.enlace(0, nodo.enlace(ncnodo + 1));
		// [xxxxx] [   ] => [xx  ] [xx  ]
		for (int i = 1; i <= nuevoNodo.cardinal(); i++) { 
			nuevoNodo.clave(i, nodo.clave(ncnodo + 1 + i));
			nuevoNodo.enlace(i, nodo.enlace(ncnodo + 1 + i));
		}
		pa.clave = nodo.clave(ncnodo + 1);
		pa.direccion = nuevoNodo.direccion();
		nodo.cardinal(ncnodo);
		escribir(nodo);
		escribir(nuevoNodo);
		return pa;
	}

	private void particion_2_3(Nodo padre, int posizq, Nodo izq, Nodo der) {
		int clavesRepartir = izq.cardinal() + der.cardinal() - 1;
		Nodo reg = new Nodo();
		int ncizq = (clavesRepartir) / 3;
		int ncreg = (clavesRepartir + 1) / 3;
		int ncder = (clavesRepartir + 2) / 3;
		int antncder = der.cardinal();
		int antncizq = izq.cardinal();
		// Se inserta en el padre una nueva clave y la nueva direccion
		reg.direccion(fichero.tomarPágina());
		padre.insertar(izq.clave(ncizq + 1), reg.direccion(), posizq + 1);
		// Pasamos datos de izq a reg: [xxx] [   ] => [xx ] [x  ]
		reg.cardinal(ncreg);
		reg.enlace(0, izq.enlace(ncizq + 1));
		for (int i = ncizq + 2; i <= antncizq; i++) {
			reg.clave(i - ncizq - 1, izq.clave(i));
			reg.enlace(i - ncizq - 1, izq.enlace(i));
		}
		izq.cardinal(ncizq);
		// Pasamos el dato del padre a la posicion correspondiente de reg
		reg.clave(antncizq - ncizq, padre.clave(posizq + 2));
		int posl = antncizq - ncizq;
		reg.enlace(posl, der.enlace(0)); // [x  ] [yyy] => [xy ] [ yy]
		for (int i = posl + 1; i <= ncreg; i++) {
			reg.clave(i, der.clave(i - posl));
			reg.enlace(i, der.enlace(i - posl));
		}
		int ncpas = antncder - ncder;
		// Pasamos al padre el valor correspondiente y compactamos der
		padre.clave(posizq + 2, der.clave(ncpas));
		der.enlace(0, der.enlace(ncpas)); // [ yy] => [yy ]
		for (int i = ncpas + 1; i <= antncder; i++) {
			der.clave(i - ncpas, der.clave(i));
			der.enlace(i - ncpas, der.enlace(i));
		}
		der.cardinal(ncder);
		escribir(izq); // [xx ] [xy ] [yy ]
		escribir(reg);
		escribir(der);
	}

	private void rotacionizqder(Nodo padre, int posizq, Nodo izq, Nodo der) {
		int clavesRepartir = izq.cardinal() + der.cardinal();
		int ncizq = (clavesRepartir) / 2;
		int ncder = clavesRepartir - ncizq;
		int ncpas = ncder - der.cardinal();
		int antncder = der.cardinal();
		// Hacemos hueco en nodo der: [yy  ] => [ yy ]
		der.cardinal(ncder);
		for (int i = antncder; i >= 1; i--) {
			der.clave(i + ncpas, der.clave(i));
			der.enlace(i + ncpas, der.enlace(i));
		}
		der.enlace(ncpas, der.enlace(0));
		// Rellenar el nodo der: [xxxx] [ yy ] => [xxx ] [xyy ]
		der.clave(ncpas, padre.clave(posizq + 1));
		for (int i = ncizq + 2; i <= izq.cardinal(); i++) {
			der.clave(i - (ncizq + 1), izq.clave(i));
			der.enlace(i - (ncizq + 1), izq.enlace(i));
		}
		der.enlace(0, izq.enlace(ncizq + 1));
		// Modificar el nodo padre
		padre.clave(posizq + 1, izq.clave(ncizq + 1));
		// Modificar el nodo izq
		izq.cardinal(ncizq);
		// Se escribe en el fichero los tres nodos
		escribir(padre);
		escribir(izq);
		escribir(der);
	}

	private void rotacionderizq(Nodo padre, int posizq, Nodo izq, Nodo der) {
		int clavesRepartir = izq.cardinal() + der.cardinal();
		int ncder = (clavesRepartir) / 2;
		int ncizq = clavesRepartir - ncder;
		int ncpas = der.cardinal() - ncder;
		int antncizq = izq.cardinal();
		// Pasamos la clave del padre y datos de der a izq
		izq.cardinal(ncizq);
		izq.clave(antncizq + 1, padre.clave(posizq + 1));
		izq.enlace(antncizq + 1, der.enlace(0));
		for (int i = 1; i < ncpas; i++) {  // [xx  ] [yyyy] => [xxy ] [ yyy]
			izq.clave(antncizq + 1 + i, der.clave(i));
			izq.enlace(antncizq + 1 + i, der.enlace(i));
		}
		// Pasamos clave al padre
		padre.clave(posizq + 1, der.clave(ncpas));
		// Quitamos hueco en der
		der.enlace(0, der.enlace(ncpas));
		for (int i = 1; i <= ncder; i++) { // [ yyy] => [yyy ]
			der.clave(i, der.clave(i + ncpas));
			der.enlace(i, der.enlace(i + ncpas));
		}
		der.cardinal(ncder);
		escribir(padre);
		escribir(izq);
		escribir(der);
	}

	private void recombinacion_2_1(Nodo padre, int posizq, Nodo izq, Nodo der) {
		// Bajamos la clave discriminante en el padre al final del izquierdo
		int antncizq = izq.cardinal();
		izq.cardinal(izq.cardinal() + 1 + der.cardinal());
		izq.clave(antncizq + 1, padre.clave(posizq + 1));
		// Pasamos el enlace cero de der a izq ya que no encaja en el bucle
		izq.enlace(antncizq + 1, der.enlace(0));
		// Pasamos el resto de claves y enlaces
		// [xx  ] [xx  ] => [xxxx] [    ]
		for (int i = 1; i <= der.cardinal(); i++) {
			izq.clave(antncizq + 1 + i, der.clave(i));
			izq.enlace(antncizq + 1 + i, der.enlace(i));
		}
		// Quitamos del padre la clave y el enlace a der
		padre.extraer(posizq + 1);
		escribir(izq);
		fichero.liberarPágina(der.direccion());
	}

	private void recombinacion_3_2(Nodo padre, int posReg, Nodo izq, Nodo reg, Nodo der) {
		int aRepartir = izq.cardinal() + reg.cardinal() + der.cardinal() + 1;
		int ncder = aRepartir / 2;
		int ncizq = aRepartir - ncder;
		int antncizq = izq.cardinal();
		int antncder = der.cardinal();
		// Rellenamos el hermano izquierdo
		izq.cardinal(ncizq);
		izq.clave(antncizq + 1, padre.clave(posReg));
		izq.enlace(antncizq + 1, reg.enlace(0));
		// [xx  ] [yy  ] => [xxy ] [ y  ]
		for (int i = antncizq + 2; i <= ncizq; i++) {
			izq.clave(i, reg.clave(i - antncizq - 1));
			izq.enlace(i, reg.enlace(i - antncizq - 1));
		}
		// Desplazamiento del hermano derecho para hacer hueco
		der.cardinal(ncder);
		int ncpas = ncder - antncder;
		for (int i = antncder; i >= 1; i--) { // [zz  ] => [ zz ]
			der.clave(i + ncpas, der.clave(i));
			der.enlace(i + ncpas, der.enlace(i));
		}
		der.enlace(ncpas, der.enlace(0));
		der.clave(ncpas, padre.clave(posReg + 1));
		// Rellenamos el hermano derecho
		// [ y  ] [ zz ] => [    ] [yzz ]
		for (int i = ncpas - 1; i >= 1; i--) {
			der.clave(i, reg.clave(reg.cardinal() + i - ncpas + 1));
			der.enlace(i, reg.enlace(reg.cardinal() + i - ncpas + 1));
		}
		der.enlace(0, reg.enlace(reg.cardinal() - ncpas + 1));
		// modificar el nodo padre
		fichero.liberarPágina(reg.direccion());
		escribir(izq);
		escribir(der);
		padre.extraer(posReg);
		padre.clave(posReg, reg.clave(reg.cardinal() - ncpas + 1));
	}

	private void crear(String ruta, int tamañoDatos, int Orden) {
		cerrar();
		this.tamañoDatos = tamañoDatos;
		this.Orden = Orden;
		Nodo nodo = new Nodo();
		nombreFichero = ruta;
		fichero.crear(nombreFichero, nodo.tamaño(), 4);
		raiz = FicheroAyuda.dirNula;
		numElem = 0;
		minimoClaves = (Orden + 1) / 2 - 1;
		fichero.adjunto(0, raiz);
		fichero.adjunto(1, numElem);
		fichero.adjunto(2, tamañoDatos);
		fichero.adjunto(3, Orden);
	}

	/**
	 * Crea un arbol B y lo asocia a un fichero.
	 * 
	 * @param ruta del fichero.
	 * @param Orden del arbol.
	 */
	public void crear(String ruta, int Orden) {
		crear(ruta, Conversor.INTBYTES, Orden);
	}

	/**
	 * Abre el arbol B almacenado y lo asocia al objeto.
	 * 
	 * @param ruta del fichero.
	 */
	public void abrir(String ruta) {
		fichero.abrir(ruta);
		raiz = fichero.adjunto(0);
		numElem = fichero.adjunto(1);
		tamañoDatos = fichero.adjunto(2);
		Orden = fichero.adjunto(3);
		minimoClaves = (Orden + 1) / 2 - 1;
	}

	/**
	 * Cierra el fichero asociado y disocia el objeto del fichero.
	 * 
	 */
	public void cerrar() {
		fichero.cerrar();
	}
	
	/**
	 * Deja el contenedor sin ningun elemento.
	 *  
	 */
	public void vaciar() {
		fichero.cerrar();
		crear(nombreFichero, Orden);
	}


	/**
	 *  añade al contenedor un nuevo elemento pasado por parametro.
	 *  
	 * @param o elemento a anadir.
	 * @return verdadero si lo anadio y falso en caso contrario.
	 */
	public boolean insertar(int o) {
		int alma = o;
		byte[] dato = Conversor.aByte(alma);
		Stack<InfoPila> pila = new Stack<InfoPila>();
		if (buscar(alma, pila)) return false; // No se admiten repetidos
		Nodo nodoActual = new Nodo();
		InfoPila info;
		ParejaInsertar pa = new ParejaInsertar();
		pa.clave = dato;
		pa.direccion = FicheroAyuda.dirNula;
		fichero.adjunto(1, ++numElem);
		if (!pila.empty()) { // El arbol no esta vacio
			info = (InfoPila) pila.pop();
			nodoActual = info.nodo;
			int pos = info.pos;
			nodoActual.insertar(pa.clave, pa.direccion, pos + 1);
			if (nodoActual.cardinal() < Orden) { // No hay sobrecarga
				escribir(nodoActual);
				return true;
			}
			while (!pila.empty()) { // Arreglar sobrecarga
				info = (InfoPila) pila.pop();
				Nodo der = new Nodo(), izq = new Nodo();
				Nodo padre = info.nodo;
				pos = info.pos;
				if (pos > 0) { // Tiene hermano izquierdo
					izq = leer(padre.enlace(pos - 1));
					if (izq.cardinal() < Orden - 1) { // Resuelto
						rotacionderizq(padre, pos - 1, izq, nodoActual);
						return true;
					}
				}
				if (pos < padre.cardinal()) { // Tiene hermano derecho
					der = leer(padre.enlace(pos + 1));
					if (der.cardinal() < Orden - 1) { // Resuelto
						rotacionizqder(padre, pos, nodoActual, der);
						return true;
					}
				}
				// No se puede rotar => se parte
				if (pos == 0) particion_2_3(padre, pos, nodoActual, der);
				else particion_2_3(padre, pos - 1, izq, nodoActual);
				if (padre.cardinal() < Orden) { // Resuelto
					escribir(padre);
					return true;
				}
				nodoActual = padre;
			}
			// Se parte la raiz
			pa = particion_1_2(nodoActual);
		}
		// Se crea una nueva raiz
		nodoActual.cardinal(1);
		nodoActual.enlace(0, raiz);
		nodoActual.clave(1, pa.clave);
		nodoActual.enlace(1, pa.direccion);
		nodoActual.direccion(fichero.tomarPágina());
		raiz = nodoActual.direccion();
		escribir(nodoActual);
		fichero.adjunto(0, raiz);
		return true;
	}

	/**
	 * Extrae del contenedor el elemento pasado por parametro, si no se encuentra no se altera el contenedor.
	 * 
	 * @param o elemento a extraer.
	 * @return verdadero si lo elimino y falso en caso contrario.
	 */
	public boolean extraer(int o) {
		int alma = o;
		Stack<InfoPila> pila = new Stack<InfoPila>();
		if (!buscar(alma, pila)) return false; // Salimos sin hacer nada
		fichero.adjunto(1, --numElem);
		InfoPila info = (InfoPila) pila.pop();
		Nodo nodoActual = info.nodo;
		int pos = info.pos;
		if (nodoActual.enlace(0) != FicheroAyuda.dirNula) {
			// Extraccion desde un nodo no hoja
			pila.add(new InfoPila(info.nodo, info.pos));
			// Hay que buscar el sucesor y cambiarlo
			LinkedList<InfoPila> cola = new LinkedList<InfoPila>();
			int dir = nodoActual.enlace(pos);
			do { // Descendemos por las ramas izquierdas
				nodoActual = leer(dir);
				dir = nodoActual.enlace(0);
				if (dir == FicheroAyuda.dirNula) pos = 1;
				else pos = 0;
				// Guardamos el camino en una cola
				cola.addLast(new InfoPila(nodoActual, pos));
			} while (dir != FicheroAyuda.dirNula);
			info = (InfoPila) pila.pop();
			// Se sustituye por el sucesor
			info.nodo.clave(info.pos, nodoActual.clave(1));
			// Se escribe por si no hay mas modificaciones
			escribir(info.nodo);
			pila.add(info);
			while (!cola.isEmpty()) {
				// Se pasa el camino de la cola a la pila
				nodoActual = ((InfoPila) cola.getFirst()).nodo;
				pila.add(cola.getFirst());
				cola.removeFirst();
			}
			info = (InfoPila) pila.pop();
			nodoActual = info.nodo;
			pos = info.pos;
		}
		// Extraccion en un nodo hoja
		nodoActual.extraer(pos);
		while (nodoActual.cardinal() < minimoClaves
				&& nodoActual.direccion() != raiz) {
			Nodo padre, der = new Nodo(), izq = new Nodo();
			info = (InfoPila) pila.pop();
			padre = info.nodo; // Se toma el padre de la pila
			pos = info.pos;
			if (pos < padre.cardinal()) { // Tiene hermano derecho
				der = leer(padre.enlace(pos + 1));
				if (der.cardinal() > minimoClaves) {
					rotacionderizq(padre, pos, nodoActual, der);
					return true;
				}
			}
			if (pos > 0) { // Tiene hermano izquierdo
				izq = leer(padre.enlace(pos - 1));
				if (izq.cardinal() > minimoClaves) {
					rotacionizqder(padre, pos - 1, izq, nodoActual);
					return true;
				}
			}
			// No se puede rotar => se recombina
			if (pos > 0 && pos < padre.cardinal()) 
				recombinacion_3_2(padre, pos, izq, nodoActual, der);
			else if (pos > 0)
				recombinacion_2_1(padre, pos - 1, izq, nodoActual);
			else recombinacion_2_1(padre, pos, nodoActual, der);
			nodoActual = padre;
		}
		if (nodoActual.cardinal() > 0) {
			// Se escribe el nodo, si tiene informacion
			escribir(nodoActual);
		}
		else { // La raiz se ha quedado sin datos
			raiz = nodoActual.enlace(0);
			fichero.liberarPágina(nodoActual.direccion());
			fichero.adjunto(0, raiz);
		}
		return true;
	}

	/**
	 * Busca un elemento en el arbol.
	 * 
	 * @param e elemento a buscar.
	 * @return verdadero si el valor pasado por parametro pertenece al contenedor y falso en caso contrario.
	 */
	public boolean buscar(int e) {
		return encuentra(e);
	}

	/**
	 * Devuelve un vector de enteros ordenados de menor a mayor con los elementos que se encuentren en el contenedor.
	 * 
	 * @return vector de enteros ordenados.
	 */
	
	// Vector y posicion para el metodo elementos
	
	public int[] elementos() {
		int[] vector = new int[numElem];
		int posicion = 0;
		Nodo nod = new Nodo();
		if (numElem > 0) {
			for (int i = 0; i < fichero.tamaño(); i++) {
				nod = leer(i);
				for (int j = 1; j <= nod.cardinal(); j++) {
					vector[posicion] = Conversor.aInt(nod.clave(j));
					posicion++;
				}
			}
		}
		Arrays.sort(vector);
		return vector;
	}
}