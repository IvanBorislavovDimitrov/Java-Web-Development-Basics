package javache.handlers;

import javache.io.Reader;
import javache.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private Socket clientSocket;

    private RequestHandler requestHandler;

    private InputStream clientSocketInputStream;

    private OutputStream clientSocketOutputStream;

    public ConnectionHandler(Socket socket, RequestHandler requestHandler) {
        this.initializeConnection(socket);
        this.requestHandler = requestHandler;
    }

    private void initializeConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.clientSocketInputStream = this.clientSocket.getInputStream();
            this.clientSocketOutputStream = this.clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String requestContent = Reader.readAllLines(this.clientSocketInputStream);
            byte[] responseContent = this.requestHandler.handleRequest(requestContent);

            Writer.writeBytes(responseContent, this.clientSocketOutputStream);

            this.clientSocketOutputStream.close();
            this.clientSocketInputStream.close();
            this.clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
