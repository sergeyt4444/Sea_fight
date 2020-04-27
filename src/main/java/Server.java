import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server extends JPanel {


    public static JFrame JFrame;
    public static final int SCALE=40;
    public static int WIDTH=10;
    public static  int HEIGHT=10;
    public static Field my_field;
    public static Field opp_field;
    public static JLabel player;
    public static JLabel status;
    public static JButton button;
    public static Ship_Array ships;
    public static Ship_Array enemy_ships;
    public static  int flag=0;
    public static int selected_ship = -1;
    public static int hits = 0;
    public static int my_hits = 0;
    public static DataInputStream input;
    public static DataOutputStream output;

    public static void main(String[] args) {
        JFrame = new JFrame("Server");
        my_field = new Field();
        opp_field = new Field();
        ships = new Ship_Array();

        Font font = new Font("Tahoma", Font.BOLD, 20);
        //my_field.Default_Init();
        JFrame.setSize(1600, 800);
        JFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JFrame.setResizable(false);
        player = new JLabel("Player 1");
        player.setBounds(500, 75, 300, 50);
        status = new JLabel("Preparations");
        status.setBounds(500, 25, 300, 50);
        button = new JButton("Finish preparations");
        button.setBounds(1250, 675, 300, 50);
        player.setFont(font);
        status.setFont(font);
        button.setFont(font);
        JFrame.add(player);
        JFrame.add(status);
        JFrame.add(button);
        JFrame.add(new Server());
        JFrame.setVisible(true);
        opp_field.matrix[0][0] = 1;
        button.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int complete = 1;
                        for (Ship ship: ships.ship_arr) {
                            if(ship.placed == 0)
                                complete = 0;
                        }
                        if (complete == 1) {
                            status.setText("Your turn");
                            flag = 1;
                        }
                    }
                }
        );


        JFrame.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int click_X = e.getX();
                int click_Y = e.getY() - 32;
                if (flag == 0 ) {
                    if (click_X > 1200) {
                        int clicked_ship = -1;
                        for (int i = 0; i < 10; i++) {
                            if ((click_X >= 1300) && (click_X <= 1300 + SCALE * ships.ship_arr.get(i).len)
                                    && (click_Y >= (20 + (SCALE + 20) * i)) && (click_Y <= (20 + SCALE + i * (SCALE + 20)))) {
                                System.out.print(i);
                                clicked_ship = i;
                                break;
                            }
                        }
                        if (clicked_ship > -1) {
                            if (ships.ship_arr.get(clicked_ship).placed == 0) {
                                selected_ship = clicked_ship;
                            } else {
                                selected_ship = clicked_ship;
                                int tmp_x = ships.ship_arr.get(clicked_ship).x;
                                int tmp_y = ships.ship_arr.get(clicked_ship).y;
                                int tmp = ships.ship_arr.get(clicked_ship).is_horiz;
                                for (int j = 0; j < ships.ship_arr.get(clicked_ship).len; j++) {
                                    my_field.matrix[tmp_y + j * (1 - tmp)][tmp_x + j * tmp] = 0;
                                }
                                ships.ship_arr.get(clicked_ship).is_horiz = 1;
                                ships.ship_arr.get(clicked_ship).placed = 0;
                            }
                        }
                    }
                    else if (click_X >= 100 && click_Y >= 150 && click_X <= 100 + SCALE*WIDTH && click_Y <= 150 + SCALE*HEIGHT) {

                        System.out.print("right");
                        if (selected_ship >= 0) {

                            System.out.print(selected_ship);
                            int tmp = ships.ship_arr.get(selected_ship).is_horiz;
                            int tmp_len = ships.ship_arr.get(selected_ship).len;
                            int col = (click_X - 100)/SCALE;
                            int row = (click_Y - 150)/SCALE;
                            System.out.print(col);
                            System.out.print(row);
                            int possible = 1;
                            if (tmp == 1) {
                                if (col + tmp_len - 1 > 9) {
                                    if (row + tmp_len - 1 <= 9) {
                                        tmp = 0;
                                        ships.ship_arr.get(selected_ship).is_horiz = 0;
                                    }
                                    else {
                                        possible = 0;
                                    }
                                }
                            }
                            else {
                                if (row + tmp_len - 1 <= 9) {
                                    tmp = 1;
                                    ships.ship_arr.get(selected_ship).is_horiz = 1;
                                }
                                else {
                                    possible = 0;
                                }
                            }
                            for (int i = -1; i <= tmp_len; i++) {
                                for (int j = -1; j <2; j++) {
                                    int col_temp = col + i*tmp + j*(1-tmp);
                                    int row_temp = row + j*tmp + i*(1-tmp);
                                    if (col_temp > -1 && row_temp > -1 && col_temp < 10 && row_temp < 10) {
                                        if (my_field.matrix[row_temp][col_temp] != 0) {
                                            possible = 0;
                                            System.out.print(col_temp);
                                            System.out.print(row_temp);
                                        }
                                    }
                                }
                            }
                            if (possible == 1) {
                                ships.ship_arr.get(selected_ship).placed = 1;
                                ships.ship_arr.get(selected_ship).x = col;
                                ships.ship_arr.get(selected_ship).y = row;
                                for (int i = 0; i < tmp_len; i++) {
                                    my_field.matrix[row + i*(1-tmp)][col + i*tmp] = 1;
                                }
                                selected_ship = -1;
                            }
                        }
                        else {
                            int rotating_ship = -1;
                            int col = (click_X - 100)/SCALE;
                            int row = (click_Y - 150)/SCALE;
                            for (int i = 0; i < 10; i++) {
                                if (ships.ship_arr.get(i).placed == 1) {
                                    if (ships.ship_arr.get(i).is_horiz == 1) {
                                        if (col >= ships.ship_arr.get(i).x && row == ships.ship_arr.get(i).y
                                                && col < ships.ship_arr.get(i).x + ships.ship_arr.get(i).len) {
                                            rotating_ship = i;
                                            break;
                                        }
                                    }
                                    else {
                                        if (col == ships.ship_arr.get(i).x && row >= ships.ship_arr.get(i).y
                                                && row < ships.ship_arr.get(i).y + ships.ship_arr.get(i).len) {
                                            rotating_ship = i;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (rotating_ship > -1) {
                                int possible = 1;
//                            ships.ship_arr.get(rotating_ship).x = col;
//                            ships.ship_arr.get(rotating_ship).y = row;
                                col = ships.ship_arr.get(rotating_ship).x;
                                row = ships.ship_arr.get(rotating_ship).y;
                                int tmp = ships.ship_arr.get(rotating_ship).is_horiz;
                                int tmp_len = ships.ship_arr.get(rotating_ship).len;
                                if (tmp == 0)
                                    tmp = 1;
                                else tmp = 0;
                                for (int i = 0; i < tmp_len; i++) {
                                    my_field.matrix[row + i * tmp][col + i * (1 - tmp)] = 0;
                                }
                                for (int i = -1; i <= tmp_len; i++) {
                                    for (int j = -1; j < 2; j++) {
                                        int col_temp = col + i * tmp + j * (1 - tmp);
                                        int row_temp = row + j * tmp + i * (1 - tmp);
                                        if (col_temp > -1 && row_temp > -1 && col_temp < 10 && row_temp < 10) {
                                            if (my_field.matrix[row_temp][col_temp] != 0) {
                                                possible = 0;
                                                System.out.print(col_temp);
                                                System.out.print(row_temp);
                                            }
                                        }
                                    }
                                }
                                if (possible == 1) {
                                    ships.ship_arr.get(rotating_ship).is_horiz = tmp;
                                    for (int i = 0; i < tmp_len; i++) {
                                        my_field.matrix[row + i * (1 - tmp)][col + i * tmp] = 1;
                                    }
                                } else {
                                    for (int i = 0; i < tmp_len; i++) {
                                        my_field.matrix[row + i * tmp][col + i * (1 - tmp)] = 1;
                                    }
                                }
                            }
                        }
                    }
                }
                else if (flag == 1) {
                    if (click_X >= 200 + WIDTH*SCALE && click_Y >= 150
                            && click_X <= 200 + 2*SCALE * WIDTH && click_Y <= 150 + SCALE * HEIGHT) {
                        int col = (click_X - 600) / SCALE;
                        int row = (click_Y - 150) / SCALE;
                        if (opp_field.matrix[row][col] == 0 || opp_field.matrix[row][col] == 1) {
                            if (opp_field.matrix[row][col] == 1) {
                                opp_field.matrix[row][col] = 3;
                                //send

                                int hit_ship = -1;
                                for (int i = 0; i < 10; i++) {
                                    if (enemy_ships.ship_arr.get(i).placed == 1) {
                                        if (enemy_ships.ship_arr.get(i).is_horiz == 1) {
                                            if (col >= enemy_ships.ship_arr.get(i).x && row == enemy_ships.ship_arr.get(i).y
                                                    && col < enemy_ships.ship_arr.get(i).x + enemy_ships.ship_arr.get(i).len) {
                                                hit_ship = i;
                                                break;
                                            }
                                        }
                                        else {
                                            if (col == enemy_ships.ship_arr.get(i).x && row >= enemy_ships.ship_arr.get(i).y
                                                    && row < enemy_ships.ship_arr.get(i).y + enemy_ships.ship_arr.get(i).len) {
                                                hit_ship = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                                int complete = 1;
                                int hit_col = enemy_ships.ship_arr.get(hit_ship).x;
                                int hit_row = enemy_ships.ship_arr.get(hit_ship).y;
                                int tmp = enemy_ships.ship_arr.get(hit_ship).is_horiz;
                                int tmp_len = enemy_ships.ship_arr.get(hit_ship).len;
                                for (int i = 0; i < tmp_len; i++)
                                    if (opp_field.matrix[row + i * (1 - tmp)][col + i * tmp] != 3) {
                                        complete = 0;
                                        break;
                                    }
                                if (complete == 1) {
                                    for (int i = -1; i <= tmp_len; i++) {
                                        for (int j = -1; j < 2; j++) {
                                            int col_temp = col + i * tmp + j * (1 - tmp);
                                            int row_temp = row + j * tmp + i * (1 - tmp);
                                            if (col_temp > -1 && row_temp > -1 && col_temp < 10 && row_temp < 10) {
                                                if (opp_field.matrix[row_temp][col_temp] == 0) {
                                                    opp_field.matrix[row_temp][col_temp] = 2;
                                                }
                                            }
                                        }
                                    }
                                }

                                hits++;
                                if (hits == 20) {
                                    status.setText("Victory!");
                                    flag = 3;
                                }
                            } else {
                                opp_field.matrix[row][col] = 2;
                                flag = 2;
                                //send
                                status.setText("Opponents turn");
                            }
                        }
                    }
                }

                JFrame.repaint();
            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });


        Gson json = new GsonBuilder().setPrettyPrinting().create();
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(4443);
            socket = serverSocket.accept();
            System.out.println("accepted");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream sin = null;
        OutputStream sout = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            sin = socket.getInputStream();
            sout = socket.getOutputStream();
            in = new DataInputStream(sin);
            out = new DataOutputStream(sout);
            if (out != null && in != null) {
                input = in;
                output = out;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(100,150,WIDTH*SCALE,HEIGHT*SCALE);
        g.fillRect(200 + WIDTH*SCALE,150,WIDTH*SCALE,HEIGHT*SCALE);
        g.setColor(Color.black);
        g.drawLine(1200, 0, 1200, 800);
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                switch (opp_field.matrix[j][i]){
                    case 0: {
                        g.setColor(Color.white);
                        g.fillRect(200 + WIDTH*SCALE + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                    case 1: {
                        g.setColor(Color.white);
                        g.fillRect(200 + WIDTH*SCALE + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                    case 2: {
                        g.setColor(Color.gray);
                        g.fillRect(200 + WIDTH*SCALE + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                    case 3: {
                        g.setColor(Color.red);
                        g.fillRect(200 + WIDTH*SCALE + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                }

            }

        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                switch (my_field.matrix[j][i]){
                    case 0: {
                        g.setColor(Color.white);
                        g.fillRect(100 + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                    case 1: {
                        g.setColor(Color.cyan);
                        g.fillRect(100 + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                    case 2: {
                        g.setColor(Color.gray);
                        g.fillRect(100 + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                    case 3: {
                        g.setColor(Color.red);
                        g.fillRect(100 + i*SCALE, 150 + j*SCALE, SCALE, SCALE);
                        break;
                    }
                }

            }
        g.setColor(Color.black);
        for(int x=0;x<=WIDTH*SCALE;x+=SCALE)
        {
            g.drawLine(x + 100,150,x + 100,HEIGHT*SCALE + 150);
        }

        for(int y=0;y<=HEIGHT*SCALE;y+=SCALE)
        {
            g.drawLine(100,y+150,WIDTH*SCALE + 100,y+150);
        }
        for(int x=0;x<=WIDTH*SCALE;x+=SCALE)
        {
            g.drawLine(x + 200 +WIDTH*SCALE,150,x + 200 + WIDTH*SCALE,HEIGHT*SCALE + 150);
        }

        for(int y=0;y<=HEIGHT*SCALE;y+=SCALE)
        {
            g.drawLine(200 + WIDTH*SCALE,y+150,WIDTH*SCALE + 200 + WIDTH*SCALE,y+150);
        }

        for (Ship ship: ships.ship_arr) {
            if (ship.placed == 0) {
                g.setColor(Color.cyan);
            }
            else {
                g.setColor(Color.gray);
            }
            g.fillRect(1300, 20 + (20 +SCALE)*ship.num,SCALE*ship.len, SCALE);
            g.setColor(Color.black);
            g.drawLine(1300, 20 + (20 +SCALE)*ship.num,1300 + SCALE*ship.len, 20 + (20 +SCALE)*ship.num );
            g.drawLine(1300, 20 + SCALE + (20 +SCALE)*ship.num,1300 + SCALE*ship.len, 20 + SCALE + (20 +SCALE)*ship.num);
            for (int x = 1300; x <= 1300 + SCALE*ship.len; x+=SCALE) {
                g.drawLine(x, 20 + (20 +SCALE)*ship.num,x, 20 + (20 +SCALE)*ship.num +SCALE );
            }
        }

    }
}
