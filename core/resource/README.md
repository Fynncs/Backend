## Banco de dados

### Extenção nescessaria
Para começar crie um banco de dados e adiciona a extenção hstore:
```roomsql
CREATE EXTENSION hstore SCHEMA "public" VERSION 1.8;
```

### Schema general
```roomsql
CREATE SCHEMA "general" AUTHORIZATION postgres;
```

### Tabela de recurso
```roomsql
CREATE TABLE "general".resource (
	id varchar(50) NOT NULL,
	"type" varchar(50) NOT NULL,
	description varchar(200) NULL,
	info public.hstore NULL,
	CONSTRAINT resource_pk PRIMARY KEY (id, type)
);
CREATE INDEX resource_id_idx ON general.resource USING btree (id);
CREATE INDEX resource_type_idx ON general.resource USING btree (type);
```