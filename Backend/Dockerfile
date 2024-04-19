# Use AdoptOpenJDK as a base image
FROM openjdk:11

# Download and install sbt
RUN \
    curl -L https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.tgz -o sbt-1.5.5.tgz && \
    tar -zxvf sbt-1.5.5.tgz && \
    rm sbt-1.5.5.tgz && \
    mv sbt /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

# Set the working directory
WORKDIR /app

# Copy the project files into the container
COPY . .

# Compile the project using sbt
RUN sbt compile

# Define the command to run your application
CMD ["sbt", "run"]
