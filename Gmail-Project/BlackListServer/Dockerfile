FROM gcc:latest

COPY src /usr/src/blacklist/src
COPY data /usr/src/blacklist/data

WORKDIR /usr/src/blacklist/src

RUN g++ -o blacklist *.cpp

EXPOSE 5555

ENTRYPOINT ["./blacklist"]
CMD ["5555", "1000", "3", "5", "5"]