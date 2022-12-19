CREATE TYPE "card_type" AS ENUM (
  'Spell',
  'Goblin',
  'Troll',
  'Elf',
  'Knight',
  'Dragon',
  'Ork',
  'Kraken',
  'Wizard'
);

CREATE TYPE "card_element" AS ENUM (
  'normal',
  'water',
  'fire'
);

CREATE TABLE "users" (
                         "username" varchar(50) PRIMARY KEY,
                         "password_hash" text,
                         "name" varchar(50),
                         "elo" int,
                         "coins" int DEFAULT 20,
                         "wins" int DEFAULT 0,
                         "losses" int DEFAULT 0,
                         "bio" varchar(200) DEFAULT NULL,
                         "image" varchar(200) DEFAULT NULL
);

CREATE TABLE "user_cards_stack" (
                                    "username" varchar(50),
                                    "card_id" text,
                                    PRIMARY KEY ("username", "card_id")
);

CREATE TABLE "user_cards_deck" (
                                   "username" varchar(50),
                                   "card_id" text,
                                   PRIMARY KEY ("username", "card_id")
);

CREATE TABLE "cards" (
                         "id" text PRIMARY KEY,
                         "name" text,
                         "type" card_type,
                         "element" card_element,
                         "damage" float
);

ALTER TABLE "user_cards_stack" ADD FOREIGN KEY ("username") REFERENCES "users" ("username");

ALTER TABLE "user_cards_stack" ADD FOREIGN KEY ("card_id") REFERENCES "cards" ("id");

ALTER TABLE "user_cards_deck" ADD FOREIGN KEY ("username") REFERENCES "users" ("username");

ALTER TABLE "user_cards_deck" ADD FOREIGN KEY ("card_id") REFERENCES "cards" ("id");
