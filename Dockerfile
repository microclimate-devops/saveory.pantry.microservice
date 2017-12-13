FROM websphere-liberty:webProfile7
MAINTAINER IBM Java engineering at IBM Cloud
COPY /target/liberty/wlp/usr/servers/defaultServer /config/
#Configure environment
ENV MONGO_USER sapphires
ENV MONGO_PWD saveoryArmory
ENV MONGO_HOST dps-test-cfcmaster.rtp.raleigh.ibm.com
ENV MONGO_PORT 27017
ENV MONGO_DATABASE_NAME saveory-test-db
# Install required features if not present
RUN installUtility install --acceptLicense defaultServer
