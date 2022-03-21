package views;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class test {

    public static void loadAbilities(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while ((br.readLine()) != null) {
            String r = br.readLine();
            ArrayList<String> s = new ArrayList<>();
            String[] a = r.split(",");
            System.out.println(Arrays.deepToString(a));
        }
    }

    public static void main(String[] args) throws Exception {
        loadAbilities("D:\\Marvel-Ultimate-War\\Abilities.csv");
    }
}
