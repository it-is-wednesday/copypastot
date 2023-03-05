# so I don't remember why, but the deps.edn clojure image didn't cut it so
# I'm here downloading clj manually like an ape

FROM docker.io/library/eclipse-temurin:11-jdk-jammy

# installing clj (deps.edn)
RUN apt-get update && apt-get install -y rlwrap make
RUN curl -O https://download.clojure.org/install/posix-install-1.11.1.1237.sh
RUN chmod +x posix-install-1.11.1.1237.sh
RUN ./posix-install-1.11.1.1237.sh

# just dependencies
COPY deps.edn /app/
WORKDIR /app
RUN clj -P

# the actual app
COPY Makefile .
COPY resources/ resources
COPY src/ src
COPY db/ db
RUN mkdir -p resources/public/assets
COPY resources/public/js/app.js resources/public/assets/app.js
COPY resources/public/css/app.css resources/public/assets/app.css
CMD ["make", "db/migrate", "server"]
EXPOSE 1337
