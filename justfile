test:
	COAST_ENV=test clj -A\:test

clean:
	rm -rf target/*

uberjar:
	mkdir -p target
	clj -M:uberjar

lint:
	clj -M:clj-kondo:eastwood

assets:
	mkdir -p resources/public/assets
	cp resources/public/js/app.js resources/public/assets/app.js
	cp resources/public/css/app.css resources/public/assets/app.css

serve: db-migrate assets
	clj -m server

db-migrate:
	clj -m coast.migrations migrate

db-rollback:
	clj -m coast.migrations rollback

db-create:
	clj -m coast.db create

db-delete-duplicates:
	sqlite3 *.sqlite3 "DELETE FROM pasta WHERE id IN (SELECT id FROM pasta GROUP BY substr(content, 0, 200) HAVING count(id) > 1)"
