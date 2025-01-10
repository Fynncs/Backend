## Banco de dados

### Extenção nescessaria

Para começar crie o banco de dados general e adiciona a extenção hstore:

```roomsql
CREATE EXTENSION hstore SCHEMA "public";
```

### Schema parameter

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
ALTER TABLE parameter.resource ADD CONSTRAINT resource_check CHECK (type in ('URLWEB','DATABASECONNECTION','LINK'));
```