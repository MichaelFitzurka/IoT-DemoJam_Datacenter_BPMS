# IoT-DemoJam_Datacenter_BPMS
Internet of Things DemoJam :: Datacenter :: Business Process Management Suite

---
Extracted from [PatrickSteiner/IoT_Demo_Datacenter](https://github.com/PatrickSteiner/IoT_Demo_Datacenter)  
This is one part of the datacenter that I could not convert to OpenShift as it uses "human tasks".  
So for the demo, BPMS is deployed outside of CDK.

Apologies upfront ... This is the most non-elegant part of the project base, in terms of deployment.  I suppose I could have created the docker image and loaded it into my image repository, but as I did not have infinite time on this, it was a basic re-use of Patrick Steiner's demo, with minor changes.  
Truth be told, I actually built the project/docker image above, ssh-ed into the running container, zipped up the psteiner directory, and then pulled it locally.  Don't judge me, it worked. :-/  

The only updates were:  
- Added a few silly buttons, and one semi-serious button at the start of the IoTProcess to deploy the drone.  
- Created a IoT folder for tasks.  
- Updated the code to point to the IoT-DemoJam environment.

Software needed:  
- [JBoss EAP V7.0.0](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=43891)
- [JBoss EAP V7.0.3 Patch](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=47721)
- [JBoss BPM Suite V6.4.0](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=48441)

## Notes:
The "jboss-eap-7.0" folder is just the additions that you need after EAP/BPMS is deployed, and not a full EAP/BPMS folder structure.  Change the "dot_niogit" sub-folder to ".niogit". (Changed the name to make it visible.)

The "LightWorkItemHandler" directory contains the code.  If you make changes, you need to add it back into EAP/BPMS in 2 locations;  
- Copy the jar file here: "jboss-eap-7.0/standalone/deployments/business-central.war/WEB-INF/lib"  
- Delete the repository from here: "jboss-eap-7.0/repositories/kie/com/redhat/demo/iotdemo/LightWorkItemHandler/"  And then upload the latest jar to the repository in BPMS: "Authoring" menu -> "Artifact Repository" menu item -> "Upload" button

I included the Dockerfile from the original project, but that is just for showing the installation instructions.  
1) Unzip/Deploy EAP.  
2) Unzip/Deploy the BPMS process to overwrite into the same directory.  
3) Patch the folder with:  
```sh
jboss-eap-7.0/bin/jboss-cli.sh "patch apply jboss-eap-7.0.3-patch.zip"
```
4) Copy over the files from the project's "jboss-eap-7.0" folder.  (Again, rename "dot_niogit").

## Deploy BPM Process
From [Patrick Steiner](https://github.com/PatrickSteiner/IoT_Demo_AllInOne/blob/master/Readme.adoc#deploy-bpm-process)
> Until now, I have not found an automated way to build and deploy the business process within JBoss BPM Suite. So to do that, you will have to  
> 1) Upload `IoT_Demo_Datacenter/bpm/LightWorkItemHandler/target/lightWorkItemHandler-1.0.0-SNAPSHOT.jar` to the repository of JBoss BPM Suite  
> 2) Build & Deploy the process from the Project Explorer of JBoss BPM Suite.

NOTE: You need to do Step 1 when you change the jar, and Step 2 every time you start the BPMS server.

