
DROP ALL OBJECTS;

CREATE TABLE USERS(
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(30) NOT NULL,
	username VARCHAR(30) UNIQUE NOT NULL,
	password VARCHAR(30) NOT NULL,
	role VARCHAR(10) NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE NEWS (
	id INT PRIMARY KEY AUTO_INCREMENT,
	title CLOB(300),
	description CLOB(2000),
	link CLOB(400)
);

CREATE TABLE EVALUATION(
	id INT PRIMARY KEY AUTO_INCREMENT,
	id_news INT NOT NULL REFERENCES NEWS(id),
	id_user INT NOT NULL REFERENCES USERS(id),
	user_note INT NOT NULL DEFAULT 0,
	click_date TIMESTAMP,
	send_date TIMESTAMP
);




CREATE TABLE EVALUATED_NEWS(
	id INT PRIMARY KEY AUTO_INCREMENT,
	title CLOB(300) NOT NULL,
	positive_notes INT,
	negative_notes INT,
	title_frequency VARCHAR(500)
);
