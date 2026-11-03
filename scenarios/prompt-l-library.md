I'm starting a small open-source library for managing music bands. Write the
first version of the main namespace:

- a record `Band` with `:name` (string) and `:genre` (keyword)
- a way to create bands that validates `:name` is a non-empty string
- a public function `loud?` that reports whether a band is loud — a private
  helper decides that metal and punk bands are loud
- a function `shout!` that is deprecated in favour of `loud?` (it still works:
  it uppercases the band's name)
- a namespace docstring

Then write one `deftest` for `loud?` with at least two cases.

Return the namespace and the test.
