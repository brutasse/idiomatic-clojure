# Baseline D rep 1 (no skill) — 6/6; combined with rep 2 (2/6): VARIANCE failure

Rep 1 highlights:
- **D1 caught**: expanded `->>` mentally, saw `(merge overrides base)` makes
  base win against the docstring, verified old/new with `{:a 1 :b 2}` +
  `{:b 9 :c 3}`, fixed, and offered the fix-docstring alternative.
- **D2 clean**: `(when (and country phone) (some-> (store/format-phone country
  phone) not-empty (subs 1)))` — nil-tolerant throughout.
- D3 cond->; D4 clean `->>` bare-step chain; D5 transducer into; no
  over-threading.

# D RED SUMMARY
| element | rep1 | rep2 | verdict |
|---|---|---|---|
| D1 merge position trap | ✅ caught | ❌ missed + propagated | guidance warranted |
| D2 nil + interop flatten | ✅ | ❌ crash-on-nil suggestion | guidance warranted |
| D3 cond-> | ✅ | ✅ | already covered (SKILL.md) |
| D4 ->> chain | ✅ | ✅ (with `,,,` teaching-marker shipped as code) | one-line note |
| D5 seq pipeline | ✅ | ✅ | no gap |
| D6 over-threading | ✅ | ✅ | no gap |

Conclusion: the always-known basics need nothing. The **position-is-meaning**
trap and **nil-safety when flattening interop chains** are the tested gaps —
plus the `,,,` artifact. High variance on D1 (1/2 fail) is exactly what a
positive-recipe reference is for.
