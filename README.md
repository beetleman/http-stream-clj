# Run

## Setup db:

```shell
./db.sh
```

Load `./setup.sql` using favorite pg client

## Run REPL

run REPL from `./server`.

```clojure
(start)

;; other commands
;; (stop)
;; (restart)
```

# Test

## get json

```shell
curl -N "http://localhost:9900"
```

## get json stream

```shell
curl -N "http://localhost:9900?stream"
```
