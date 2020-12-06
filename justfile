test:
	COAST_ENV=test clj -A\:test

clean:
	rm -rf target/*

uberjar:
	clj -M:uberjar

lint: find-unused
	clj -M:clj-kondo:eastwood

find-unused:
	#!/bin/bash
	echo looking for unused symbols...
	cd src
	for f in $(egrep -o -R "defn?-? [^ ]*" * --include '*.clj' | cut -d \  -f 2 | sort | uniq); do
		echo $f $(grep -R --include '*.clj' -- "$f" * | wc -l);
	done | grep " 1$" | cut -d " " -f 1
	echo

assets:
	clj -m coast.assets || true
	cp resources/public/js/app.js resources/public/assets/bundle-d41d8cd98f00b204e9800998ecf8427e.js
	mkdir /tmp/copypastot-css-bundle
	purgecss --css resources/public/css/*.css --content src/*.clj --output /tmp/copypastot-css-bundle
	# order is important here, since in app.css we override some bootstrap declarations
	cat /tmp/copypastot-css-bundle/{bootstrap.min.css,app.css} > resources/public/assets/bundle-78f63806f570aaf12622232b4d9db093.css
	rm -r /tmp/copypastot-css-bundle

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

# Local Variables:
# mode: makefile
# End:
# vim: set ft=make :
