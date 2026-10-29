# Writing (more) Idiomatic Clojure

An agent skill for writing, reviewing, and refactoring (more) idiomatic
Clojure — the way experienced Clojurists write it: plain data first, small
pure functions composed with core sequence functions, side effects eager and
named with `!` at the edges, open extension via multimethods instead of GoF
ceremony.

Clone this repository:

```
git clone git@github.com:brutasse/idiomatic-clojure.git
```

Symlink the clone directory into `~/.agents/skills/`:

```
cd idiomatic-clojure
ln -s "$PWD" ~/.agents/skills/writing-idiomatic-clojure
```

## Layout

| Path | What it is |
|---|---|
| `SKILL.md` | The skill: generation recipe, house resolutions, Java-in-Clojure red flags. Loaded on Clojure tasks. |
| `references/code-smells.md` | Don't→do catalog, scanned row-by-row when reviewing or refactoring for idiom. |
| `references/threading.md` | Choosing and checking threading macros: selection by argument position, the position-is-meaning trap, nil-chain flattening. Read before threading non-trivially or reviewing threaded code. |
| `references/patterns-in-clojure.md` | GoF patterns mapped to their Clojure core constructs, incl. the N+M rule (two multimethods over a canonical intermediate representation, never N×M pairs). |
| `scenarios/` | Test fixtures, scoring rubrics, and the RED/GREEN baseline + skill-run transcripts. Not part of the skill. |
| `scratch/` | Differential-testing harnesses the test agents built while self-verifying. Not part of the skill. |

## How it was built

Test-first, per my `skill-writing` skill's method: fresh subagents ran the
scenarios **without** the skill (RED), the skill was written to target only
the observed failures, then re-run **with** it (GREEN), and a REFACTOR cycle
closed the last variance found (sort-direction tie order).

The headline finding from baselining: modern agents already know the
bsless-style catalog (`doall`→`mapv`, `mapcat`, `remove`, transducing
`into`…), so this skill deliberately is **not** a tutorial. It binds the
judgment calls models get wrong or apply inconsistently:

- plural wrapper functions — missed 2/2 without the skill, deleted 3/3 with it
- contested rewrites that varied run-to-run (nil-default filling, summing
  strategy, sort tie order) — pinned as house resolutions; converged after
- review thoroughness — reviews must scan the catalog, not rely on recall

Every code snippet in the skill was evaluated against Clojure 1.12.5 over a
throwaway nREPL (`scenarios/verified-snippets.md` is the ledger; it caught
that `remove-vals` is `clojure.set/remove-vals`, not core).

## Sources

The substance is distilled and adapted from:

- Clojure.org — [Threading Macros Guide](https://clojure.org/guides/threading_macros)
  (basis of `references/threading.md`)
- Clojure.org — [Higher Order Functions](https://clojure.org/guides/higher_order_functions)
  (functions-as-arguments / `double-op` generalization, predicate naming, and
  the `#()` readability ceiling rows in `references/code-smells.md`)
- Clojure.org — [Equality](https://clojure.org/guides/equality) and
  [Comparators](https://clojure.org/guides/comparators)
  (basis of the "Value identity" section in `references/code-smells.md` and
  the sort-key tiebreaker clause in SKILL.md)

- Misha Vilyutin — [Clojure Design Patterns](https://mishadoff.com/blog/clojure-design-patterns/)
  (basis of `references/patterns-in-clojure.md`)
- Ben Sless — [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)
  and [Idiomatic Clojure: Mixing Side Effects and Iteration](https://bsless.github.io/side-effects/)
  (basis of `references/code-smells.md` and the strictness rules in `SKILL.md`)
- [life0fun/clojure-idiom](https://github.com/life0fun/clojure-idiom) —
  surveyed as an examples source
- [josephinoo/clojure-idiomatic](https://github.com/josephinoo/clojure-idiomatic) —
  a prior skill on the same topic; read before writing this one, which
  deliberately covers the prescriptive rewrite/judgment layer it lacks
  instead of re-teaching threading basics

## License

MIT — see [LICENSE](LICENSE). The reference tables paraphrase the articles
above; their ideas remain with their authors.
