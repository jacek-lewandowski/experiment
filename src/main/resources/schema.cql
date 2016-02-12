CREATE KEYSPACE IF NOT EXISTS experiment 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE IF NOT EXISTS experiment.surveys (
	category			TEXT,
	id					INT,
	caption				TEXT,
	required			BOOLEAN,
	validator_name		TEXT,
	validator_params	MAP<TEXT, TEXT>,
	
	PRIMARY KEY 		(category, id)
);

CREATE TABLE IF NOT EXISTS experiment.text_resources (
	key					TEXT,
	value				TEXT,
	
	PRIMARY KEY 		(key)
);

CREATE TABLE IF NOT EXISTS experiment.variables (
    category			TEXT,
	id					INT,
	name				TEXT,
	plus				TEXT,
	minus				TEXT,
	plus_exclusions		SET<INT>,
	minus_exclusions	SET<INT>,

	PRIMARY KEY 		(category, id)
);

CREATE TABLE IF NOT EXISTS experiment.users (
	code				TEXT,
	email_address		TEXT,
	completed_stages	SET<TEXT>,
	current_stage       TEXT,
	category			TEXT,

	PRIMARY KEY			(code)
);

CREATE TABLE IF NOT EXISTS experiment.groups (
	code				TEXT,
	category			TEXT,

	PRIMARY KEY			(code)
);

CREATE TABLE IF NOT EXISTS experiment.stage_data (
	user_code			TEXT,
	stage				TEXT,
	key					TEXT,
	idx					INT,
	data				TEXT,

	PRIMARY KEY ((user_code, stage), key, idx)
);
