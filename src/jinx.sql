CREATE DATABASE jinx;
use jinx;

CREATE TABLE Player(
                       name varchar(100) PRIMARY KEY NOT NULL,
                       password integer NOT NULL
);

CREATE TABLE PlayerHistory(
                              id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
                              player varchar(100) NOT NULL,
                              saved date NOT NULL,
                              score integer NOT NULL,
                              numLuckCards integer NOT NULL
);

CREATE TABLE PlayerHistoryEnemy(
                                   id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
                                   ph_id integer NOT NULL,
                                   enemy varchar(100) NOT NULL,
                                   scorce integer NOT NULL
);

CREATE TABLE Speicher(
                         id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
                         created date NOT NULL
);

CREATE TABLE Runde(
                      id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
                      s_id integer NOT NULL
);

CREATE TABLE Tisch(
                      id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
                      cardStack LONGTEXT,
                      luckCardStack LONGTEXT,
                      field LONGTEXT,
                      r_id integer NOT NULL
);

CREATE TABLE Spieler(
                        id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
                        name varchar(100),
                        cards LONGTEXT,
                        luckCards LONGTEXT,
                        score integer,
                        sleeptime integer,
                        manualNextMsg boolean,
                        diceCount integer,
                        rolls integer,
                        active boolean,
                        ai varchar(100),
                        r_id integer
);

CREATE TABLE Action(
                       id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
                       zug LONGTEXT,
                       luckCard LONGTEXT,
                       card LONGTEXT,
                       s_id integer,
                       p_id integer NOT NULL
);

# add Foreignkey for player
ALTER TABLE PlayerHistory ADD foreign key (player) REFERENCES Player(name);
# add Foreignkey for PlayerHistoryEnemies
ALTER TABLE PlayerHistoryEnemy ADD FOREIGN KEY (ph_id) REFERENCES PlayerHistory(id);
ALTER TABLE PlayerHistoryEnemy ADD FOREIGN KEY (enemy) REFERENCES Player(name);

# add Foreignkey for Runde
ALTER TABLE Runde ADD foreign key (s_id) REFERENCES Speicher(id);

# add Foreignkey for Tisch
ALTER TABLE Tisch ADD FOREIGN KEY (r_id) REFERENCES Runde(id);
# add Foreignkey Spieler
ALTER TABLE Spieler ADD FOREIGN KEY (r_id) REFERENCES Runde(id);
# add Foreignkey Action
ALTER TABLE Action ADD FOREIGN KEY (s_id) REFERENCES Speicher(id);
ALTER TABLE Action ADD FOREIGN KEY (p_id) REFERENCES Spieler(id);