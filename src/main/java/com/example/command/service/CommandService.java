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


    private String providerStr = 'provider "aws" {
                            profile    = "default"
                            region     = "eu-west-3"
                       }';
    private String deployStr = '#####VPC#########################################
                        resource "aws_vpc" "devvpc" {
                            cidr_block = "192.168.0.0/16"
                            tags = {
                                Name = "dev_env"
                            }
                        }

                        resource "aws_subnet" "devsub" {
                            vpc_id	= aws_vpc.devvpc.id
                            cidr_block	= "192.168.10.0/24"
                            tags = {
                                name = "sub_dev"
                            }
                        }

                        resource "aws_internet_gateway" "dev_ig" {
                            vpc_id = aws_vpc.devvpc.id
                        }

                        ###################################Security_group######################################

                        resource "aws_security_group" "devsg" {
                                vpc_id  = aws_vpc.devvpc.id

                                ingress {
                                        from_port       = 22
                                        to_port         = 22
                                        protocol        = "tcp"
                                        cidr_blocks     = ["81.251.137.52/32"]
                                }

                            dynamic "ingress" {
                            for_each = var.sg_ingress
                            content {
                            description      = lookup(ingress.value, "description", null)
                            from_port        = lookup(ingress.value, "from_port", null)
                            to_port          = lookup(ingress.value, "to_port", null)
                            protocol         = lookup(ingress.value, "protocol", null)
                            cidr_blocks      = lookup(ingress.value, "cidr_blocks", null)
                            }
                        }


                                egress {
                                        from_port       = 0
                                        to_port         = 0
                                        protocol        = "-1"
                                        cidr_blocks     = ["0.0.0.0/0"]
                                }
                            tags = {
                                Name = "devsg"
                            }

                        }
                        #####################EIP#####################################
                        resource "aws_eip" "deveip" {
                            instance = aws_instance.dev_ec2.id
                            vpc	= true
                        }

                        output "deveip" {
                            value = aws_eip.deveip.*.public_ip
                        }

                        resource "aws_key_pair" "dev_key" {
                        key_name   = "dev-key"
                        public_key = "%s"
                        }

                        resource "aws_instance" "dev_ec2" {
                                ami     = "ami-0c6ebbd55ab05f070"
                                instance_type   = "t2.micro"
                                key_name = "dev-key"
                                subnet_id = aws_subnet.devsub.id
                                vpc_security_group_ids  = [aws_security_group.devsg.id]
                                tags = {
                                        Name = "%s"
                                }
                        }';
        private String variableStr = 'variable "sg_ingress" {
                                default     = {
                                    "my ingress rule" = {
                                    "description" = "For SSH"
                                    "from_port"   = "22"
                                    "to_port"     = "22"
                                    "protocol"    = "tcp"
                                    "cidr_blocks" = ["%s/32"]
                                    }
                                }
                                        type        = map(any)
                                        description = "Security group rules"
                                }';



    public ResultCommand shellCommand(String command) throws Exception {
        try {
            File provider = new File("/tmp/provider.tf");
            File deploy = new File("/tmp/deploy.tf");
            File variable = new File ("/tmp/variable.tf");

            Filewriter providerWriter = new FileWriter("/tmp/provider.tf");
            Filewriter deployWriter = new FileWriter("/tmp/deploy.tf");
            Filewriter variableWriter = new FileWriter("/tmp/variable.tf");

            if(!provider.exists()) {
                provider.createNewFile();
                providerWriter.write(this.providerStr);
                providerWriter.close();
            }

            if(!deploy.exists()) {
                deploy.createNewFile();
                deployWriter.write(this.deployStr, "key","nomMachine");
                deployWriter.close();               
            }

            if(!variable.exists()) {
                variable.createNewFile();
                variableWriter.write(this.variableStr, "IP");
                variableWriter.close();               
            }


            // Commandes Terraforms

            logger.info("terraform init");

            boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");

            String homeDirectory = System.getProperty("user.home");
            
            if (isWindows) {
                process = Runtime.getRuntime()
                        .exec(String.format("cd /tmp/ || terraform init -force-copy"))
                        .exec(String.format("cd /tmp/ || terraform apply -auto-approve"));
            } else {
                process = Runtime.getRuntime()
                        .exec(String.format("cd /tmp/ || terraform init -force-copy"))
                        .exec(String.format("cd /tmp/ || terraform apply  -auto-approve"));
            }

            Process process = null;


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
            logger.info("closing connection");
        }

        throw new Exception("Erreur lors de l'execution de la commande");
    }

}
