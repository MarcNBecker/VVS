DROP DATABASE IF EXISTS `vvs`;

CREATE DATABASE `vvs` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Studiengangsleiter`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Studiengangsleiter` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Modulplan`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Modulplan` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Studiengang` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Vertiefungsrichtung` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Modul`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Modul` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Kurzbeschreibung` VARCHAR(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`ModulInstanz`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`ModulInstanz` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Modul` INT unsigned NOT NULL,
  `Modulplan` INT unsigned NOT NULL,
  `Credits` INT unsigned NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `INDEX_ModulInstanz_Modul` (`Modul` ASC),
  INDEX `INDEX_ModulInstanz_Modulplan` (`Modulplan` ASC),
  CONSTRAINT `UNIQUE_ModulInstanz` 
  	UNIQUE (`Modul`, `Modulplan`),
  CONSTRAINT `FK_ModulInstanz_Modul`
    FOREIGN KEY (`Modul`)
    REFERENCES `vvs`.`Modul` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_ModulInstanz_Modulplan`
    FOREIGN KEY (`Modulplan`)
    REFERENCES `vvs`.`Modulplan` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Fach`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Fach` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Kurzbeschreibung` VARCHAR(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `vvs`.`FachInstanz`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`FachInstanz` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Fach` INT unsigned NOT NULL,
  `ModulInstanz` INT unsigned NOT NULL,
  `Semester` INT unsigned NOT NULL,
  `Stunden` INT unsigned NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `INDEX_FachInstanz_Fach` (`Fach` ASC),
  INDEX `INDEX_FachInstanz_ModulInstanz` (`ModulInstanz` ASC),  
  CONSTRAINT `UNIQUE_FachInstanz` 
  	UNIQUE (`Fach`, `ModulInstanz`),
  CONSTRAINT `FK_FachInstanz_Fach`
    FOREIGN KEY (`Fach`)
    REFERENCES `vvs`.`Fach` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_FachInstanz_ModulInstanz`
    FOREIGN KEY (`ModulInstanz`)
    REFERENCES `vvs`.`ModulInstanz` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Kurs`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Kurs` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Modulplan` INT unsigned NOT NULL,
  `Kursname` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `KursMail` VARCHAR(255) COLLATE utf8_unicode_ci NOT NULL,
  `StudentenAnzahl` INT unsigned NOT NULL,
  `KurssprecherVorname` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `KurssprecherName` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `KurssprecherMail` VARCHAR(255) COLLATE utf8_unicode_ci NOT NULL,
  `KurssprecherTelefon` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Studiengangsleiter` INT unsigned NOT NULL,
  `SekretariatName` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `INDEX_Kurs_Modulplan` (`Modulplan` ASC),
  INDEX `INDEX_Kurs_Studiengangsleiter` (`Studiengangsleiter` ASC),
  CONSTRAINT `FK_Kurs_Modulplan`
    FOREIGN KEY (`Modulplan`)
    REFERENCES `vvs`.`Modulplan` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_Kurs_Studiengangsleiter`
    FOREIGN KEY (`Studiengangsleiter`)
    REFERENCES `vvs`.`Studiengangsleiter` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Blocklage`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Blocklage` (
  `Kurs` INT unsigned NOT NULL,
  `Semester` INT unsigned NOT NULL,
  `StartDatum` DATE NOT NULL,
  `EndDatum` DATE NOT NULL,
  `Raum` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`Kurs`, `Semester`),
  INDEX `INDEX_Blocklage_Kurs` (`Kurs` ASC),
  CONSTRAINT `FK_Blocklage_Kurs`
    FOREIGN KEY (`Kurs`)
    REFERENCES `vvs`.`Kurs` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Dozent`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Dozent` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Titel` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Name` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Vorname` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Strasse` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Wohnort` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Postleitzahl` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Mail` VARCHAR(255) COLLATE utf8_unicode_ci NOT NULL,
  `TelefonPrivat` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `TelefonMobil` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL, 
  `TelefonGeschaeftlich` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Fax` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Arbeitgeber` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Geschlecht` BOOLEAN NOT NULL,
  `Status` INT unsigned NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Kommentar`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Kommentar` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Dozent` INT unsigned NOT NULL,
  `Text` TEXT COLLATE utf8_unicode_ci NOT NULL,
  `Verfasser` INT unsigned NULL,
  `Timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  INDEX `INDEX_Kommentar_Dozent` (`Dozent` ASC),
  INDEX `INDEX_Kommentar_Verfasser` (`Verfasser` ASC),
  CONSTRAINT `FK_Kommentar_Dozent`
    FOREIGN KEY (`Dozent`)
    REFERENCES `vvs`.`Dozent` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_Kommentar_Verfasser`
    FOREIGN KEY (`Verfasser`)
    REFERENCES `vvs`.`Studiengangsleiter` (`ID`)
    ON DELETE SET NULL
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Anhang`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Anhang` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Dozent` INT unsigned NOT NULL,
  `Daten` BLOB NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `INDEX_Anhang_Dozent` (`Dozent` ASC),
  CONSTRAINT `FK_Anhang_Dozent`
    FOREIGN KEY (`Dozent`)
    REFERENCES `vvs`.`Dozent` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`DozentFach`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`DozentFach` (
  `Dozent` INT unsigned NOT NULL,
  `Fach` INT unsigned NOT NULL,
  PRIMARY KEY (`Dozent`, `Fach`),
  INDEX `INDEX_DozentFach_Dozent` (`Dozent` ASC),
  INDEX `INDEX_DozentFach_Fach` (`Fach` ASC),
  CONSTRAINT `FK_DozentFach_Dozent`
    FOREIGN KEY (`Dozent`)
    REFERENCES `vvs`.`Dozent` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_DozentFach_Fach`
    FOREIGN KEY (`Fach`)
    REFERENCES `vvs`.`Fach` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Vorlesung`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Vorlesung` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Kurs` INT unsigned NOT NULL,
  `FachInstanz` INT unsigned NOT NULL,
  `Dozent` INT unsigned NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `INDEX_Vorlesung_Kurs` (`Kurs` ASC),
  INDEX `INDEX_Vorlesung_FachInstanz` (`FachInstanz` ASC),
  INDEX `INDEX_Vorlesung_Dozent` (`Dozent` ASC),  
  CONSTRAINT `UNIQUE_Vorlesung` 
  	UNIQUE (`Kurs`, `FachInstanz`),
  CONSTRAINT `FK_Vorlesung_Kurs`
    FOREIGN KEY (`Kurs`)
    REFERENCES `vvs`.`Kurs` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_Vorlesung_FachInstanz`
    FOREIGN KEY (`FachInstanz`)
    REFERENCES `vvs`.`FachInstanz` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_Vorlesung_Dozent`
    FOREIGN KEY (`Dozent`)
    REFERENCES `vvs`.`Dozent` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Termin`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Termin` (
  `ID` INT unsigned NOT NULL AUTO_INCREMENT,
  `Vorlesung` INT unsigned NOT NULL,
  `Datum` DATE NOT NULL,
  `StartUhrzeit` TIME NOT NULL,
  `EndUhrzeit` TIME NOT NULL,
  `Pause` INT unsigned NOT NULL,
  `Raum` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `Klausur` BOOLEAN NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `INDEX_Termin_Vorlesung` (`Vorlesung` ASC),
  CONSTRAINT `FK_Termin_Vorlesung`
    FOREIGN KEY (`Vorlesung`)
    REFERENCES `vvs`.`Vorlesung` (`ID`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `vvs`.`Feiertag`
-- -----------------------------------------------------
CREATE TABLE `vvs`.`Feiertag` (
  `Datum` DATE NOT NULL,
  `Name` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`Datum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;