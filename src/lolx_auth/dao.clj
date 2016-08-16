(ns lolx-auth.dao)

(defn unique-emails-validator
  [users]
  (distinct?
   (map
    #(:email %)
    users)))

(def in-memory-db (atom [] :validator unique-emails-validator))

(defn add-user
  [id first-name last-name email password]
  (try
    (swap! 
     in-memory-db
     (fn [users]
       (conj 
        users 
        {:id id :first-name first-name :last-name last-name :email email :password password})))
    true
    (catch IllegalStateException e false)))

(defn find
  [email password]
  (first
   (filter
    #(and (= email (:email %)) (= password (:password %)))
    @in-memory-db
    )))
