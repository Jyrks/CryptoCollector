Flyway looks for db/migration folder by default.
Just adding the dependency will cause Flyway to migrate on startup(Also requires spring-boot-starter-jdbc and postgresql).

Creating database:

Log into database as user postgres(root user):
sudo -u postgres psql

Create new Database user:
create user tester with password 'pass';

Create database:
create database etherbase;

Grant all privileges to user for that database:
grant all privileges on database etherbase to tester;
