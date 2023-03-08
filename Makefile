.PHONY: test

test:
	COAST_ENV=test clj -A\:test

clean:
	rm -rf target/*

uberjar:
	mkdir -p target
	clj -A\:uberjar

lint:
	clj -M:clj-kondo:eastwood

assets:
	clj -M -m coast.assets

server:
	clj -M -m server 0.0.0.0 1337

db/migrate:
	clj -M -m coast.migrations migrate

db/rollback:
	clj -M -m coast.migrations rollback

db/create:
	clj -M -m coast.db create

db/drop:
	clj -M -m coast.db drop

db/delete-duplicates:
	sqlite3 *.sqlite3 "DELETE FROM pasta WHERE id IN (SELECT id FROM pasta GROUP BY substr(content, 0, 200) HAVING count(id) > 1)"
