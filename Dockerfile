FROM clojure:tools-deps
COPY . /app
WORKDIR /app
RUN mkdir -p resources/public/assets
RUN cp resources/public/js/app.js resources/public/assets/app.js
RUN cp resources/public/css/app.css resources/public/assets/app.css
CMD ["clj", "-M:migrate:serve"]
EXPOSE 1337
