FROM navikt/java:13

EXPOSE 50051

COPY dp-inntekt-api/build/libs/dp-inntekt-api-all.jar /app/app.jar