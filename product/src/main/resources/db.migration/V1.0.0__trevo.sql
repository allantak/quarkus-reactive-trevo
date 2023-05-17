CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE PRODUCT (
	productId uuid DEFAULT uuid_generate_v4 (),
	productName varchar(50) NOT NULL UNIQUE,
	areaSize real,
	description text,
	culture varchar NOT NULL,
	PRIMARY KEY (product_id)
);

