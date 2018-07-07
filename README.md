docker run --name etherdb -p 5573:5432 -e POSTGRES_PASSWORD=pass -d postgres:9.4
docker exec -it etherdb bash
psql -U postgres

Flyway looks for db/migration folder by default.
Just adding the dependency will cause Flyway to migrate on startup(Also requires spring-boot-starter-jdbc and postgresql).

Creating database:
Log into database as user postgres(root user):
sudo -u postgres psql   (different in Docker - psql -U postgres)

Create new Database user:
create user tester with password 'pass';

Create database:
create database etherbase;

Grant all privileges to user for that database:
grant all privileges on database etherbase to tester;

Create sql dump:
pg_dump -U postgres etherbase > database_dump

Copy file to docker container:
docker cp /home/jyrks/database_dump etherdb:/database_dump

Create database from dump
psql -U postgres etherbase < database_dump
