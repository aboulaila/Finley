-- Table Definition ----------------------------------------------

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email character varying(255) NOT NULL UNIQUE,
    name character varying(255),
    picture_url text,
    provider character varying(255) NOT NULL,
    provider_id character varying(255),
    password_hash character varying(255),
    roles text[] NOT NULL DEFAULT ARRAY['ROLE_USER'::text],
    enabled boolean NOT NULL DEFAULT true,
    account_non_expired boolean NOT NULL DEFAULT true,
    credentials_non_expired boolean NOT NULL DEFAULT true,
    account_non_locked boolean NOT NULL DEFAULT true,
    email_verified boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    last_login timestamp with time zone,
    CONSTRAINT users_provider_provider_id_key UNIQUE (provider, provider_id)
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX users_pkey ON users(id int4_ops);
CREATE UNIQUE INDEX users_email_key ON users(email text_ops);
CREATE UNIQUE INDEX users_provider_provider_id_key ON users(provider text_ops,provider_id text_ops);

-- Table Definition ----------------------------------------------

CREATE TABLE conversations (
    user_id character varying NOT NULL,
    messages jsonb NOT NULL DEFAULT '[]'::jsonb,
    id SERIAL PRIMARY KEY
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX conversations_pkey ON conversations(id int4_ops);

-- Table Definition ----------------------------------------------

CREATE TABLE agent_response (
    id BIGSERIAL PRIMARY KEY,
    conversation_id bigint NOT NULL REFERENCES conversations(id),
    thought_process text NOT NULL,
    message text NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX agent_response_pkey ON agent_response(id int8_ops);

-- Table Definition ----------------------------------------------

CREATE TABLE product_configurations (
    id SERIAL PRIMARY KEY,
    product_category character varying(255) NOT NULL UNIQUE,
    product_description text NOT NULL,
    information_gathering_list text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX product_configurations_pkey ON product_configurations(id int4_ops);
CREATE UNIQUE INDEX product_configurations_product_category_key ON product_configurations(product_category text_ops);

-- Table Definition ----------------------------------------------

CREATE TABLE user_intents (
    id SERIAL PRIMARY KEY,
    user_id character varying(255) NOT NULL,
    intent character varying(50) NOT NULL,
    mood character varying(50) NOT NULL,
    context text,
    summary text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    conversation_id bigint REFERENCES conversations(id)
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX user_intents_pkey ON user_intents(id int4_ops);