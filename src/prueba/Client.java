package prueba;

import java.util.Scanner;

/**
 *
 * @author roger
 */
public class Client {

    public static void main(String[] args) {
        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;

        do {
            System.out.println("1. Crear una partida nova "
                    + "\n2. Conectar-se a una partida "
                    + "\n3. Sortir");
            System.out.print("> ");

            if (teclat.hasNextInt()) {
                int opcio = teclat.nextInt();
                switch (opcio) {
                    case 1:
                        System.out.println("Creando partida...");
                        break;
                    case 2:
                        System.out.println("Conectando...");
                        break;
                    case 3:
                        System.out.println("Cerrando...");
                        sortir = true;
                        break;
                    default:
                        System.out.println("\nInserta un número válido...\n");
                }
            } else {
                System.out.println("\nEscribe un numero...\n");
                teclat.next();
            }
        } while (!sortir);

    }

}
