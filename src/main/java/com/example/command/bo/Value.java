package com.example.command.bo;

public class Value {

    public static String providerStr = "provider \"aws\" {" +
                                 "profile    = \"default\" " +
                                 "region     = \"eu-west-3\" " +
                                 "}";

    public static String deployStr = "#####VPC#########################################" +
                               "resource \"aws_vpc\" \"devvpc\" { "+
                               "cidr_block = \"192.168.0.0/16\" "+
                               "tags = { "+
                               "Name = \"dev_env\" "+
                               "} "+
                               "}" +

                               "resource \"aws_subnet\" \"devsub\" { "+
                               "vpc_id	= aws_vpc.devvpc.id "+
                               "cidr_block	= \"192.168.10.0/24\" "+
                               "tags = { "+
                               "name = \"sub_dev\" "+
                               "} "+
                               "} "+

                               "resource \"aws_internet_gateway\" \"dev_ig\" { "+
                               "vpc_id = aws_vpc.devvpc.id "+
                               "} "+

                               "resource \"aws_route_table\" \"devinst\" {"+
                                        "vpc_id  = aws_vpc.devvpc.id"+
                                        "route {"+
                                                "cidr_block = \"0.0.0.0/0\""+
                                                "gateway_id = aws_internet_gateway.dev_ig.id"+
                                        "}"+
                                "}"+

                                "resource \"aws_route_table_association\" \"devsubr\" {"+
                                        "subnet_id       = aws_subnet.devsub.id"+
                                        "route_table_id  = aws_route_table.devinst.id"+
                                "}"+

                               "###################################Security_group###################################### "+

                               "resource \"aws_security_group\" \"devsg\" { "+
                               "vpc_id  = aws_vpc.devvpc.id "+

                               "ingress { "+
                               "from_port       = 22 "+
                               "to_port         = 22 "+
                               "protocol        = \"tcp\" "+
                               "cidr_blocks     = [\"15.236.133.144/32\"] "+
                               "} "+

                               "dynamic \"ingress\" { "+
                               "for_each = var.sg_ingress "+
                               "content { "+
                               "description      = lookup(ingress.value, \"description\", null) "+
                               "from_port        = lookup(ingress.value, \"from_port\", null) "+
                               "to_port          = lookup(ingress.value, \"to_port\", null) "+
                               "protocol         = lookup(ingress.value, \"protocol\", null) "+
                               "cidr_blocks      = lookup(ingress.value, \"cidr_blocks\", null) "+
                               "} "+
                               "} "+


                               "egress { "+
                               "from_port       = 0 "+
                               "to_port         = 0 "+
                               "protocol        = \"-1\" "+
                               "cidr_blocks     = [\"0.0.0.0/0\"] "+
                               "} "+
                               "tags = { "+
                               "Name = \"devsg\" "+
                               "} "+

                               "} "+
                               "#####################KEY#####################################"+
                                "resource \"aws_key_pair\" \"dev_key\" {"+
                                "key_name   = \"dev-key\""+
                                "public_key = %s "+
                                "}"+

                                "resource \"aws_key_pair\" \"ans_key\" {"+
                                    "key_name = \"ans_key\""+
                                    "public_key = \"${file(var.ans_pub_key)}\""+
                                "}"+
                                "#####################EC2 instance############################"+

                                "resource \"aws_instance\" \"dev_ec2\" {"+
                                    "ami	= \"ami-0f5094faf16f004eb\""+
                                    "instance_type	= \"t2.micro\""+
                                    "key_name = \"dev-key\""+
                                    "associate_public_ip_address = true"+
                                    "subnet_id = aws_subnet.devsub.id"+
                                    "vpc_security_group_ids	= [aws_security_group.devsg.id]"+
                                    "user_data = <<-EOF"+
                                        "#!/bin/bash"+
                                        "echo PermitRootLogin yes >> /etc/ssh/sshd_config"+
                                        "systemctl reload sshd.service"+
                                        "echo \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQC+WARRS/Q3mGagTqlxROmwPQ1hkIestZ4Hi7uwO3OzX4pgaVrCyzpZps9Vg8qn51Qef0ZibC8pYj+sh+Ood+KsbtUvm8uiL4KUfWxybriyokRjZfXxyRaLAyC5O1Ma6hQr8C5Nl7NcVaRNIwhndcEGF0HRKQFyqqA5P+PLyrR9iv/XXjCrHG+xzM+7FG0Z6EWmHuPxJlQEeqzFW/LbUA+SVzqvBBQmOmTUKIQDANQ24vySqJepY90+gZ1gDwDo9L9D/n4kJPjw4wIgz2/z9WcqsSKqHeqmQEVuT9pE9+2OWDODyIx5o8r0fJDxwNysfYxcY+WyjvHD2sp05Qa76PzCUv7JG9XMLQ0UL1b9Jf2D64rI9SEDMXBjsL196gPEDn4P6JKDxshq1C/BCWlD1kMgABHUIWCGveW8w1Gr84jHrxsv9qOAhX8LkRd2iy6/JLRfv6uxQCF33wCcXCDBxNaqhqqcjpwInCwgr+pgg1hzzNClTs6Q3gZhdxETsor6oAk= thomas@DESKTOP-RHES848\" > /root/.ssh/authorized_keys"+
                                    "EOF"+
                                    "tags = {"+
                                        "Name = %s"+
                                    "}"+
                                    
                                    "provisioner \"remote-exec\" {"+
                                        "inline = [\"echo CONNECTED\",]"+
                                    "}"+
                                    
                                    "connection {"+
                                        "host	= \"${aws_instance.dev_ec2.public_ip}\""+
                                        "type	= \"ssh\""+
                                        "user	= \"root\""+
                                        "private_key = \"${file(var.ans_pri_key)}\""+
                                       "}"
                                    
                                    "provisioner \"local-exec\" {"+
                                                "working_dir = \"/etc/ansible/\""+
                                                "command = \"echo ${aws_instance.dev_ec2.public_ip} >> hosts\""+

                                        "}"+

                                    "provisioner \"local-exec\" {"+
                                            "working_dir = \"/etc/ansible/\""+
                                            "command = \"ansible-playbook -u root --private-key ${var.ans_pri_key} lamp_pb.yml -i ${aws_instance.dev_ec2.public_ip},\""+

                                    "}"+	
                                "}"+

                                "output \"devpubip\" {"+
                                    "value = aws_instance.dev_ec2.public_ip"+
                                "}";

    public static String variableStr = 
                                "variable \"ans_pub_key\" {"+
                                    "default = \"/home/ec2-user/.ssh/anskey.pub\""+
                                "}"+

                                "variable \"ans_pri_key\" {"+
                                    "default = \"/home/ec2-user/.ssh/anskey.pem\""+
                                "}"+
                                "variable \"sg_ingress\" { " +
                                 "default     = { " +
                                 "\"my ingress rule\" = { "+
                                 "\"description\" = \"For SSH\" "+
                                 "\"from_port\"   = \"22\" "+
                                 "\"to_port\"     = \"22\" "+
                                 "\"protocol\"    = \"tcp\" "+
                                 "\"cidr_blocks\" = [\"%s/32\"] "+
                                 "} " +
                                 "} " +
                                 "type        = map(any) "+
                                 "description = \"Security group rules\" " +
                                 "}";

}
