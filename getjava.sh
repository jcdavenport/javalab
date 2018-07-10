#!/bin/bash

add-apt-repository ppa:webupd8team/java \
    && apt-get update \
    && apt-get install -y \
        vim terminator git wget \
        xfonts-terminus inetutils-ping \
        oracle-java8-installer \
        oracle-java8-set-default \
        libpostgresql-jdbc-java \
        libpostgresql-jdbc-java-doc \
    && apt-get autoclean -y \
    && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/*

cd /root
git clone https://www.github.com/jcdavenport/parrotbashrc.git /root
cp /root/parrotbashrc/parrot.bashrc /root/.bashrc
rm -r /root/parrotbashrc

source /root/.bashrc
printf "\n NEW COMMAND LINE!!\n"

wget https://download.jetbrains.com/idea/ideaIC-2018.1.5.tar.gz -O /opt/ideaIC-2018.1.5.tar.gz \
    && tar xfz /opt/ideaIC-*.tar.gz -C /opt/

wget https://jdbc.postgresql.org/download/postgresql-42.2.2.jar -O /usr/share/java/postgresql-42.2.2.jar

bash /opt/idea-*/bin/idea.sh
