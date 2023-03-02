(ns server
  (:require [coast]
            [routes])
  (:gen-class))

(def app (coast/app {:routes routes/routes}))

(defn -main [& [host port]]
  (coast/server app {:ip (or host "localhost") :port port}))

(comment
  (-main))
