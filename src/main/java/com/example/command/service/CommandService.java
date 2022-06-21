package com.example.command.service;

import com.example.command.dto.ResultCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class


@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    public CommandService(){
    }

    public ResultCommand shellCommand(String command) throws Exception {
        try {
            File myFile = new File("/tmp/terraform.tf");
            Filewriter myWriter = new FileWriter("/tmp/terraform.tf");
            
            // Vérif
            if(myFile.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                myWriter.write("Le Terraform ici %s","ip ici");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");

            } else {
                System.out.println("File already exists.");
                myWriter.write("Le Terraform ici %s","ip ici");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");

            }

            // Execution commande terraform


        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new Exception("Erreur lors de l'execution de la commande");
    }

}
