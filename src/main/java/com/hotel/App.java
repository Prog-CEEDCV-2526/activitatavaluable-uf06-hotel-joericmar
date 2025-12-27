package com.hotel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Gestió de reserves d'un hotel.
 */
public class App {

    // --------- CONSTANTS I VARIABLES GLOBALS ---------

    // Tipus d'habitació
    public static final String TIPUS_ESTANDARD = "Estàndard";
    public static final String TIPUS_SUITE = "Suite";
    public static final String TIPUS_DELUXE = "Deluxe";

    // Serveis addicionals
    public static final String SERVEI_ESMORZAR = "Esmorzar";
    public static final String SERVEI_GIMNAS = "Gimnàs";
    public static final String SERVEI_SPA = "Spa";
    public static final String SERVEI_PISCINA = "Piscina";

    // Capacitat inicial
    public static final int CAPACITAT_ESTANDARD = 30;
    public static final int CAPACITAT_SUITE = 20;
    public static final int CAPACITAT_DELUXE = 10;

    // IVA
    public static final float IVA = 0.21f;

    // Scanner únic
    public static Scanner sc = new Scanner(System.in);

    // HashMaps de consulta
    public static HashMap<String, Float> preusHabitacions = new HashMap<String, Float>();
    public static HashMap<String, Integer> capacitatInicial = new HashMap<String, Integer>();
    public static HashMap<String, Float> preusServeis = new HashMap<String, Float>();

    // HashMaps dinàmics
    public static HashMap<String, Integer> disponibilitatHabitacions = new HashMap<String, Integer>();
    public static HashMap<Integer, ArrayList<String>> reserves = new HashMap<Integer, ArrayList<String>>();

    // Generador de nombres aleatoris per als codis de reserva
    public static Random random = new Random();

    // --------- MÈTODE MAIN ---------

    /**
     * Mètode principal. Mostra el menú en un bucle i gestiona l'opció triada
     * fins que l'usuari decideix eixir.
     */
    public static void main(String[] args) {
        inicialitzarPreus();

        int opcio = 0;
        do {
            mostrarMenu();
            opcio = llegirEnter("Seleccione una opció: ");
            gestionarOpcio(opcio);
        } while (opcio != 6);

        System.out.println("Eixint del sistema... Gràcies per utilitzar el gestor de reserves!");
        sc.close();
    }

    // --------- MÈTODES DEMANATS ---------

    /**
     * Configura els preus de les habitacions, serveis addicionals i
     * les capacitats inicials en els HashMaps corresponents.
     */
    public static void inicialitzarPreus() {
        // Preus habitacions
        preusHabitacions.put(TIPUS_ESTANDARD, 50f);
        preusHabitacions.put(TIPUS_SUITE, 100f);
        preusHabitacions.put(TIPUS_DELUXE, 150f);

        // Capacitats inicials
        capacitatInicial.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        capacitatInicial.put(TIPUS_SUITE, CAPACITAT_SUITE);
        capacitatInicial.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Disponibilitat inicial (comença igual que la capacitat)
        disponibilitatHabitacions.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        disponibilitatHabitacions.put(TIPUS_SUITE, CAPACITAT_SUITE);
        disponibilitatHabitacions.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Preus serveis
        preusServeis.put(SERVEI_ESMORZAR, 10f);
        preusServeis.put(SERVEI_GIMNAS, 15f);
        preusServeis.put(SERVEI_SPA, 20f);
        preusServeis.put(SERVEI_PISCINA, 25f);
    }

