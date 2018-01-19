PRAGMA foreign_keys = on;
CREATE TABLE veranstaltung (
 _id INTEGER PRIMARY KEY
 NOT NULL,
 sprache TEXT NOT NULL,
 ver_name TEXT NOT NULL,
 url TEXT NOT NULL
);
CREATE TABLE raum(
 _id TEXT PRIMARY KEY ON CONFLICT IGNORE 
 NOT NULL,
 gebaeude TEXT NOT NULL,
 zimmer TEXT NOT NULL,
 campus TEXT NOT NULL
);
CREATE TABLE gehoert_zu (
 ver_id INTEGER REFERENCES veranstaltung (_id) ON DELETE CASCADE
 NOT NULL,
 studiengang_id TEXT REFERENCES studiengang (_id) ON DELETE CASCADE
 NOT NULL,
 min_semester INTEGER,
 max_semester INTEGER,
 _id INTEGER PRIMARY KEY AUTOINCREMENT
 NOT NULL,
 UNIQUE (
 ver_id,
 studiengang_id
 )
 ON CONFLICT IGNORE
);
CREATE TABLE termin (
 _id INTEGER PRIMARY KEY 
 NOT NULL,
 dozent TEXT NOT NULL,
 tag INTEGER NOT NULL,
 begin INTEGER NOT NULL,
 ende INTEGER NOT NULL,
 hinweis TEXT ,
 start_datum TEXT NOT NULL,
 end_datum TEXT NOT NULL,
 typ INTEGER NOT NULL,
 gehoert_zu_gr INTEGER REFERENCES gruppe (_id) ON DELETE CASCADE,
 gehalten_in INTEGER REFERENCES raum (_id) ON DELETE RESTRICT,
 UNIQUE (
 _id,
 gehoert_zu_gr
 )
 ON CONFLICT ABORT
);
CREATE TABLE gruppe (
 _id INTEGER PRIMARY KEY
 NOT NULL,
 name TEXT NOT NULL,
 gehoert_zu_ver INTEGER REFERENCES veranstaltung (_id) ON DELETE CASCADE
 NOT NULL,
 UNIQUE (
 name,
 gehoert_zu_ver
 )
 ON CONFLICT ABORT
);
CREATE TABLE studiengang (
 _id TEXT PRIMARY KEY ON CONFLICT IGNORE
 NOT NULL
);
