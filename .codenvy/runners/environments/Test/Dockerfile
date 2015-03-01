# Base image.  You can pull from Docker Hub.  Codenvy
# provides a series of tested base images that include 
# Web Shell, installed utilities, and language support.
# You can browse our images in Docker Hub or at
# github.com/codenvy/dockerfiles. The shellinabox image
# provides core Linux utilities and terminal access to runner.
FROM codenvy/jdk7

# Codenvy uses this port to map IDE clients to the output of
# your application executing within the Runner. Set these
# values to the port of your application and Codenvy will
# map this port to the output within the browser, CLI, and API.
# You can set this value multiple times.
# For example:
# ENV CODENVY_APP_PORT_8080_HTTP 8080
#
# ENV CODENVY_APP_PORT_<port>_HTTP <port>

# Codenvy uses this port to map IDE clients to the debugger
# of your application within the Runner. Set these
# values to the port of your debugger and Codenvy will
# map this port to the debugger console in the browser.
# You can set this value multiple times.
# For example:
# ENV CODENVY_APP_PORT_8000_DEBUG 8000
#
# ENV CODENVY_APP_PORT_<port>_DEBUG <port>

# Set this value to the port of any terminals operating
# within your runner.  If you inherit a base image from
# codenvy/shellinabox (or any of our images that inherit
# from it, you do not need to set this value.  We already 
# set it for you.
# ENV CODENVY_WEB_SHELL_PORT <port>

# Execute your custom commands here.  You can add
# as many RUN commands as you want.  Combining
# RUN commands into a single entry will cause your 
# environment to load faster.  Also, building your image
# with docker offline and uploading it to Docker Hub
# as a pre-built base image will also cause it to load
# Faster.  This example installs python, curl, and the
# Google SDK as an example.
# RUN sudo apt-get update -y && \
#     sudo apt-get install --no-install-recommends -y -q curl build-essential python3 python3-dev python-pip git python3-pip && \
#     sudo pip3 install -U pip && \
#     sudo pip3 install virtualenv && \
#     sudo mkdir /opt/googlesdk && \
#     wget -qO- "https://dl.google.com/dl/cloudsdk/release/google-cloud-sdk.tar.gz" | sudo tar -zx -C /opt/googlesdk && \
#     sudo /bin/sh -c "/opt/googlesdk/google-cloud-sdk/install.sh" && \
#     sudo chmod +x /opt/googlesdk/google-cloud-sdk/bin/gcloud
RUN mkdir /home/user/app
ADD $app$ /home/user/app/$app$
#VOLUME ["/home/user/app"]
#ENV CODENVY_APP_BIND_DIR /home/user/app

RUN cd /home/user/app && \
	unzip libratouploader-jar-with-dependencies.zip && \
   echo 'java -cp ./lib/async-http-client-1.9.10.jar:./lib/commons-cli-1.2.jar:./lib/jackson-core-asl-1.9.7.jar:./lib/jackson-mapper-asl-1.9.7.jar:./lib/librato-java-1.0.7.jar:./lib/netty-3.10.0.final.jar:./lib/slf4j-api-1.6.6.jar:./application.jar uk.co.mentalspace.libratouploader.Main -mp DEV- -src test -lk willinghamg@hotmail.com -lsk e4444b01ac4f5f4e105b920cca2c75d1899d10abe0cd422a86999f030d3302f5 -files findbugs.xml checkstyle.xml ./junit/TESTS-TestSuites.xml' > /home/user/app/run.sh && \
   chmod +777 ./run.sh && \
   mv application.jar application.zip && \
   unzip application.zip && \
   mv application.zip application.jar 
#   ./run.sh

# Include this as the CMD instruction in your Dockerfile if
# you'd like the runner to stay alive after your commands
# have finished executing. Keeping the runner alive is
# necessary if you'd like to terminal into the image.  If 
# your Dockerfile launches a server or daemon, like Tomcat,
# you do not need to set this value as Docker will not
# terminate until that process has finished.
CMD sleep 365d
