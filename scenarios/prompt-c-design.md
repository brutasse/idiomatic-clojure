Design a Clojure module for report exporting:

- The system produces different kinds of reports: invoices, statements, and tax
  forms. Over time we will add new kinds.
- Each report kind must be exportable to PDF, CSV, and JSON. Over time we will
  add new formats.
- Adding a new report kind or a new format should not require touching the
  existing ones.

Sketch the design with real Clojure code: how the report data is modeled, how
export dispatch works, and demonstrate extensibility by adding one new report
kind and one new format on top of your base design. Return the full code in
your reply.
