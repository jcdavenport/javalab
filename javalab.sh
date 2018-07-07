#!/bin/bash

add-apt-repository ppa:webupd8team/java
apt update && \
apt install \
vim \
terminator \
git \
curl \
xfonts-terminus \
inetutils-ping \
oracle-java8-installer \
oracle-java8-set-default \
libpostgresql-jdbc-java \
libpostgresql-jdbc-java-doc

curl https://jdbc.postgresql.org/download/postgresql-42.2.2.jar --output /appdata/postgresql-42.2.2.jar

curl https://download.jetbrains.com/idea/ideaIC-181.5540.3.tar.gz --output /appdata/ideaIC-181.5540.3.tar.gz

cp /appdata/postgresql-42.2.2.jar /usr/share/java

tar xf /appdata/ideaIC-*.tar.gz -C /opt/

cd /root
git clone https://www.github.com/jcdavenport/parrotbashrc.git
cp parrotbashrc/parrot.bashrc .bashrc
rm -r parrotbashrc

cd /opt/-*/bin && ./idea.sh

exit
