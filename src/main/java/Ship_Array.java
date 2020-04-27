import java.util.ArrayList;

public class Ship_Array {
    public ArrayList<Ship> ship_arr;

    public Ship_Array() {
        ship_arr = new ArrayList<Ship>(10);
        for (int i = 0; i < 10; i++) {
            ship_arr.add(new Ship());
            ship_arr.get(i).num = i;
        }
        ship_arr.get(0).len = 4;
        ship_arr.get(1).len = 3;
        ship_arr.get(2).len = 3;
        for (int i = 3; i < 6; i++)
            ship_arr.get(i).len = 2;
        for(int i = 6; i < 10; i++)
            ship_arr.get(i).len = 1;

    }
}
