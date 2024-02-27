packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = ">= 1"
    }
  }
}

variable "project_id1" {
  type    = string
  default = "dev-csye-6225-415001"
}

variable "source_image" {
  type    = string
  default = "centos-stream-8-v20240110"
}

variable "zone" {
  type    = string
  default = "us-central1-a"
}

variable "machine_type" {
  type    = string
  default = "e2-medium"
}

variable "ssh_username" {
  type    = string
  default = "packer"
}

source "googlecompute" "webapp" {
  project_id  = var.project_id
  source_image  = var.source_image
  zone          = var.zone
  instance_name = "csye6225-${formatdate("YYYYMMDDhhmmss", timestamp())}"
  ssh_username  = var.ssh_username
  machine_type  = var.machine_type
  tags          = ["csye6225"]
}


build {
  sources = ["sources.googlecompute.webapp"]

  provisioner "file" {
    source      = "webservice.service"
    destination = "/tmp/webservice.service"
  }

  provisioner "file" {
    source      = "../target/Web-Application-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/Web-Application-0.0.1-SNAPSHOT.jar"
  }
  provisioner "shell" {
    inline = [
      "sudo groupadd -f csye6225",
      "sudo useradd -s /sbin/nologin -g csye6225 -d /opt/csye6225 -m csye6225",
      "sudo mv /tmp/webservice.service /etc/systemd/system/",
      "sudo mv /tmp/Web-Application-0.0.1-SNAPSHOT.jar /opt/csye6225/webapp-0.0.1-SNAPSHOT.jar",
      "sudo chown -R csye6225:csye6225 /opt/csye6225/webapp-0.0.1-SNAPSHOT.jar",
      "sudo chmod 750 /opt/csye6225/webapp-0.0.1-SNAPSHOT.jar",
      "sudo yum update -y",
      "sudo yum install -y java-17-openjdk-devel",
      "echo 'Completed Java Installation'",
      "sudo yum install -y mysql-server",
      "sudo systemctl start mysqld",
      "sudo systemctl enable mysqld",
      "sudo systemctl status mysqld",
      "echo \"Installing mysql\"",
      "sudo mysql -u root -e \"CREATE USER 'vamsi'@'localhost' IDENTIFIED BY 'STEYNlee@858';\" ",
      "sudo mysql -u root -e \"GRANT ALL PRIVILEGES ON *.* TO 'vamsi'@'localhost' with grant option;\"",
      "sudo mysql -u root -e \"FLUSH PRIVILEGES;\"",
      "sudo systemctl daemon-reload",
      "sudo systemctl start webservice.service",
      "sudo systemctl enable webservice.service"
    ]
  }
}
