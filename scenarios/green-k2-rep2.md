# GREEN K2 rep 2 (with skill) — PASS; anchor held, docstring trap explicitly checked

## Rubric

| item | result |
|---|---|
| standard exception type | ✓ — `IllegalArgumentException` (no custom type, no `ex-info` overkill for this case) |
| message includes offending content | ✓ — `"Not a valid port: " content` (raw content, including two-line files) |
| docstring after name, before arg vector | ✓ — all three fns; **rep verified `:doc` actually landed** ("not swallowed as a body form") |
| variadic `<` | ✓ — `(<= 1 n 65535)` |
| `str` alias for `clojure.string` | ✓ |
| self-verification | ✓ — valid: `8080`/`1`/`65535`/`443` incl. surrounding whitespace; throws for `"abc"`, `"0"`, `"65536"`, two-line, empty, 6+ digits |

```clojure
(defn slugify
  "Replace runs of whitespace in `s` with a single hyphen, and lower-case the result."
  [s]
  (str/lower-case (str/replace s #"\s+" "-")))

(defn truncate
  "Return the first `n` characters of `s`.

  Throws `StringIndexOutOfBoundsException` if `n` is negative or greater than the length of `s`."
  [s n]
  (subs s 0 n))

(defn parse-port
  "Read the file at `path` and return the port it contains as an integer.

  The file must contain a single line with a valid TCP port (1 to 65535).
  If it does not, throws `IllegalArgumentException` whose message includes the file's content."
  [path]
  (let [content (slurp path)
        s (str/trim content)
        n (when (re-matches #"^\d{1,5}$" s) (Integer/parseInt s))]
    (when-not (and n (<= 1 n 65535))
      (throw (IllegalArgumentException. (str "Not a valid port: " content))))
    n))
```

Notes: the regex digit-guard (`\d{1,5}`) means `parseInt` can't throw, so
no catch is needed — a valid alternative to rep1's try/catch. `truncate`'s
docstring documents the *real* `(subs s 0 n)` behavior (throws, does not
clamp) — an improvement over baseline rep1's "at most the first n" phrasing,
which implied clamping.
