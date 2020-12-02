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
	clj -m coast.assets

server:
	clj -m server

db-migrate:
	clj -m coast.migrations migrate

db-rollback:
	clj -m coast.migrations rollback

db-create:
	clj -m coast.db create

db-drop:
	clj -m coast.db drop

# Local Variables:
# mode: makefile
# End:
# vim: set ft=make :
