ALTER DATABASE coffee_shops SET TIMEZONE TO 'Europe/Moscow';
SELECT pg_reload_conf();

CREATE OR REPLACE FUNCTION cafe_by_grade(grade_id bigint) RETURNS bigint AS '
    BEGIN
        RETURN (
            SELECT cafeteria_id FROM grades
            WHERE id = grade_id
            LIMIT 1
        );
    END
' LANGUAGE plpgsql;
-- Учитываем перки кофейни по её отзывам
CREATE OR REPLACE FUNCTION process_perks_audit() RETURNS TRIGGER AS '
    DECLARE
    BEGIN
        IF (TG_OP = ''INSERT'') THEN
            INSERT INTO cafeterias_perks (cafeteria_id, perk_id, count)
            VALUES (cafe_by_grade(NEW.grade_id), NEW.perk_id, 1)
            ON CONFLICT (cafeteria_id, perk_id) DO UPDATE SET count=cafeterias_perks.count+1;
            RETURN NEW;
        ELSIF (TG_OP = ''DELETE'') THEN
            UPDATE cafeterias_perks SET count=cafeterias_perks.count-1
            WHERE cafeteria_id = cafe_by_grade(OLD.grade_id) AND perk_id = OLD.perk_id;
            DELETE FROM cafeterias_perks
            WHERE count = 0;
            RETURN OLD;
        END IF;
        RETURN NULL; -- возвращаемое значение для триггера AFTER игнорируется
    END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS perks_audit ON grade_perks;

CREATE TRIGGER perks_audit
    AFTER INSERT OR DELETE ON grade_perks
    FOR EACH ROW EXECUTE PROCEDURE process_perks_audit();

-- Пересчитываем рейтинг кофейни в зависимости от оценок
CREATE TABLE IF NOT EXISTS cafeterias_rating(
    cafeteria_id bigint  not null primary key
            references cafeterias,
    count        bigint not null,
    sum          bigint not null
);
CREATE OR REPLACE FUNCTION process_rating_audit() RETURNS TRIGGER AS '
    DECLARE cur_sum bigint;
    DECLARE cur_count bigint;
    BEGIN
        IF (TG_OP = ''UPDATE'') THEN
            -- по сути, INSERT (особенности загрузки hibernate)
            IF (OLD.cafeteria_id IS NULL) THEN
                INSERT INTO cafeterias_rating (cafeteria_id, count, sum)
                VALUES (NEW.cafeteria_id, 1, NEW.grade)
                ON CONFLICT (cafeteria_id) DO UPDATE SET
                    count=cafeterias_rating.count+1, sum=cafeterias_rating.sum+NEW.grade
                RETURNING sum, count INTO cur_sum, cur_count;
            -- непосредственно UPDATE
            ELSE
                UPDATE cafeterias_rating SET
                    sum=cafeterias_rating.sum + NEW.grade - OLD.grade
                WHERE cafeteria_id = NEW.cafeteria_id
                RETURNING sum, count INTO cur_sum, cur_count;
            END IF;
            -- пересчитываем средний рейтинг для INSERT/UPDATE
            UPDATE cafeterias SET rating = cur_sum::double precision/cur_count
            WHERE id = NEW.cafeteria_id;
        ELSIF (TG_OP = ''DELETE'') THEN
            UPDATE cafeterias_rating SET
                count=cafeterias_rating.count-1, sum=cafeterias_rating.sum-OLD.grade
            WHERE cafeteria_id = OLD.cafeteria_id
            RETURNING sum, count INTO cur_sum, cur_count;
            DELETE FROM cafeterias_rating
            WHERE count = 0;
            -- пересчитываем средний рейтинг для DELETE
            IF (cur_count != 0) THEN
                UPDATE cafeterias SET rating = cur_sum::double precision/cur_count
                WHERE id = NEW.cafeteria_id;
            ELSE
                UPDATE cafeterias SET rating = NULL
                WHERE id = OLD.cafeteria_id;
            END IF;
        END IF;
        RETURN NULL; -- возвращаемое значение для триггера AFTER игнорируется
    END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS rating_audit ON grades;

CREATE TRIGGER rating_audit
    AFTER UPDATE OR DELETE ON grades
    FOR EACH ROW EXECUTE PROCEDURE process_rating_audit();