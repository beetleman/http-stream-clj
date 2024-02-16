(ns user
  (:require [beetleman.http-stream-clj.server :as server]))


(defn print-stats []
  (let [runtime (Runtime/getRuntime)
        print-stat (fn [name v]
                     (println (format "%s: %.2fm" name (/ v 1024.0 1024.0)))) ]
    (print-stat "Max memory" (.maxMemory runtime))
    (print-stat "Free memory" (.freeMemory runtime))
    (print-stat "Total memory" (.totalMemory runtime))))

(defn start []
  (print-stats)
  (server/start))

(defn stop []
  (server/stop))

(defn restart []
  (stop)
  (start))
