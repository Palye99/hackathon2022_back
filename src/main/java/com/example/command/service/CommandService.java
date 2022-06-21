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
            FileWriter myWriter = new FileWriter("/tmp/terraform.tf");

            // Vérif
            if(myFile.createNewFile()) {
                // todo dto
                logger.debug("File created: " + "myObj.getName()");
                // fix me
//                myWriter.write("Le Terraform ici %s","ip ici");
                myWriter.close();
                logger.debug("Successfully wrote to the file.");

            } else {
                logger.debug("File already exists.");
//                myWriter.write("Le Terraform ici %s","ip ici");
                myWriter.close();
                logger.debug("Successfully wrote to the file.");

            }

            // Execution commande terraform

            logger.info("command from shell");

            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase().startsWith("windows");

            String homeDirectory = System.getProperty("user.home");
            Process process = null;
            if (isWindows) {
                process = Runtime.getRuntime()
                        .exec(String.format("cmd.exe /c %s", command));
            } else {
                process = Runtime.getRuntime()
                        .exec(String.format("sh -c %s", command));
            }

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            ResultCommand resultCommand = new ResultCommand();

            // Read the output from the command
            logger.debug("Here is the standard output of the command:\n");

            String s = null;

            while ((s = stdInput.readLine()) != null) {
                resultCommand.setResult(resultCommand.getResult() + s);
                System.out.println(s);
            }

            // Read any errors from the attempted command
            logger.error("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                resultCommand.setError(resultCommand.getError() + "\n" + s);
                System.out.println(s);
            }
            return resultCommand;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.info("closing connection");
        }

        throw new Exception("Erreur lors de l'execution de la commande");
    }

}
