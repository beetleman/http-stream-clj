(ns beetleman.http-stream-clj.server
  (:require [clojure.java.io :as io]
            [honey.sql :as sql]
            [jsonista.core :as jsonista]
            [mount.core :as mount]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :refer [as-unqualified-lower-maps datafiable-row]]
            [ring.adapter.jetty9 :as jetty]
            [ring.core.protocols :as ring-protocols]
            [ring.util.response :refer [response content-type]]))

(set! *warn-on-reflection* true)

(mount/defstate ds
  :start {:dbtype   "postgres"
          :dbname   "postgres"
          :user     "postgres"
          :password (System/getenv "DB_PASSWORD")
          :port     (Integer/parseInt (System/getenv "DB_PORT"))})

(def port 9900)

(defn stream-json-coll [coll xform]
  (reify ring-protocols/StreamableResponseBody
    (write-body-to-stream [_ _ output-stream]
      (with-open [writer (io/writer output-stream)]
        (try
          (transduce (comp xform
                           (map jsonista/write-value-as-string)
                           (interpose ",\n"))
                     (fn
                       ([]
                        (.write writer "["))
                       ([_]
                        (.write writer "]")
                        (.flush writer))
                       ([_ ^String x]
                        (.write writer x)))
                     coll)
          (catch org.eclipse.jetty.io.EofException _)
          (catch Exception e
            (println e)
            (throw e)))))))

(defn handler [{:keys [query-string]}]
  (-> (jdbc/plan ds
                 (sql/format {:select [:*]
                              :from   [:items]})
                 ;; https://jdbc.postgresql.org/documentation/query/#getting-results-based-on-a-cursor
                 (if (= query-string "stream")
                   {:fetch-size  100
                    :concurrency :read-only
                    :auto-commit false
                    :cursors     :close
                    :result-type :forward-only
                    :builder-fn  as-unqualified-lower-maps}
                   {:builder-fn  as-unqualified-lower-maps}))
      (stream-json-coll (map #(datafiable-row % ds {})))
      response
      (content-type "application/json; charset=utf-8")))

(mount/defstate server
  :start (jetty/run-jetty #'handler
                          {:port  port
                           :join? false})
  :stop (jetty/stop-server server))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))
