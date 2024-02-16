(ns beetleman.http-stream-clj.client
  (:require [clj-http.client :as http]
            [charred.api :as charred]))

(comment


  ;; not sql streaming
  (-> (http/get "http://localhost:9900" {:as :stream})
      :body
      (charred/read-json))


  ;; streaming
  (-> (http/get "http://localhost:9900?stream" {:as :stream})
      :body
      (charred/read-json)))
