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

                               "###################################Security_group###################################### "+

                               "resource \"aws_security_group\" \"devsg\" { "+
                               "vpc_id  = aws_vpc.devvpc.id "+

                               "ingress { "+
                               "from_port       = 22 "+
                               "to_port         = 22 "+
                               "protocol        = \"tcp\" "+
                               "cidr_blocks     = [\"81.251.137.52/32\"] "+
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
                               "#####################EIP##################################### "+
                               "resource \"aws_eip\" \"deveip\" { "+
                               "vpc	= true "+
                               "instance = aws_instance.dev_ec2.id "+
                               "} "+

                               "output \"deveip\" {" +
                               "value = aws_eip.deveip.*.public_ip" +
                               "}" +

                               "resource \"aws_key_pair\" \"dev_key\" { "+
                               "key_name   = \"dev-key\" "+
                               "public_key = \"%s\" "+
                               "} "+

                               "resource \"aws_instance\" \"dev_ec2\" { "+
                               "ami     = \"ami-0c6ebbd55ab05f070\" "+
                               "instance_type   = \"t2.micro\" "+
                               "key_name = \"dev-key\" "+
                               "subnet_id = aws_subnet.devsub.id "+
                               "vpc_security_group_ids  = [aws_security_group.devsg.id] "+
                               "tags = { "+
                               "Name = \"%s\" "+
                               "} "+
                               "}";

    public static String variableStr = "variable \"sg_ingress\" { " +
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
