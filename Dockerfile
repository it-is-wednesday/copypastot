FROM clojure:tools-deps
COPY . /app
WORKDIR /app
RUN apt-get update && apt-get install -y sqlite3
RUN clj -M -m coast.migrations create
RUN clj -M -m coast.migrations migrate
RUN mkdir -p resources/public/assets
RUN cp resources/public/js/app.js resources/public/assets/app.js
RUN cp resources/public/css/app.css resources/public/assets/app.css
CMD ["clj", "-M", "-m", "server", "0.0.0.0", "1337"]
EXPOSE 1337
