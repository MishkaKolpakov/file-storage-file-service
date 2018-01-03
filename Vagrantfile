# -*- mode: ruby -*-
# vi: set ft=ruby :

ENV['VAGRANT_DEFAULT_PROVIDER'] = 'virtualbox'

variables = ["MONGODB_USERNAME", "MONGODB_PASSWORD", "MONGODB_DATABASE"]

variables.each do |var|
  if !ENV.has_key?(var)
    raise "Please specify the `#{var}` environment variable"
  end
end

$set_environment_variables = <<SCRIPT
tee "/etc/profile.d/myvars.sh" > "/dev/null" <<EOF
export MONGODB_USERNAME=#{ENV['MONGODB_USERNAME']}
export MONGODB_PASSWORD=#{ENV['MONGODB_PASSWORD']}
export MONGODB_DATABASE=#{ENV['MONGODB_DATABASE']}
EOF
SCRIPT

$init_script = <<SCRIPT
apt-get update -y
apt-get dist-upgrade -y
apt-get install curl git openjdk-8-jdk-headless maven -y
apt-get autoclean -y
apt-get autoremove -y
mvn clean package -Dmaven.test.skip=true -f /vagrant/pom.xml -B
SCRIPT

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"

  config.vm.provider "virtualbox" do |vb|
    # Display the VirtualBox GUI when booting the machine
    # Uncomment if you want to interact with it directly
    # vb.gui = true
    vb.cpus = 1
    vb.memory = "2048"
  end

  config.vm.network "private_network", ip: "192.168.200.101"
  # Uncomment if you want the application to be accessible via localhost
  # for convenience, as it is always accessible via private ip

  #config.vm.network "forwarded_port", guest: 8080, host: 9001
  #config.vm.network "forwarded_port", guest: 27017, host: 27017

  config.vm.provision "shell", inline: $set_environment_variables, run: "always"
  config.vm.provision "shell", inline: $init_script
  config.vm.provision "docker" do |d|
    d.pull_images "bitnami/mongodb"
    d.pull_images "java:8-jdk-alpine"
    d.build_image "/vagrant",
      args: "-t fileservice"
    d.run "mongo", image: "bitnami/mongodb",
      args: "-p 27017:27017 -e MONGODB_USERNAME -e MONGODB_PASSWORD -e MONGODB_DATABASE -v /opt/mongo:/bitnami",
      restart: "always"
    d.run "fileservice", image: "fileservice",
      args: "-p 8080:8080 --link mongo:mongo -e MONGODB_USERNAME -e MONGODB_PASSWORD -e MONGODB_DATABASE",
      restart: "always"
  end
end
