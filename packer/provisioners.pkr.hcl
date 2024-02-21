build {
  sources = [
    "source.docker.example"
  ]

  provisioner "shell-local" {
    command = "tar cf toupload/files.tar files"
  }
  provisioner "file" {
    destination = "/tmp/"
    source      = "./toupload"
  }
  provisioner "shell" {
    inline = [
      "cd /tmp && tar xf toupload/files.tar",
      "rm toupload/files.tar"
    ]
  }
}
