CREATE TABLE entry_point (
  entry_id INTEGER NOT NULL,
  point    INTEGER NOT NULL,
  `from`   DATETIME DEFAULT now(),
  thru     DATETIME DEFAULT '9999-12-31 23:59:59',
  PRIMARY KEY (entry_id, thru),
  FOREIGN KEY (entry_id) REFERENCES entry (entry_id)
    ON DELETE CASCADE
)
  ENGINE = InnoDB;

