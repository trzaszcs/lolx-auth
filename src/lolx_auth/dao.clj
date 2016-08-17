(ns lolx-auth.dao)

(defn unique-emails-validator
  [users]
  (distinct?
   (map
    #(:email %)
    users)))

(def in-memory-db 
  (atom 
   [{
     :id "666"
     :first-name "John" 
     :last-name "Deer" 
     :email "john@wp.pl"
     :password "d74ff0ee8da3b9806b18c877dbf29bbde50b5bd8e4dad7a3a725000feb82e8f1"}] 
   :validator unique-emails-validator))

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
