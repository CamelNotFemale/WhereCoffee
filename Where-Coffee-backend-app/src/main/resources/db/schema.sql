create table if not exists grades_audit(
    c           varchar(1),
    stamp       timestamp,
    id           bigint,
    comment      varchar(255),
    date         timestamp,
    grade        integer not null,
    user_id      bigint,
    cafeteria_id bigint
);


CREATE OR REPLACE FUNCTION process_emp_audit() RETURNS TRIGGER AS '
    DECLARE
    BEGIN
        IF (TG_OP = ''INSERT'') THEN
            /* инсерт не нужен, т.к. hibernate все равно сначала создает пустой, а затем заполняет */
            INSERT INTO grades_audit SELECT ''I'', now(), NEW.*;
            RETURN NEW;
        ELSIF (TG_OP = ''UPDATE'') THEN
            INSERT INTO grades_audit SELECT ''U'', now(), NEW.*;
            RETURN NEW;
        ELSIF (TG_OP = ''DELETE'') THEN
            INSERT INTO grades_audit SELECT ''D'', now(), OLD.*;
            RETURN OLD;
        END IF;
        RETURN NULL; -- возвращаемое значение для триггера AFTER игнорируется
    END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS emp_audit ON grades;

CREATE TRIGGER emp_audit
    AFTER INSERT OR UPDATE OR DELETE ON grades
    FOR EACH ROW EXECUTE PROCEDURE process_emp_audit();