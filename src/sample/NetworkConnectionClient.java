
package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

    public abstract class NetworkConnectionClient {

        private ConnThread connthread = new ConnThread();
        private Consumer<Serializable> callback;

        public NetworkConnectionClient(Consumer<Serializable> callback) {
            this.callback = callback;
            connthread.setDaemon(true);

        }

        public void startConn() throws Exception{
            connthread.start();
        }

        public void send(Serializable data) throws Exception{
            connthread.out.writeObject(data);
        }

        public void closeConn() throws Exception{
            connthread.socket.close();
        }

        abstract boolean isServer();
        abstract protected String getIP();
        abstract protected int getPort();
        abstract protected String getID();

        class ConnThread extends Thread {
            private Socket socket;
            private ObjectOutputStream out;

            ConnThread(){}


            public void run() {
                try {
                    System.out.println("In connthread run client");
                    Socket mysocket = new Socket(getIP(), getPort());
                    this.socket = mysocket;
                    String ID = "ID: " + getID();
                    System.out.println("Client established connection");

                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream());
                    this.out = out;

                    send(ID);


                    this.socket.setTcpNoDelay(true);

                    while(true) {
                        Serializable data = (Serializable) in.readObject();

                        String tmp = data.toString();
                        tmp = tmp.intern();
                        String[] split = tmp.split(" ");
                        if (split[0].equals("Roundwinner")){
                            System.out.println("Client recieved roundwinner: " + split[1]);
                            send(data);
                            System.out.println("Sending roundwinner form client");
                        }
                        if (split[0].equals("Game")){
                            if (split[3].equals(3)){
                                data = "Game over! It was a tie.";
                            }
                        }
                        if (split.length > 3){
                            if (split[2].equals("disconnected.")){
                                String disconnect = "Disconnected socket: " + socket;
                                send(disconnect);
                            }
                        }
                        callback.accept(data);
                    }

                }
                catch(Exception e) {
                    callback.accept("connection Closed");
                }
            }
        }

    }



