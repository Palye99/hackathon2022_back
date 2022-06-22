package com.example.command.service;

import com.example.command.dto.ResultCommand;
import com.example.command.dto.TerraformDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class

import static com.example.command.bo.Value.*;

@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    public CommandService(){
    }

    public ResultCommand shellCommand(String command) throws Exception {
        try {
            logger.info("terraform init");

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

    public ResultCommand execTerraform(TerraformDTO terraformDTO) throws Exception {
        try {
            File provider = new File("/tmp/provider.tf");
            File deploy = new File("/tmp/deploy.tf");
            File variable = new File ("/tmp/variable.tf");

            FileWriter providerWriter = new FileWriter("/tmp/provider.tf");
            FileWriter deployWriter = new FileWriter("/tmp/deploy.tf");
            FileWriter variableWriter = new FileWriter("/tmp/variable.tf");

            if(!provider.exists()) {
                provider.createNewFile();
                providerWriter.write(providerStr);
                providerWriter.close();
            }

            if(!deploy.exists()) {
                deploy.createNewFile();
                deployWriter.write(String.format(deployStr, terraformDTO.publicKey, terraformDTO.instanceName));
                deployWriter.close();
            }

            if(!variable.exists()) {
                variable.createNewFile();
                variableWriter.write(String.format(variableStr, terraformDTO.ipAddress));
                variableWriter.close();
            }


            // Commandes Terraforms

            logger.info("terraform init");

            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase().startsWith("windows");

            String homeDirectory = System.getProperty("user.home");

            Process process = null;

            if (isWindows) {
                process = Runtime.getRuntime()
                        .exec("cd /tmp/ || terraform init -force-copy && cd /tmp/ || terraform apply -auto-approve");
            } else {
                process = Runtime.getRuntime()
                        .exec("cd /tmp/ || terraform init -force-copy && cd /tmp/ || terraform apply  -auto-approve");
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
                logger.debug(s);
            }

            // Read any errors from the attempted command
            logger.debug("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                resultCommand.setError(resultCommand.getError() + "\n" + s);
                logger.error(s);
            }

            // Suppression fichiers
            if(deploy.exists()) {
                deploy.delete();
                logger.info("/tmp/deploy.tf deleted");

            }
            if(provider.exists()) {
                provider.delete();
                logger.info("/tmp/provider.tf deleted");

            }
            if(variable.exists()) {
                variable.delete();
                logger.info("/tmp/variable.tf deleted");

            }

            return resultCommand;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Erreur lors de l'execution de la commande");
        } finally {
            logger.info("closing connection");
        }
     }


    @Deprecated
    public ResultCommand terraformDeprecated(String command) throws Exception {
        try {
            File provider = new File("/tmp/provider.tf");
            File deploy = new File("/tmp/deploy.tf");
            File variable = new File ("/tmp/variable.tf");

            FileWriter providerWriter = new FileWriter("/tmp/provider.tf");
            FileWriter deployWriter = new FileWriter("/tmp/deploy.tf");
            FileWriter variableWriter = new FileWriter("/tmp/variable.tf");

            if(!provider.exists()) {
                provider.createNewFile();
                providerWriter.write(providerStr);
                providerWriter.close();
            }

            if(!deploy.exists()) {
                deploy.createNewFile();
//                deployWriter.write(deployStr, "key","nomMachine");
                deployWriter.close();
            }

            if(!variable.exists()) {
                variable.createNewFile();
//                variableWriter.write(variableStr, "IP");
                variableWriter.close();
            }


            // Commandes Terraforms

            logger.info("terraform init");

            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase().startsWith("windows");

            String homeDirectory = System.getProperty("user.home");

            Process process = null;

            if (isWindows) {
                process = Runtime.getRuntime()
                        .exec("cd /tmp/ || terraform init -force-copy && cd /tmp/ || terraform apply -auto-approve");
//                        .exec(String.format("cd /tmp/ || terraform init -force-copy"))
//                        .exec(String.format("cd /tmp/ || terraform apply -auto-approve"));
            } else {
                process = Runtime.getRuntime()
                        .exec("cd /tmp/ || terraform init -force-copy && cd /tmp/ || terraform apply  -auto-approve");
//                        .exec(String.format("cd /tmp/ || terraform init -force-copy"))
//                        .exec(String.format("cd /tmp/ || terraform apply  -auto-approve"));
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


            // Suppression fichiers
            if(deploy.exists()) {
                deploy.delete();
                logger.info("/tmp/deploy.tf deleted");

            }
            if(provider.exists()) {
                provider.delete();
                logger.info("/tmp/provider.tf deleted");

            }
            if(variable.exists()) {
                variable.delete();
                logger.info("/tmp/variable.tf deleted");

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