    /**
     * Mostra el menú principal amb les opcions disponibles per a l'usuari.
     */
    public static void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Reservar una habitació");
        System.out.println("2. Alliberar una habitació");
        System.out.println("3. Consultar disponibilitat");
        System.out.println("4. Consultar dades d'una reserva");
        System.out.println("5. Consultar reserves per tipus");
        System.out.println("6. Ixir");
    }

    /**
     * Processa l'opció seleccionada per l'usuari i crida el mètode corresponent.
     */
    public static void gestionarOpcio(int opcio) {
        switch (opcio) {
            case 1:
                reservarHabitacio();
                break;
            case 2:
                alliberarHabitacio();
                break;
            case 3:
                consultarDisponibilitat();
                break;
            case 4:
                obtindreReserva();
                break;
            case 5:
                obtindreReservaPerTipus();
                break;
            case 6:
                // Ixir - ja es gestiona al main
                break;
            default:
                System.out.println("Opció no vàlida");
        }
    }

    /**
     * Gestiona tot el procés de reserva: selecció del tipus d'habitació,
     * serveis addicionals, càlcul del preu total i generació del codi de reserva.
     */
    public static void reservarHabitacio() {
        System.out.println("\n===== RESERVAR HABITACIÓ =====");

        // Seleccionar tipus d'habitació disponible
        String tipus = seleccionarTipusHabitacioDisponible();
        if (tipus == null) {
            System.out.println("No s'ha pogut completar la reserva.");
            return;
        }

        // Seleccionar serveis addicionals
        ArrayList<String> serveis = seleccionarServeis();

        // Calcular preu total
        float preuTotal = calcularPreuTotal(tipus, serveis);

        // Generar codi de reserva únic
        int codi = generarCodiReserva();

        // Crear llista amb les dades de la reserva
        ArrayList<String> dadesReserva = new ArrayList<>();
        dadesReserva.add(tipus); // Posició 0: tipus d'habitació
        dadesReserva.add(String.format("%.2f", preuTotal)); // Posició 1: preu total

        // Posicions 2-5: serveis (afegir fins a 4, posar null si no n'hi ha)
        for (int i = 0; i < 4; i++) {
            if (i < serveis.size()) {
                dadesReserva.add(serveis.get(i));
            } else {
                dadesReserva.add(null);
            }
        }

        // Guardar la reserva al HashMap
        reserves.put(codi, dadesReserva);

        // Actualitzar la disponibilitat
        int disponiblesActuals = disponibilitatHabitacions.get(tipus);
        disponibilitatHabitacions.put(tipus, disponiblesActuals - 1);

        // Mostrar resum de la reserva
        System.out.println("\n✅ Reserva creada amb èxit!");
        System.out.println("Codi de reserva: " + codi);
        System.out.println("Tipus d'habitació: " + tipus);
        System.out.println("Preu total (amb IVA): " + String.format("%.2f", preuTotal) + "€");
        if (!serveis.isEmpty()) {
            System.out.println("Serveis addicionals: " + String.join(", ", serveis));
        }
    }

    /**
     * Pregunta a l'usuari un tipus d'habitació en format numèric i
     * retorna el nom del tipus.
     */
    public static String seleccionarTipusHabitacio() {
        System.out.println("\nSeleccione tipus d'habitació:");
        System.out.println("1. " + TIPUS_ESTANDARD);
        System.out.println("2. " + TIPUS_SUITE);
        System.out.println("3. " + TIPUS_DELUXE);

        int opcio = llegirEnter("Opció (1-3): ");

        switch (opcio) {
            case 1:
                return TIPUS_ESTANDARD;
            case 2:
                return TIPUS_SUITE;
            case 3:
                return TIPUS_DELUXE;
            default:
                System.out.println("Opció no vàlida.");
                return null;
        }
    }

    /**
     * Mostra la disponibilitat i el preu de cada tipus d'habitació,
     * demana a l'usuari un tipus i només el retorna si encara hi ha
     * habitacions disponibles. En cas contrari, retorna null.
     */
    public static String seleccionarTipusHabitacioDisponible() {
        System.out.println("\nTipus d'habitació disponibles:");
        mostrarInfoTipus(TIPUS_ESTANDARD);
        mostrarInfoTipus(TIPUS_SUITE);
        mostrarInfoTipus(TIPUS_DELUXE);

        String tipus = seleccionarTipusHabitacio();
        if (tipus != null) {
            int disponibles = disponibilitatHabitacions.get(tipus);
            if (disponibles > 0) {
                return tipus;
            } else {
                System.out.println("No queden habitacions disponibles del tipus " + tipus);
            }
        }
        return null;
    }

    /**
     * Permet triar serveis addicionals (entre 0 i 4, sense repetir) i
     * els retorna en un ArrayList de String.
     */
    public static ArrayList<String> seleccionarServeis() {
        ArrayList<String> serveisSeleccionats = new ArrayList<>();

        System.out.println("\nServeis addicionals (màxim 4, sense repetir):");

        boolean afegirMes = true;

        while (afegirMes && serveisSeleccionats.size() < 4) {
            System.out.println("\n0. Finalitzar");
            System.out.println("1. " + SERVEI_ESMORZAR + " (10€)");
            System.out.println("2. " + SERVEI_GIMNAS + " (15€)");
            System.out.println("3. " + SERVEI_SPA + " (20€)");
            System.out.println("4. " + SERVEI_PISCINA + " (25€)");

            System.out.print("Seleccione una opció (0-4): ");
            int opcio = sc.nextInt();
            sc.nextLine(); // Netejar buffer

            if (opcio == 0) {
                afegirMes = false;
            } else if (opcio >= 1 && opcio <= 4) {
                String servei = "";
                switch (opcio) {
                    case 1:
                        servei = SERVEI_ESMORZAR;
                        break;
                    case 2:
                        servei = SERVEI_GIMNAS;
                        break;
                    case 3:
                        servei = SERVEI_SPA;
                        break;
                    case 4:
                        servei = SERVEI_PISCINA;
                        break;
                }

                if (!serveisSeleccionats.contains(servei)) {
                    serveisSeleccionats.add(servei);
                    System.out.println("✅ Servei afegit: " + servei);
                } else {
                    System.out.println("❌ Aquest servei ja està seleccionat.");
                }
            } else {
                System.out.println("❌ Opció no vàlida.");
            }
        }

        return serveisSeleccionats;
    }

    /**
     * Calcula i retorna el cost total de la reserva, incloent l'habitació,
     * els serveis seleccionats i l'IVA.
     */
    public static float calcularPreuTotal(String tipusHabitacio, ArrayList<String> serveisSeleccionats) {
        float subtotal = preusHabitacions.get(tipusHabitacio);

        System.out.println("\n--- Desglós del preu ---");
        System.out.println("Preu habitació (" + tipusHabitacio + "): " + subtotal + "€");

        // Afegir preu dels serveis
        if (!serveisSeleccionats.isEmpty()) {
            System.out.print("Serveis: ");
            for (String servei : serveisSeleccionats) {
                float preuServei = preusServeis.get(servei);
                subtotal += preuServei;
                System.out.print(servei + " (" + preuServei + "€), ");
            }
            System.out.println();
        }

        System.out.println("Subtotal: " + String.format("%.2f", subtotal) + "€");

        // Calcular IVA
        float iva = subtotal * IVA;
        System.out.println("IVA (21%): " + String.format("%.2f", iva) + "€");

        // Calcular total
        float total = subtotal + iva;
        System.out.println("TOTAL: " + String.format("%.2f", total) + "€");

        return total;
    }

    /**
     * Genera i retorna un codi de reserva únic de tres xifres
     * (entre 100 i 999) que no estiga repetit.
     */
    public static int generarCodiReserva() {
        int codi;
        do {
            codi = 100 + random.nextInt(900); // Genera número entre 100 i 999
        } while (reserves.containsKey(codi)); // Assegura que no estigui repetit
        return codi;
    }

    /**
     * Permet alliberar una habitació utilitzant el codi de reserva
     * i actualitza la disponibilitat.
     */
    public static void alliberarHabitacio() {
        System.out.println("\n===== ALLIBERAR HABITACIÓ =====");

        int codi = llegirEnter("Introdueix el codi de reserva: ");

        if (reserves.containsKey(codi)) {
            ArrayList<String> dadesReserva = reserves.get(codi);
            String tipus = dadesReserva.get(0);

            // Actualitzar disponibilitat (sumar 1 habitació)
            int disponiblesActuals = disponibilitatHabitacions.get(tipus);
            disponibilitatHabitacions.put(tipus, disponiblesActuals + 1);

            // Eliminar reserva
            reserves.remove(codi);

            System.out.println("✅ Reserva trobada!");
            System.out.println("✅ Habitació (" + tipus + ") alliberada correctament.");
            System.out.println("✅ Disponibilitat actualitzada.");
        } else {
            System.out.println("❌ No s'ha trobat cap reserva amb aquest codi.");
        }
    }

    /**
     * Mostra la disponibilitat actual de les habitacions (lliures i ocupades).
     */
    public static void consultarDisponibilitat() {
        System.out.println("\n===== DISPONIBILITAT D'HABITACIONS =====");
        System.out.println("Tipus\t\tLliures\tOcupades");
        System.out.println("------------------------------------");

        mostrarDisponibilitatTipus(TIPUS_ESTANDARD);
        mostrarDisponibilitatTipus(TIPUS_SUITE);
        mostrarDisponibilitatTipus(TIPUS_DELUXE);
    }

    /**
     * Funció recursiva. Mostra les dades de totes les reserves
     * associades a un tipus d'habitació.
     */
    public static void llistarReservesPerTipus(int[] codis, String tipus) {
        // Cas base: si no hi ha més codis, acaba la recursivitat
        if (codis.length == 0) {
            return;
        }

        // Comprovar el primer codi del vector
        int primerCodi = codis[0];
        if (reserves.containsKey(primerCodi)) {
            ArrayList<String> dades = reserves.get(primerCodi);
            // Si el tipus coincideix, mostrar les dades
            if (dades.get(0).equals(tipus)) {
                mostrarDadesReserva(primerCodi);
            }
        }

        // Crear nou vector sense el primer element
        int[] nousCodis = new int[codis.length - 1];
        System.arraycopy(codis, 1, nousCodis, 0, nousCodis.length);

        // Crida recursiva amb el nou vector
        llistarReservesPerTipus(nousCodis, tipus);
    }

    /**
     * Permet consultar els detalls d'una reserva introduint el codi.
     */
    public static void obtindreReserva() {
        System.out.println("\n===== CONSULTAR RESERVA =====");

        int codi = llegirEnter("Introdueix el codi de reserva: ");

        if (reserves.containsKey(codi)) {
            mostrarDadesReserva(codi);
        } else {
            System.out.println("❌ No s'ha trobat cap reserva amb aquest codi.");
        }
    }

    /**
     * Mostra totes les reserves existents per a un tipus d'habitació
     * específic.
     */
    public static void obtindreReservaPerTipus() {
        System.out.println("\n===== CONSULTAR RESERVES PER TIPUS =====");

        // Seleccionar tipus d'habitació
        String tipus = seleccionarTipusHabitacio();
        if (tipus == null) {
            return;
        }

        // Obtenir tots els codis de reserva
        Integer[] codisArray = reserves.keySet().toArray(new Integer[0]);
        int[] codis = new int[codisArray.length];
        for (int i = 0; i < codisArray.length; i++) {
            codis[i] = codisArray[i];
        }

        System.out.println("\nReserves del tipus \"" + tipus + "\":");
        llistarReservesPerTipus(codis, tipus);

        // Comprovar si hi havia reserves d'aquest tipus
        if (!reservesContenenTipus(codis, tipus)) {
            System.out.println("No hi ha reserves d'aquest tipus.");
        }
    }

    /**
     * Consulta i mostra en detall la informació d'una reserva.
     */
    public static void mostrarDadesReserva(int codi) {
        if (!reserves.containsKey(codi)) {
            System.out.println("❌ Reserva no trobada.");
            return;
        }

        ArrayList<String> dades = reserves.get(codi);

        System.out.println("\n--- Dades de la reserva " + codi + " ---");
        System.out.println("- Tipus d'habitació: " + dades.get(0));
        System.out.println("- Cost total: " + dades.get(1) + "€");

        System.out.print("- Serveis addicionals: ");
        boolean teServeis = false;
        for (int i = 2; i < dades.size(); i++) {
            if (dades.get(i) != null) {
                if (!teServeis) {
                    System.out.println(); // Salt de línia per al primer servei
                    teServeis = true;
                }
                System.out.println("  * " + dades.get(i));
            }
        }

        if (!teServeis) {
            System.out.println("Cap");
        }
        System.out.println();
    }

    // --------- MÈTODES AUXILIARS (PER MILLORAR LEGIBILITAT) ---------

    /**
     * Llig un enter per teclat mostrant un missatge i gestiona possibles
     * errors d'entrada.
     */
    static int llegirEnter(String missatge) {
        int valor = 0;
        boolean correcte = false;
        while (!correcte) {
            try {
                System.out.print(missatge);
                valor = sc.nextInt();
                correcte = true;
            } catch (Exception e) {
                System.out.println("❌ Error: introdueix un número vàlid.");
                sc.nextLine(); // Netejar buffer
            }
        }
        sc.nextLine(); // Netejar buffer després de llegir l'enter
        return valor;
    }

    /**
     * Mostra per pantalla informació d'un tipus d'habitació: preu i
     * habitacions disponibles.
     */
    static void mostrarInfoTipus(String tipus) {
        int disponibles = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        float preu = preusHabitacions.get(tipus);
        System.out.println("- " + tipus + " (" + disponibles + " disponibles de " + capacitat + ") - " + preu + "€");
    }

    /**
     * Mostra la disponibilitat (lliures i ocupades) d'un tipus d'habitació.
     */
    static void mostrarDisponibilitatTipus(String tipus) {
        int lliures = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        int ocupades = capacitat - lliures;

        String etiqueta = tipus;
        if (etiqueta.length() < 8) {
            etiqueta = etiqueta + "\t"; // Per a alinear la taula
        }

        System.out.println(etiqueta + "\t" + lliures + "\t" + ocupades);
    }

    /**
     * Comprova si hi ha reserves d'un tipus específic (mètode auxiliar).
     */
    static boolean reservesContenenTipus(int[] codis, String tipus) {
        for (int codi : codis) {
            if (reserves.containsKey(codi)) {
                ArrayList<String> dades = reserves.get(codi);
                if (dades.get(0).equals(tipus)) {
                    return true;
                }
            }
        }
        return false;
    }
}