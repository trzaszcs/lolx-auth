(ns lolx-auth.dao
  (:require [clj-time.core :refer [now]] ))



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
     :password "d74ff0ee8da3b9806b18c877dbf29bbde50b5bd8e4dad7a3a725000feb82e8f1"
     :state "Wielkopolskie"
     :city "Poznan"
     :created (now)}] 
   :validator unique-emails-validator))

(defn- update-user
  [id new-values]
  (try
    (swap! 
     in-memory-db
     (fn [users]
       (map
        (fn [user]
          (if (= id (user :id))
            (merge user new-values)
            user
            )
          )
        users
        )))
    true
    (catch IllegalStateException e false)))

(defn add-user
  [id first-name last-name email state city password]
  (try
    (swap! 
     in-memory-db
     (fn [users]
       (conj 
        users 
        {:id id 
         :first-name first-name 
         :last-name last-name 
         :email email 
         :state state 
         :city city 
         :password password
         :type "standard"
         :created (now)})))
    true
    (catch IllegalStateException e false)))

(defn add-fb-user
  [id first-name last-name email city facebook-id]
  (try
    (swap! 
     in-memory-db
     (fn [users]
       (conj 
        users 
        {:id id
         :first-name first-name 
         :last-name last-name 
         :email email 
         :city city 
         :type "facebook"
         :facebook-id facebook-id
         :created (now)})))
    true
    (catch IllegalStateException e false)))

(defn link-fb-account
  [id facebook-id]
  (update-user id {:facebook-id facebook-id}))

(defn update
  [id email first-name last-name state city]
  (update-user id {:email email :first-name first-name :last-name last-name :state state :city city}))

(defn find-by-id
  [id]
  (first
   (filter
    #(= id (:id %))
    @in-memory-db
    )))

(defn find-by-fb-id
  [fb-id]
  (first
   (filter
    #(= fb-id (:facebook-id %))
    @in-memory-db
    )))

(defn find-by-email
  [email]
  (first
   (filter
    #(= email (:email %))
    @in-memory-db
    )))

(defn find
  [email password]
  (first
   (filter
    #(and (= email (:email %)) (= password (:password %)))
    @in-memory-db
    )))

(defn change-password
  [id new-password]
  (update-user id {:password new-password}))

(defn reset-password
  [id ref-id]
  (update-user id {:blocked true :password-reset-ref-id ref-id})
  (find-by-id id))
