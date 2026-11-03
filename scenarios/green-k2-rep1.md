# GREEN K2 rep 1 (with skill) — PASS; anchor held + K2 round summary

## Rubric

| item | result |
|---|---|
| standard exception type | ✓ — `IllegalArgumentException` (no custom type) |
| message includes offending content | ✓ — `(str "Invalid port in " path ": " content)` |
| docstring after name, before arg vector | ✓ — all three fns |
| variadic `<` | ✓ — `(<= 1 port 65535)` |
| `str` alias for `clojure.string` | ✓ |
| self-verification | ✓ — "all 18 assertions pass" |

```clojure
(defn parse-port
  "Returns the TCP port (1–65535) contained in the file at `path` as an integer.
   Throws `IllegalArgumentException` if the file does not contain a valid port."
  [path]
  (let [content (str/trim (slurp path))
        port (try (Integer/parseInt content) (catch NumberFormatException _ nil))]
    (if (and port (<= 1 port 65535))
      port
      (throw (IllegalArgumentException. (str "Invalid port in " path ": " content))))))

(defn slugify
  "Returns a lowercase version of `s` with runs of whitespace replaced by a single hyphen."
  [s]
  (str/lower-case (str/replace s #"\s+" "-")))

(defn truncate
  "Returns the first `n` characters of `s`."
  [s n]
  (subs s 0 n))
```

Notes: `Integer/parseInt` + `catch NumberFormatException` (ledger-verified
pattern); docstrings are accurate without being verbose. `truncate`'s
docstring doesn't mention the throw-on-short-string behavior — rep2 did;
both are acceptable, not a rubric item.

---

## K2 cross-rep convergence (both reps in)

| contested call | GREEN rep1 | rep2 |
|---|---|---|
| validation mechanism | `Integer/parseInt` + `catch NumberFormatException` | regex digit-guard `#"^\d{1,5}$"` (no catch needed) |
| exception | `IllegalArgumentException` | `IllegalArgumentException` |
| docstring placement | after name, all 3 fns | after name, all 3 fns |
| variadic range check | `(<= 1 port 65535)` | `(<= 1 n 65535)` |

## K2 ROUND FINAL

| item | RED (baseline) | GREEN |
|---|---|---|
| standard exception types | 2/2 clean | **2/2 clean — no regression** |
| docstring placement (silent-bug trap) | 2/2 clean | **2/2 clean** (rep2 explicitly verified `:doc` landed) |
| variadic `<` | 2/2 | 2/2 |

**VERDICT: GREEN.** K2 was a consistency anchor, not a fix — the baseline
never failed, and the skill kept it that way. Two different validation
mechanisms (try/catch vs regex guard) both landed idiomatic; no rule
change warranted.
