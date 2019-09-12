name: Build

on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@master
      - name: Set up JDK 11
        uses: actions/setup-java@v1
        with:
          version: 11
      - name: Login to docker
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: docker login docker.pkg.github.com -u ${GITHUB_ACTOR} || true
      - name: Login to docker
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: docker login docker.pkg.github.com -u ${GITHUB_ACTOR} -p ${GITHUB_TOKEN} || true
      - name: Login to docker
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: docker login docker.pkg.github.com -u navikt -p ${GITHUB_TOKEN} || true
      - name: Login to docker
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: docker login docker.pkg.github.com -u androa -p ${GITHUB_TOKEN} || true
      - name: Login to docker
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: docker login docker.pkg.github.com -u git -p ${GITHUB_TOKEN} || true
      - name: Login to docker
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: docker login docker.pkg.github.com -u github -p ${GITHUB_TOKEN} || true
      - name: Abort the job with a success
        run: true
        #- name: Build with Gradle
        #  run: false && ./gradlew build
        #- name: Build docker image
        #  run: docker build . --tag ${GITHUB_REPOSITORY}:${$GITHUB_SHA}
        #- name: Push docker image
        #  run: |
        #    docker push docker.pkg.github.com/${GITHUB_REPOSITORY}:${$GITHUB_SHA}
