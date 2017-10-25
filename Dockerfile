FROM psteiner/base

LABEL maintainer "psteiner <psteiner@redhat.com>"

USER root

ENV BPMHOME=jboss-eap-7.0 \
    EAP_PACKAGE=jboss-eap-7.0.0.zip \
    JBOSS_PATCH=jboss-eap-7.0.3-patch.zip \
    BPM_PACKAGE=jboss-bpmsuite-6.4.0.GA-deployable-eap7.x.zip \
    JDG_PACKAGE=jboss-datagrid-7.0.0-eap-modules-library

# transfer JBoss products & config files
ADD Docker_Files/software/ $HOME/tmp/
ADD Docker_Files/config/ $HOME/tmp/
ADD Docker_Files/niogit $HOME/tmp/niogit/
ADD Docker_Files/repositories $HOME/tmp/repositories/
ADD LightWorkItemHandler/target/LightWorkItemHandler-1.0.0-SNAPSHOT.jar $HOME/tmp/

RUN yum -y install java-1.8.0-openjdk java-1.8.0-openjdk-devel

# let the user 'psteiner' own the files
RUN chown -R psteiner $HOME

# continue as 'psteiner'
USER psteiner

# unpack and install BPM
RUN unzip $HOME/tmp/$EAP_PACKAGE -d $HOME && \
    unzip -o $HOME/tmp/$BPM_PACKAGE -d $HOME

# patch JBoss EAP
WORKDIR $HOME/$BPMHOME
RUN ./bin/jboss-cli.sh "patch apply $HOME/tmp/$JBOSS_PATCH"

# copy configurations to their place
RUN mv $HOME/tmp/applic* $HOME/$BPMHOME/standalone/configuration

# copy process to the right place
RUN mv $HOME/tmp/niogit $HOME/.niogit && \
    mv $HOME/tmp/repositories $HOME/repositories

# copy custom WorkItemHandler
RUN mkdir -p $HOME/.m2/repository/com/redhat/demo/iotdemo/LightWorkItemHandler/1.0.0-SNAPSHOT/ && \
    mv $HOME/tmp/LightWorkItemHandler-1.0.0-SNAPSHOT.jar $HOME/$BPMHOME/standalone/deployments/business-central.war/WEB-INF/lib/

# unpack and copy JBoss Data Grid modules
RUN unzip $HOME/tmp/$JDG_PACKAGE.zip -d $HOME/tmp
RUN cp -R $HOME/tmp/$JDG_PACKAGE/modules/* $HOME/$BPMHOME/modules

# cleanup
RUN rm -rf $HOME/tmp/jboss-*

WORKDIR $HOME

# open ports
EXPOSE 8080 9990 9418

# ENTRYPOINT
CMD $HOME/$BPMHOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Dorg.kie.override.deploy.enabled=true  -Djava.security.egd=file:/dev/urandom


# Build command -> docker build --rm -t workspacefuse_iotdatacenterbpm .
# run command -> docker run -d -p 10000:8080 workspacefuse_iotdatacenterbpm
# run and connect -> docker run -p 10000:8080 -i -t workspacefuse_iotdatacenterbpm /bin/bash
