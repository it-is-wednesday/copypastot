(ns migrations.20201117121429-create-table-pasta
  (:require [coast.db.migrations :refer :all]
            [coast.db.connection :refer [spec]]))

(defn change []
  (create-table
   :pasta
   (text :content)
   (text :author)
   (bool :approved)
   (timestamp :created-at :null false :default (get-in sql [(spec :adapter) :now]))))
