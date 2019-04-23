package chatUdp;

/**
 * **********************
 * Matheus Marafeli * IFTM * Sistemas Distribuídos * * Client *
 * ***********************
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread {

    private final DatagramSocket s;

    public Client(DatagramSocket socket) {
        this.s = socket;
    }

    public static void main(String[] args) {

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date hora = Calendar.getInstance().getTime();
            String dataFormatada = sdf.format(hora);

            DatagramSocket s = new DatagramSocket();
            InetAddress destino = InetAddress.getByName("localhost");
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String envio, textoMensagem;

            System.out.print("Digite seu nome para entrar: ");
            String nome = teclado.readLine();
            System.out.println("Bem vindo(a) " + nome);

            envio = nome + " entrou!";

            Thread t = new Client(s);
            t.start();

            while (!envio.equalsIgnoreCase("")) {
                byte[] buffer = envio.getBytes();
                DatagramPacket msg = new DatagramPacket(buffer, buffer.length, destino, 1314);
                s.send(msg);
                textoMensagem = teclado.readLine();

                if (!textoMensagem.equals("")) {
                    envio = nome + " [" + dataFormatada + "] disse: " + textoMensagem;
                } else {
                    envio = textoMensagem;
                }
            }

            envio = nome + " saiu do chat!";
            byte[] buffer = envio.getBytes();
            DatagramPacket msg = new DatagramPacket(buffer, buffer.length, destino, 1314);
            s.send(msg);
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {

        try {
            while (true) {
                DatagramPacket resposta = new DatagramPacket(new byte[1024], 1024);
                s.receive(resposta);

                System.out.println(new String(resposta.getData()));
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
