package hash341;

import java.io.*;
import java.util.*;

public class CityTable implements Serializable {

    City[] cities;
    SecondaryTable[] secTable;
    ArrayList<City> collidedCities;
    int[] collisions;
    Hash24 h1 = new Hash24();
    int nsize;

    public CityTable (String fname, int tsize) {
        nsize = tsize;
        cities = new City[tsize];
        secTable = new SecondaryTable[tsize];
        collisions = new int[tsize];

        collidedCities = new ArrayList<>();

        h1.dump();
        System.out.println("Table size: " + tsize);

        try {
            File myObh = new File(fname);
            Scanner myReader = new Scanner(myObh);
            while (myReader.hasNextLine()) {
                City city = new City();

                String data = myReader.nextLine();
                city.name = data.replace("'", "");

                data = myReader.nextLine();
                String[] temp = data.split("\\s+");
                city.latitude = Float.parseFloat(temp[0]);
                city.longitude = Float.parseFloat(temp[1]);

                int index = h1.hash(city.name) % tsize;
                if (Objects.equals(cities[index], null)) {
                    cities[index] = city;
                } else {
                    collisions[index]++;
                    city.setCollidedSpot(index);
                    collidedCities.add(city);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not read");
        }
        for (int i = 0; i < tsize; i++) {
            ArrayList<City> CollidedAt = new ArrayList<>();
            if (collisions[i] > 0) {
                for (int j = 0; j < collidedCities.size(); j++) {
                    if (collidedCities.get(j).getCollidedSpot() == i) {
                        CollidedAt.add(collidedCities.get(j));
                    }
                }
//               System.out.println("# of primary slots with ");
                secTable[i] = new SecondaryTable(collisions[i]);
                secTable[i].insertCities(CollidedAt);
            }
        }
    }

    public City find(String cName) {
        int idx = h1.hash(cName) % nsize;
        if (Objects.equals(cities[idx], null)) {
            return null;
        }
        if (cities[idx].name.equals(cName)) {
            return cities[idx];
        } else {
            if (Objects.equals(secTable[idx], null)) {
                return null;
            }
            int secIdx = secTable[idx].getHashSec().hash(cName) % secTable[idx].secSize;
            return secTable[idx].collidedCities[secIdx];
        }
    }

    public void writeToFile (String fName) {
//        FileOutputStream out = new FileOutputStream(fName);
//        ObjectOutputStream oout = new ObjectOutputStream(out);
//
////        oout.writeObject(c);
        try {

            FileOutputStream fileOut = new FileOutputStream(fName);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(new CityTable("US_Cities_LL.txt", 16000));
            objectOut.close();
            System.out.println("The Object  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static CityTable readFromFile (String fName) {
        return new CityTable("test", 1);
    }

    public static void main(String[] args) {
        CityTable test = new CityTable("US_Cities_LL.txt", 16000);
        int max = test.collisions[0];
        int sortedCollisions[] = test.collisions.clone();
        for (int i : test.collisions) {
            if (i > max) {
                max = i;
            }
        }
        System.out.println("Max collisions = " + max);
        ArrayList<City> mostCollidedCities = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            int count = 0;
            for (int j = 0; j < 16000; j++) {
                if (test.collisions[j] == i) {
                    count++;
                    if (i == 4 || i == 5 || i == 6 || i == 7 || i == 8) {
                        mostCollidedCities.add(test.cities[j]);
                    }
                }
            }
            System.out.println("# of primary slots with " + i + " cities = " + count);
        }
        System.out.println("");
        System.out.println("*** Cities in the slot with most collisions ***");
        for (int i = mostCollidedCities.size()-1; i > mostCollidedCities.size()-7; i--) {
            System.out.println(mostCollidedCities.get(i).name + " (" + mostCollidedCities.get(i).latitude + "," + mostCollidedCities.get(i).longitude + ")");
        }

        int[] hashCount = new int[21];
        int greater1HashCount = 0;
        for (int i = 0; i < 16000; i++) {
            if (test.collisions[i] > 0) {
                if (test.secTable[i].secSize > 1) {
                    greater1HashCount++;
                }
                for (int j = 0; j < 20; j++) {
                    if (test.secTable[i].hashCount == j) {
                        hashCount[j]++;
                    }

                }
            }
        }
        System.out.println("");
        double totalHashes = 0;
        double secTableCount = 0;
        for (int i = 1; i < 21; i++) {
            totalHashes += (i * hashCount[i]);
            secTableCount += (hashCount[i]);
            System.out.println("# of secondary hash tables trying " + i + " hash functions = " + hashCount[i]);
        }

        System.out.println("");
        System.out.println("Number of secondary hash tables with more than 1 item = " + greater1HashCount);
        System.out.println("Average # of hash functions tried = " + totalHashes/secTableCount);


    }
}
