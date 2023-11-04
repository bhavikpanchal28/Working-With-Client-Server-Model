import java.io.*;
import java.net.*;
import java.util.Arrays;

class Server {
    private static class Handler {
        private static double compoundInterest(double principal, double rate, int compoundedPerPeriod, int periods) {
            return principal * Math.pow(1 + (rate / compoundedPerPeriod), compoundedPerPeriod * periods);
        }

        private static double mortage(double principal, double rate, int months) {
            double x = Math.pow(1 + rate, months);
            return principal * rate * (x / (x - 1));
        }

        private static String bmi(double weight, double height) {
            double bmi = (weight * 703) / Math.pow(height, 2);

            if (bmi < 18.5) {
                return bmi + " - Underweight";
            } else if (bmi >= 18.5 && bmi < 25) {
                return bmi + " - Normal";
            } else if (bmi >= 25 && bmi < 30) {
                return bmi + " - Overweight";
            } else {
                return bmi + " - Obese";
            }
        }

        private static double salary(Double hourlyRate, Double hoursWorked) {
            return hourlyRate * hoursWorked * 0.87;
        }

        private static double gratuity(Double years, Double salary) {
            return (years * salary * 15) / 26;

        }

        public static String exec(String msg) {
            String[] tokens = msg.split(" ");
            String command = tokens[0];
            String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (command.equals("COMPOUND_INTEREST")) {
                return Double.toString(compoundInterest(Double.parseDouble(args[0]), Double.parseDouble(args[1]),
                        Integer.parseInt(args[2]), Integer.parseInt(args[3])));
            } else if (command.equals("MORTGAGE")) {
                return Double.toString(
                        mortage(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Integer.parseInt(args[2])));
            } else if (command.equals("BMI")) {
                return bmi(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
            } else if (command.equals("SALARY")) {
                return Double.toString(salary(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
            } else if (command.equals("GRATUITY")) {
                return Double.toString(gratuity(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
            } else {
                return "INVALID REQUEST";
            }
        }
    }

    private static class Worker implements Runnable {
        private final Socket clientSocket;
        private int requestCount = 0;

        public Worker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                out.println("Connection established.");

                String line;
                while ((line = in.readLine()) != null) {
                    out.println(++this.requestCount);
                    out.println(Handler.exec(line));
                }
            } catch (IOException e) {
                System.out.println("Connection Lost.");
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(1234);
            serverSocket.setReuseAddress(true);

            System.out.println("Server running on :1234");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("NEW CONNECTION: " + clientSocket.getInetAddress().getHostAddress());

                Worker worker = new Worker(clientSocket);
                new Thread(worker).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}