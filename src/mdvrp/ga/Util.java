package mdvrp.ga;

import mdvrp.Customer;

public class Util {

    public static float euclid(Customer a, Customer b) {
        float x = a.getX() - b.getX();
        float y = a.getY() - b.getY();
        x *= x;
        y *= y;
        return x + y;
    }

}
