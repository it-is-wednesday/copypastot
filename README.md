# copypastot

## Prerequisites
- [Just](https://github.com/casey/just#installation) task runner
- [PurgeCSS](https://purgecss.com/) (`npm install --global purgecss`)
- [Clojure CLI Tools](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools)

## Start

```bash
git clone git@github.com:your-user-name/copypastot.git
cd copypastot
just db-create db-migrate assets serve
```

## Dev

- Evaluate `server.clj` in your REPL
- Run `(server/-main)` in your REPL
- Visit http://localhost:1337 to make sure it's working
- For most changes, re-evaluating the variable will also change the running server. It may not in
  some random cases, so you can restart the server by running `(server/-main)` again

## Ship
Make sure you've 
[defined your `.env`](https://coastonclojure.com/docs/configuration#user-content-production),
then:

```bash
just serve
```
