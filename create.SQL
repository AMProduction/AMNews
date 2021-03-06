BEGIN;
CREATE SEQUENCE auto_id_news
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
CREATE TABLE "News"
(
  id bigint NOT NULL DEFAULT nextval('auto_id_news'::regclass),
  subject character varying NOT NULL,
  text_presenter character varying,
  text_news character varying NOT NULL,
  created_date timestamp without time zone,
  last_modified_date timestamp without time zone,
  CONSTRAINT "News_pkey" PRIMARY KEY (id)
);
COMMIT;
