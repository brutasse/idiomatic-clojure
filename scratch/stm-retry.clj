;; scratch harness: deterministic STM retry.
;; Shape: T1 reads r first (pins old version), main commits a conflicting
;; write while T1 is still in-transaction, then T1 writes -> retry on commit.
;; If atom side effects inside dosync ran once, log has one entry; the
;; claim under test is that they run once per ATTEMPT -> two entries.
(def r (ref 0))
(def log (atom []))
(def signaled (promise))

(let [t1 (Thread. (fn []
                    (dosync
                     (deref r)              ;; pin read version BEFORE main's commit
                     (swap! log conj :attempt)
                     (deliver signaled :read)
                     (Thread/sleep 500)     ;; let main's commit land in the window
                     (alter r + 1))))]
  (.start t1)
  (deref signaled)
  (dosync (alter r + 5))                     ;; conflicting commit, lands before T1's write
  (.join t1))

(println "RESULT log=" @log "r=" @r "entries=" (count @log))
(System/exit 0)
