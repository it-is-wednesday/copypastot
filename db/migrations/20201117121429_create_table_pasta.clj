(ns migrations.20201117121429-create-table-pasta
  (:require [coast.db.migrations :refer :all]))

(defn change []
  (create-table
   :pasta
   (text :content)
   (text :author)
   (bool :approved)
   (timestamps)))
