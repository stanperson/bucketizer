package com.stan.person.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PortfolioPlanReader {

    public static PortfolioPlan readPortfolioPlan(File file) {

        PortfolioPlan plan = new PortfolioPlan();

        try{
            Scanner inputStream = new Scanner(file);

            while(inputStream.hasNextLine()){
                String line= inputStream.nextLine();
                if (line.isEmpty()) break;
                if (line.startsWith("End")) break;
                plan.add(line);
            }
            inputStream.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return plan;

    }
}
