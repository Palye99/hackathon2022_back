package com.example.command.service;

import com.example.command.dto.ResultCommand;
import com.example.command.dto.TerraformDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;

import static com.example.command.bo.Value.*;

@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    public CommandService(){
    }

    public ResultCommand shellCommand(String command) throws Exception {
        try {
            logger.info("Commande init");

            boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");

            String homeDirectory = System.getProperty("user.home");

            Process process = null;

            if (isWindows) {
                process = Runtime.getRuntime()
                        .exec(String.format("cmd.exe /c %s", command));
            } else {
                command = "ls";
                process = Runtime.getRuntime()
                        .exec(String.format("/bin/bash -c %s", command));
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

    public ResultCommand execTerraform(TerraformDTO terraformDTO) throws Exception {
        try {
            File provider = new File("provider.tf");
            File deploy = new File("deploy.tf");
            File variable = new File ("variable.tf");

            logger.info("provider exist ? " + provider.exists());
            logger.info("deploy exist ? " + deploy.exists());
            logger.info("variable exist ? " + variable.exists());

            if(!provider.exists()) {
                provider.createNewFile();
                FileWriter providerWriter = new FileWriter("provider.tf");
                providerWriter.write(providerStr);
                providerWriter.close();
            }

            if(!deploy.exists()) {
                deploy.createNewFile();
                FileWriter deployWriter = new FileWriter("deploy.tf");
                deployWriter.write(String.format(deployStr, terraformDTO.publicKey, terraformDTO.instanceName));
                deployWriter.close();
            }

            if(!variable.exists()) {
                FileWriter variableWriter = new FileWriter("variable.tf");
                variable.createNewFile();
                variableWriter.write(String.format(variableStr, terraformDTO.ipAddress));
                variableWriter.close();
            }

            logger.info("provider exist ? " + provider.exists());
            logger.info("deploy exist ? " + deploy.exists());
            logger.info("variable exist ? " + variable.exists());

            // Commandes Terraforms

            logger.info("terraform init");

            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase().startsWith("windows");

            logger.info("Is windows ? " + isWindows);

            String homeDirectory = System.getProperty("user.home");

            logger.info("path homeDirectory = " + homeDirectory);

            Process process = null;

            if (isWindows) {
                logger.info("Start processing windows");
                process = Runtime.getRuntime()
                        .exec("cmd.exe /c cd /tmp/ || terraform init -force-copy && cd /tmp/ || terraform apply -auto-approve");
                logger.info("cd /tmp/ || terraform init -force-copy && cd /tmp/ || terraform apply -auto-approve");
                logger.info("End processing windows");
            } else {
                logger.info("Start processing shell");
                String[] commands = { "/bin/bash", "-c", "terraform init -force-copy | terraform apply -auto-approve" };

                process = Runtime.getRuntime()
                        .exec(commands);
                logger.info("terraform init -force-copy | terraform apply -auto-approve");
                logger.info("End processing shell");
            }

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            ResultCommand resultCommand = new ResultCommand();

            // Read the output from the command
            logger.info("Here is the standard output of the command:\n");

            String s = null;

            while ((s = stdInput.readLine()) != null) {
                resultCommand.setResult(resultCommand.getResult() + s);
                logger.info(s);
            }

            // Read any errors from the attempted command
            logger.info("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                resultCommand.setError(resultCommand.getError() + "\n" + s);
                logger.error(s);
            }

            // Suppression fichiers
            if(deploy.exists()) {
                deploy.delete();
                logger.info("deploy.tf deleted");

            }
            if(provider.exists()) {
                provider.delete();
                logger.info("provider.tf deleted");

            }
            if(variable.exists()) {
                variable.delete();
                logger.info("variable.tf deleted");

            }

            return resultCommand;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Erreur lors de l'execution de la commande");
        } finally {
            logger.info("closing connection");
        }
     }
}
