(ns lolx-auth.validation)


(defn nil-or-empty? 
  [str]
  (or (nil? str) (empty? str))
)


(defn not-valid?
  [first-name last-name email password state city]
  (or (nil-or-empty? first-name)
      (nil-or-empty? last-name)
      (nil-or-empty? email)
      (nil-or-empty? password)
      (nil-or-empty? city)
      (nil-or-empty? state))
)

(defn authroization-valid? 
  [email password]
  (not (or (nil-or-empty? email) (nil-or-empty? password))))

(defn registration-valid? 
  [first-name last-name email password state city]
  (not (not-valid? first-name last-name email password city state)))
