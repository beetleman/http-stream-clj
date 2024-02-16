-- CREATE TABLE:
CREATE TABLE items (
  id SERIAL UNIQUE NOT NULL,
  code text,
  type text,
  name text
);


-- GENERATE DATA:
insert into items (
    code, type, name
)
select
    left(md5(i::text), 10),
    md5(random()::text),
    md5(random()::text)
from generate_series(1, 10000000) s(i);
