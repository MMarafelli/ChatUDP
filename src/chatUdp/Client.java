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

    private final DatagramSocket ds;

    public Client(DatagramSocket socket) {
        this.ds = socket;
    }

    public static void main(String[] args) {

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date hora = Calendar.getInstance().getTime();
            String dataFormatada = sdf.format(hora);

            DatagramSocket ds = new DatagramSocket();
            InetAddress destino = InetAddress.getByName("localhost");
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String envio, textoMensagem;

            System.out.print("Digite seu nome para entrar no chat: ");
            String nome = teclado.readLine();
            System.out.println("Bem vindo(a) " + nome);

            envio = nome + " entrou!";

            Thread t = new Client(ds);
            t.start();

            while (!envio.equalsIgnoreCase("sair da sala") || !envio.equalsIgnoreCase("")) {
                byte[] buffer = envio.getBytes();
                DatagramPacket msg = new DatagramPacket(buffer, buffer.length, destino, 1314);
                ds.send(msg);
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
            ds.send(msg);
            ds.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        try {
            while (true) {
                DatagramPacket resposta = new DatagramPacket(new byte[1024], 1024);
                ds.receive(resposta);

                System.out.println(new String(resposta.getData()));
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
