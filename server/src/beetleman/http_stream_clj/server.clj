(ns beetleman.http-stream-clj.server
  (:require [mount.core :as mount]
            [ring.util.response :refer [response content-type]]
            [ring.core.protocols :as ring-protocols]
            [ring.adapter.jetty9 :as jetty]
            [charred.api :as charred]
            [clojure.java.io :as io]))

(def port 9900)
(def n 100)

(defn stream-json-coll [coll]
  (reify ring-protocols/StreamableResponseBody
    (write-body-to-stream [_ _ output-stream]
      (with-open [writer (io/writer output-stream)]
        (try
          (transduce (comp (map charred/write-json-str)
                           (interpose ",\n"))
                     (fn
                       ([]
                        (.write writer "["))
                       ([_]
                        (.write writer "]")
                        (.flush writer))
                       ([_ x]
                        (.write writer x)
                        (.flush writer)))
                     coll)
          (catch Exception e
            (println e)
            (throw e)))))))

(defn handler [_]
  (-> (range n)
      stream-json-coll
      response
      (content-type "application/json; charset=utf-8")))

(mount/defstate server
  :start (jetty/run-jetty #'handler
                          {:port  port
                           :join? false})
  :stop (jetty/stop-server server))

(comment

  (mount/start)

  )
