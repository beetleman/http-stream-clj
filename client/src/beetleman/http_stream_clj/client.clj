(ns beetleman.http-stream-clj.client
  (:require [clj-http.client :as http]
            [charred.api :as charred]))

(-> (http/get "http://localhost:9900" {:as :stream})
    :body
    (charred/read-json))
