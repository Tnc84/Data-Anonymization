-- Initialize database schema and test data
-- This script runs automatically on application startup

DROP TABLE IF EXISTS "USER";
CREATE TABLE "USER"(ID INT PRIMARY KEY, EMAIL VARCHAR(255));
INSERT INTO "USER" VALUES(1, 'Bogdan');
INSERT INTO "USER" VALUES(2, 'Lori');

