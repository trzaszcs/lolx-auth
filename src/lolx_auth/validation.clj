(ns lolx-auth.validation)


(defn nil-or-empty? 
  [str]
  (or (nil? str) (empty? str))
)


(defn not-valid?
  [first-name last-name email password]
  (or (nil-or-empty? first-name)
      (nil-or-empty? last-name)
      (nil-or-empty? email)
      (nil-or-empty? password))
)


(defn valid? 
  [first-name last-name email password]
  (not (not-valid? first-name last-name email password)))
