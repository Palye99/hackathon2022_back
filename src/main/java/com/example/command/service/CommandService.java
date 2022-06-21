package com.example.command.service;

import com.example.command.dto.ResultCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    public CommandService(){
    }

    public ResultCommand shellCommand(String command) throws Exception {
        try {
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
            System.out.println("Here is the standard output of the command:\n");

            String s = null;

            while ((s = stdInput.readLine()) != null) {
                resultCommand.setResult(resultCommand.getResult() + s);
                System.out.println(s);
            }

            // Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                resultCommand.setError(resultCommand.getError() + "\n" + s);
                System.out.println(s);
            }
            return resultCommand;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new Exception("Erreur lors de l'execution de la commande");
    }

}
