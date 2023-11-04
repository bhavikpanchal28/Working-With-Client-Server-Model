import java.io.*;
import java.net.*;

public class Client {
    private static class CLI {
        private static BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

        public static void fatal(String message) {
            System.err.println(message);
            System.exit(1);
        }

        static String read(String message) {
            System.out.print(message);

            try {
                String response = inputReader.readLine();
                System.out.println("");
                return response;
            } catch (IOException e) {
                fatal("Failed to read input " + e);
            }
            return null;
        }

        BufferedReader serverIn;
        PrintWriter serverOut;

        public CLI(BufferedReader serverIn, PrintWriter serverOut) {
            this.serverIn = serverIn;
            this.serverOut = serverOut;
        }

        private String request(String command, String[] args) {
            try {
                String body = command + " " + String.join(" ", args);
                serverOut.println(body);
                serverOut.flush();

                System.out.println("Rquest count: " + serverIn.readLine());
                return serverIn.readLine();
            } catch (IOException e) {
                fatal(e.getMessage());
                return null;
            }
        }

        private void run() {
            int choice = Integer.parseInt(read(
                    "\nMenu\n1. Compound Interest Calculator\n2. Fixed Monthly Mortgage Repayment Calculator\n3. BMI Calculator\n4. Weekly Salary Calculator\n5. Gratuity Calculator\n6. Exit\n\nOption: "));

            if (choice == 0) {

            } else if (choice == 1) {
                String principal = read("Principal: ");
                String rate = read("Interest Rate: ");
                String compoundedPerPeriod = read("Number of times interest is compounded per period: ");
                String periods = read("Number of periods: ");

                String result = request("COMPOUND_INTEREST",
                        new String[] { principal, rate, compoundedPerPeriod, periods });
                System.out.println("Interest = " + result);
            } else if (choice == 2) {
                String principal = read("Principal: ");
                String rate = read("Monthly Interest Rate: ");
                String months = read("Number of periods: ");

                String result = request("MORTGAGE", new String[] { principal, rate, months });
                System.out.println("Mortage = " + result + "/month");
            } else if (choice == 3) {
                String weight = read("Weight(lbs): ");
                String height = read("Height(in): ");

                String result = request("BMI", new String[] { weight, height });
                System.out.println("BMI = " + result);
            } else if (choice == 4) {
                String rate = read("Hourly Rate: $");
                String hours = read("Hours Worked: ");

                String result = request("SALARY", new String[] { rate, hours });
                System.out.println("Weekly Salary = $" + result + "/week");
            } else if (choice == 5) {
                String years = read("Years Worked: ");
                String monthlySalary = read("Monthly Salary: $");

                String result = request("GRATUITY", new String[] { years, monthlySalary });
                System.out.println("Gratuity = $" + result);
            } else if (choice == 6) {
                System.err.println("\nBye");
                System.exit(0);
            } else {
                System.out.println("\nInvalid input.\n");
            }

            run();
        }
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("\n" + serverIn.readLine());

            new CLI(serverIn, serverOut).run();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
