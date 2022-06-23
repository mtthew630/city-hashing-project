package hash341;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class SecondaryTable implements Serializable {
    public Hash24 getHashSec() {
        return hashSec;
    }

    Hash24 hashSec;
    int hashCount;

    public void setCollidedCities(City[] collidedCities) {
        this.collidedCities = collidedCities;
    }

    City[] collidedCities;
    int secSize;

    public SecondaryTable(int tsize) {
        this.secSize = tsize*tsize;
//        System.out.println(secSize);
        collidedCities = new City[secSize];
        hashSec = new Hash24();
        hashCount = 1;
    }

    public void insertCities(ArrayList<City> collidedAt) {
        City[] tempCities = new City[secSize];
        for (int i = 0; i < collidedAt.size(); i++) {
            int index = this.hashSec.hash(collidedAt.get(i).name) % this.secSize;
            if (Objects.equals(tempCities[index], null)) {
                tempCities[index] = collidedAt.get(i);
            } else {
                hashSec = new Hash24();
                this.hashCount++;
                tempCities = new City[secSize];
                i = 0;
//                System.err.println("Unexpected collision in secondary table");
            }
        }
        setCollidedCities(tempCities);

    }
}
