I have a vector of order ids in Clojure. For each id I need to fetch the order
from our HTTP API (`orders.api/get-order`, returns a map) and then insert it
into the database (`db/insert-order!`). Both are side-effecting.

Write a complete Clojure function `fetch-and-store!` that takes the ids and does
this, reporting the inserted orders. Return the full code in your reply.
