(ns user
  (:require [beetleman.http-stream-clj.server :as server]))

(defn start []
  (server/start))

(defn stop []
  (server/stop))

(defn restart []
  (stop)
  (start))
