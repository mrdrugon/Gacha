import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class MultiplayerHandler implements Main.NetworkManager {
    private BufferedReader in;
    private PrintWriter out;
    private Thread listenThread;
    private Consumer<String> messageHandler;

    public MultiplayerHandler(Socket socket) throws IOException{
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        startListening();
    }

    private void startListening(){
        listenThread = new Thread(()->{
            try {
                String line;
                while ((line = in.readLine()) != null){
                    if(messageHandler != null){
                        messageHandler.accept(line);
                    }
                }
            } catch (IOException e){
                System.err.println("Conection lost.");
            }
        });
        listenThread.start();
    }

    @Override
    public void send(String message){
        out.println(message);
    }

    @Override
    public void onReceive(Consumer<String> handler){
        this.messageHandler = handler;
    }

    @Override
    public void close(){
        try{
            in.close();
            out.close();
            listenThread.interrupt();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
