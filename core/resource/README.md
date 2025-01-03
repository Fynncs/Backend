## Banco de dados

### Extenção nescessaria

Para começar crie um banco de dados e adiciona a extenção hstore:

```roomsql
CREATE EXTENSION hstore SCHEMA "public" VERSION 1.8;
```

### Schema general

```roomsql
CREATE SCHEMA parameter AUTHORIZATION postgres;
```

### Tabela de recurso

```roomsql
CREATE TABLE parameter.resource (
	id varchar(50) NOT NULL,
	"type" varchar(50) NOT NULL,
	description varchar(200) NULL,
	info public.hstore NULL,
	CONSTRAINT resource_pk PRIMARY KEY (id, type)
);
CREATE INDEX resource_id_idx ON parameter.resource USING btree (id);
CREATE INDEX resource_type_idx ON parameter.resource USING btree (type);
```