FROM clojure:tools-deps

# just dependencies
COPY deps.edn /app/deps.edn
WORKDIR /app
RUN mkdir -p target
RUN clj -P

# the actual app
COPY resources/public/js/app.js resources/public/assets/app.js
COPY resources/public/css/app.css resources/public/assets/app.css
COPY src db /app/
# CMD ["clj", "-M:migrate:serve"]
CMD echo hi
EXPOSE 1337
