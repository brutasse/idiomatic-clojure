# Fixture F — generation tempting HOF anti-patterns

Delta items probed (rubric in scoring-hof.md): F1 predicate naming, F2
duplicate-op pair generalization, F3 literal readability ceiling, F4
multi-% literal.

## Prompt used

You are answering a colleague's coding question. No files to create — just
answer in your reply with complete code.

Question:

I need a small band-manager library in Clojure. A band is a map like
`{:name "Kowelski" :genre :rock :active? true :members 4 :score 87
:tags [:raw :polish]}`. Write these:

- `top-bands` — takes a collection of bands, returns the active ones with at
  least 3 members and a genre of `:rock` or `:metal`, sorted by `:score`
  (highest first)
- `taglines` — for a collection of bands, a list of tagline strings: the
  lower-cased name, then " :: ", then the band's keyword tags joined by
  " · "
- `zip-taglines` — same taglines, but built from two parallel collections
  (names and tag lists) instead of band maps
- `boost-scores` — a new vector of the bands with each :score multiplied by
  a given factor
- `boost-members` — same, multiplying :members instead
