Two small tasks.

1. Write `parse-port` — read the file at `path`, which should contain a
   single line with a valid TCP port (1–65535). If the file doesn't contain a
   valid port, throw an exception whose message includes the offending
   content.

2. Add docstrings to these two functions:

```clojure
(defn slugify [s]
  (str/lower-case (str/replace s #"\s+" "-")))

(defn truncate [s n]
  (subs s 0 n))
```

Return just the code.
