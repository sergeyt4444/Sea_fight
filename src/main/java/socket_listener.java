
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;

public class socket_listener extends Thread{

    public Socket socket;
    public DataInputStream is;
    public DataOutputStream os;
    int is_serv;

    public socket_listener() {
        socket = null;
        is = null;
        os = null;
        is_serv = 0;
    }

    public socket_listener(Socket s, DataInputStream dis, DataOutputStream dos, int bool) throws IOException {
        socket = s;
        is = dis;
        os = dos;
        is_serv = bool;
    }

    public void run() {
        Gson json = new GsonBuilder().setPrettyPrinting().create();
        Type tships = new TypeToken<Ship_Array>(){}.getType();
        Type tfield = new TypeToken<Field>(){}.getType();
        String sfield = null;
        try {
            sfield = is.readUTF();
            String sships = is.readUTF();
            if (is_serv == 1) {
                Server.opp_field = json.fromJson(sfield, tfield);
                Server.enemy_ships = json.fromJson(sships, tships);
                System.out.println("Accepted data");
            } else {
                Client.opp_field = json.fromJson(sfield, tfield);
                Client.enemy_ships = json.fromJson(sships, tships);
                System.out.println("Accepted data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is_serv == 1) {
            while(Server.flag == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }
            System.out.println("Accepted data");
            Server.flag = 1;
            Server.status.setText("Your turn");
        }
        else {
            while (Client.flag == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            System.out.println("Accepted data");
            Client.flag = 2;
            Client.status.setText("Opponents turn");
        }

    }
}
