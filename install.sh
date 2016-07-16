sudo yum update
sudo yum install git
sudo yum install docker
sudo pip install docker-compose
sudo service docker start
docker volume create --name=mysql-data
sudo usermod -aG docker $(whoami)
