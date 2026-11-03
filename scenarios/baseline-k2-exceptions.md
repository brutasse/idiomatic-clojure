# Baseline K2 (no skill) — exceptions + docstrings — 2/2 clean; rows are consistency anchors

Agent: general subagent, fresh session, no skill injected. 2 reps,
2026-11-02. Prompt: `prompt-k2-exceptions.md` (two tasks: `parse-port` —
read a file holding one valid TCP port, throw with the offending content in
the message if invalid; add docstrings to `slugify`/`truncate`).

## Scorecard

| rep | exception type | docstring placement | notes |
|---|---|---|---|
| 1 | `IllegalArgumentException` + `format` | before arg vector, both fns | `re-matches #"\A\d+\z"` guard; `str/trim` |
| 2 | `ex-info` with `{:path :content}` data | before arg vector, both fns | `Integer/parseInteger` + `catch NumberFormatException`; `(<= 1 n 65535)` variadic |

Both reps: no custom exception types, no `DEPRECATED`-style docstring
abuse, no docstring-after-arg-vector (the silent-bug trap).

## Verbatim outputs

Rep 1 (`ses_e6300bb41ffeXONd21W6ETjyAw`):

```clojure
(defn parse-port
  "Read the file at `path`, which should contain a single line with a valid TCP port (1-65535), and return it as an integer.
   Throws an IllegalArgumentException whose message contains the offending content if the file does not contain a valid port."
  [path]
  (let [content (str/trim (slurp path))
        port (when (re-matches #"\A\d+\z" content)
               (Long/parseLong content))]
    (when (or (nil? port) (not (<= 1 port 65535)))
      (throw (IllegalArgumentException.
              (format "Invalid port in %s: %s" path content))))
    (int port)))

(defn slugify
  "Lowercase `s` and replace runs of whitespace with hyphens."
  [s]
  (str/lower-case (str/replace s #"\s+" "-")))

(defn truncate
  "Return at most the first `n` characters of `s`."
  [s n]
  (subs s 0 n))
```

Rep 2 (`ses_e6300bb40ffeskkOsQMd3OmekI`):

```clojure
(defn parse-port
  "Read the file at `path`, which should contain a single line with a valid
   TCP port (1-65535), and return it as an integer.
   Throws an exception whose message includes the offending content if the
   file does not contain a valid port."
  [path]
  (let [content (slurp path)
        n (try (Integer/parseInteger (trim content))
               (catch NumberFormatException _ nil))]
    (when-not (and n (<= 1 n 65535))
      (throw (ex-info (str "Invalid port in " path ": " content)
                      {:path path :content content})))
    n))

(defn slugify
  "Convert `s` to a slug: lowercased, with each run of whitespace replaced
   by a single dash."
  [s]
  (str/lower-case (str/replace s #"\s+" "-")))

(defn truncate
  "Return the first `n` characters of `s`.
   Throws an exception if `s` has fewer than `n` characters."
  [s n]
  (subs s 0 n))
```

## Implications

- No RED failure observed: the conventions rows for K2 (standard exception
  types, `ex-info`, docstring placement, variadic `<`) are **consistency
  anchors**, not fixes — the skill must state them so the behavior holds,
  and the GREEN run checks for no regression, not for repair.
- Both reps independently used the variadic comparison `(<= 1 n 65535)` —
  the idiom row is safe to keep as stated.
