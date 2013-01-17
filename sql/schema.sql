
--  ## MySQL 5.1+

GRANT ALL ON essc.* TO essc@localhost IDENTIFIED BY 'essc';
FLUSH PRIVILEGES;

CREATE TABLE `product` (
  `id`               INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name`             VARCHAR(255) NOT NULL UNIQUE KEY,
  `extIdJira`        VARCHAR(255) DEFAULT NULL,
  `extIdBugzilla`    VARCHAR(255) DEFAULT NULL,
  `note`             VARCHAR(255) DEFAULT NULL,

  `gitHash`          VARCHAR(255) DEFAULT NULL,
  `link508`          TEXT DEFAULT NULL,
  `linkBrew`         TEXT DEFAULT NULL,
  `linkBuildHowto`   TEXT DEFAULT NULL,
  `linkCC`           TEXT DEFAULT NULL,
  `linkCodeCoverage` TEXT DEFAULT NULL,
  `linkGitRepo`      TEXT DEFAULT NULL,
  `linkIssuesFixed`  TEXT DEFAULT NULL,
  `linkIssuesFound`  TEXT DEFAULT NULL,
  `linkJavaEE`       TEXT DEFAULT NULL,
  `linkJavadoc`      TEXT DEFAULT NULL,
  `linkMavenLocalRepo`   TEXT DEFAULT NULL,
  `linkMead`             TEXT DEFAULT NULL,
  `linkMeadJob`          TEXT DEFAULT NULL,
  `linkReleasedBinaries` TEXT DEFAULT NULL,
  `linkReleasedDocs`     TEXT DEFAULT NULL,
  `linkStagedBinaries`   TEXT DEFAULT NULL,
  `linkStagedDocs`       TEXT DEFAULT NULL,
  `linkTattleTale`       TEXT DEFAULT NULL,
  `linkTck`              TEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `prod_custField` (
  `id`              INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `product_id`      INT UNSIGNED NOT NULL,
  `name`            VARCHAR(255) NOT NULL,
  UNIQUE KEY `prodId_name` (`product_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `release` (
  `id`              INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `product_id`      INT UNSIGNED NOT NULL,
  `version`         VARCHAR(255) DEFAULT NULL,
  `extIdJira`       VARCHAR(255) DEFAULT NULL,
  `extIdBugzilla`   VARCHAR(255) DEFAULT NULL,
  `internal`        BOOLEAN NOT NULL,
  `lastChanged`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `note`            VARCHAR(255) DEFAULT NULL,
  `plannedFor`      DATE DEFAULT NULL,
  `status`          TINYINT UNSIGNED DEFAULT NULL,

  `gitHash`         VARCHAR(255) DEFAULT NULL,
  `link508`         TEXT DEFAULT NULL,
  `linkBrew`        TEXT DEFAULT NULL,
  `linkBuildHowto`  TEXT DEFAULT NULL,
  `linkCC`          TEXT DEFAULT NULL,
  `linkCodeCoverage` TEXT DEFAULT NULL,
  `linkGitRepo`     TEXT DEFAULT NULL,
  `linkIssuesFixed` TEXT DEFAULT NULL,
  `linkIssuesFound` TEXT DEFAULT NULL,
  `linkJavaEE`      TEXT DEFAULT NULL,
  `linkJavadoc`     TEXT DEFAULT NULL,
  `linkMavenLocalRepo` TEXT DEFAULT NULL,
  `linkMead`        TEXT DEFAULT NULL,
  `linkMeadJob`     TEXT DEFAULT NULL,
  `linkReleasedBinaries` TEXT DEFAULT NULL,
  `linkReleasedDocs`     TEXT DEFAULT NULL,
  `linkStagedBinaries`   TEXT DEFAULT NULL,
  `linkStagedDocs`  TEXT DEFAULT NULL,
  `linkTattleTale`  TEXT DEFAULT NULL,
  `linkTck`         TEXT DEFAULT NULL,
  KEY `product_id` (`product_id`),
  CONSTRAINT `product_id_fk` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `rel_custField` (
  `id`              INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `field_id`        INT UNSIGNED NOT NULL,
  `release_id`      INT UNSIGNED NOT NULL,
  `val`             VARCHAR(255) NOT NULL,
  UNIQUE KEY `fieldId_relId` (`field_id`, `release_id`),
  CONSTRAINT `field_id_fk`   FOREIGN KEY (`field_id`)   REFERENCES `prod_custFields` (`id`),
  CONSTRAINT `release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id`   INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `mail` VARCHAR(255) DEFAULT NULL UNIQUE KEY,
  `name` VARCHAR(255) NOT NULL UNIQUE KEY,
  `pass` VARCHAR(255) NOT NULL,
  `showProd` BOOLEAN NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
