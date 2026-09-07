package com.remotescreen.app;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static Socket socket;

    public static Socket getSocket() {
        if (socket == null) {
            try {
                socket = IO.socket(
                    "https://remote-screen-csqe.onrender.com"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return socket;
    }

    public static void connect() {
        Socket s = getSocket();

        if (s != null && !s.connected()) {
            s.connect();
        }
    }

    public static void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }
}
